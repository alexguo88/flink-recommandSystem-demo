
package com.demo.flink.map;

import com.demo.util.HbaseClient;
import com.demo.domain.LogEntity;
import org.apache.flink.api.common.time.Time;
import org.apache.flink.api.common.functions.RichMapFunction;
import org.apache.flink.api.common.state.StateTtlConfig;
import org.apache.flink.api.common.state.ValueState;
import org.apache.flink.api.common.state.ValueStateDescriptor;
import org.apache.flink.configuration.Configuration;

import java.io.Serializable;

/**
 * 用户喜好任务（）
 */
public class UserInterestMapFunction extends RichMapFunction<LogEntity, String> {

    ValueState<Action> state; //状态

    @Override
    public void open(Configuration parameters) throws Exception {

        // 设置 state 的过期时间为100s
        StateTtlConfig ttlConfig = StateTtlConfig
                .newBuilder(Time.seconds(100L))
                .setUpdateType(StateTtlConfig.UpdateType.OnCreateAndWrite)
                .setStateVisibility(StateTtlConfig.StateVisibility.NeverReturnExpired)
                .build();

        //新建一个状态
        ValueStateDescriptor<Action> desc = new ValueStateDescriptor<>("Action time", Action.class);
        desc.enableTimeToLive(ttlConfig);//设置状态过期时间

        state = getRuntimeContext().getState(desc);
    }

    @Override
    public String map(LogEntity logEntity) throws Exception {
        Action actionLastTime = state.value(); //用户上次动作

        Action actionThisTime = new Action(logEntity.getAction(), logEntity.getTime().toString()); //用户本次动作


        int times = 1;


        if (actionLastTime == null) { // 如果用户没有操作 则为state创建值，保存到hbase
            //actionLastTime = actionThisTime;
            //saveToHBase(logEntity, 1);
            state.update(actionThisTime);
        } else { //否则，修改状态
            times = getTimesByRule(actionLastTime, actionThisTime);
        }


        saveToHBase(logEntity, times);

        // 如果用户的操作为3(购物),则清除这个key的state
        if (actionThisTime.getType().equals("3")) {
            state.clear();
        }
        return null;
    }

    //修改用户偏好数据
    private int getTimesByRule(Action actionLastTime, Action actionThisTime) {
        // 动作主要有3种类型
        // 1 -> 浏览  2 -> 分享  3 -> 购物
        int a1 = Integer.parseInt(actionLastTime.getType());
        int a2 = Integer.parseInt(actionThisTime.getType());


        int t1 = Integer.parseInt(actionLastTime.getTime());
        int t2 = Integer.parseInt(actionThisTime.getTime());

        int pluse = 1;

        // 如果动作连续发生且时间很短(小于100秒内完成动作), 则标注为用户对此产品兴趣度很高
        if (a2 > a1 && (t2 - t1) < 100_000L) {
            pluse *= a2 - a1;
        }
        return pluse;
    }

    //保存状态到hbase
    private void saveToHBase(LogEntity log, int times) throws Exception {
        if (log != null) {
            for (int i = 0; i < times; i++) {
                HbaseClient.increamColumn("u_interest", String.valueOf(log.getUserId()), "p", String.valueOf(log.getProductId()));
            }

        }

    }

}

/**
 * 动作类 记录动作类型和动作发生时间(Event Time)
 */
class Action implements Serializable {

    private String type;
    private String time;

    public Action(String type, String time) {
        this.type = type;
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}