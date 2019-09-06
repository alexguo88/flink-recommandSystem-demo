package com.demo.scheduler.coeff;

import com.demo.util.HbaseClient;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * 基于协同过滤的产品相关度计算
 *
 * * 策略1 ：协同过滤
 *      *           abs( i ∩ j)
 *      *      w = ——————————————
 *      *           sqrt(i || j)
 *
 */
public class ItemCfCoeff {

    /**
     * 计算一个产品和其他相关产品的评分,并将计算结果放入Hbase
     *
     * @param id     产品id
     * @param others 其他产品的id
     */
    public void getSingelItemCfCoeff(String id, List<String> others) throws Exception {

        for (String other : others) {
        	if(id.equals(other)) continue;
            Double score = twoItemCfCoeff(id, other); //计算两个产品相似度

            //保存结果
            HbaseClient.putData("px",id, "p",other,score.toString());
        }
    }

    //-----------------------------------

    /**
     * 计算两个产品之间的相似度评分
     * @param id
     * @param other
     * @return
     * @throws IOException
     */
    private Double twoItemCfCoeff(String id, String other) throws IOException {
        List<Map.Entry> p1 = HbaseClient.getRow("p_history", id);
        List<Map.Entry> p2 = HbaseClient.getRow("p_history", other);

        //计算sqrt(i || j)
        int n = p1.size();
        int m = p2.size();
        Double total = Math.sqrt(n * m);
        if (total == 0){ //除数不能为0
            return 0.0;
        }

        //计算abs( i ∩ j) (用户id相同的个数)
        int sum = 0;
        for (Map.Entry entry : p1) {
            String key = (String) entry.getKey();
            for (Map.Entry p : p2) {
                if (key.equals(p.getKey())) {
                    sum++;
                }
            }
        }

        //相除
        return sum/total;

    }


}
