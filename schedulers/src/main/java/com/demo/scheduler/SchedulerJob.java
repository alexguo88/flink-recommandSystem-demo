package com.demo.scheduler;

import com.demo.scheduler.coeff.ItemCfCoeff;
import com.demo.scheduler.coeff.ProductCoeff;
import com.demo.util.HbaseClient;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 每12小时定时调度一次 基于两个推荐策略的 产品评分计算
 * 策略1 ：协同过滤
 * <p>
 * 数据写入Hbase表  px
 * <p>
 * 策略2 ： 基于产品标签 计算产品的余弦相似度
 * <p>
 * 数据写入Hbase表 ps
 */
public class SchedulerJob {

    //线程池调度器
    static ExecutorService executorService = Executors.newFixedThreadPool(10);


    public static void main(String[] args) {
        //		ScheduledExecutorService pool = new ScheduledThreadPoolExecutor(5);
        Timer qTimer = new Timer();
        qTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                System.out.println(new Date() + " 开始执行任务！");

                /**
                 * 取出被用户点击过的产品id，getAllKey只是一个示例，真实情况不太可能把所有的产品取出来
                 *
                 */
                List<String> allProId = new ArrayList<>();
                try {
                    allProId = HbaseClient.getAllKey("p_history");
                } catch (IOException e) {
                    System.err.println("获取历史产品id异常: " + e.getMessage());
                    e.printStackTrace();
                    return;
                }
                /**
                 * 可以考虑任务执行前是否需要把历史记录删掉
                 */
                for (String id : allProId) {
                    // 每12小时调度一次
                    executorService.execute(new Task(id, allProId));
                }
            }

        }, 0, 15 * 1000);// 定时每15分钟


    }


    // 线程任务
    private static class Task implements Runnable {

        private String id;
        private List<String> others;

        public Task(String id, List<String> others) {
            this.id = id;
            this.others = others;
        }


        ItemCfCoeff item = new ItemCfCoeff(); // 协同过滤
        ProductCoeff prod = new ProductCoeff(); // 基于产品标签 计算产品的余弦相似度


        @Override
        public void run() {
            try {
                item.getSingelItemCfCoeff(id, others);
                prod.getSingelProductCoeff(id, others);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }


}
