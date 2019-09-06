package com.demo.flink.map;

import com.demo.util.HbaseClient;
import com.demo.util.MysqlClient;
import com.demo.domain.LogEntity;
import com.demo.util.AgeUtil;
import com.demo.util.LogToEntity;
import org.apache.flink.api.common.functions.MapFunction;

import java.sql.ResultSet;

/**
 * 给产品画像
 *
 */
public class ProductPortraitMapFunction implements MapFunction<String, String> {
    @Override
    public String map(String s) throws Exception {
        LogEntity log = LogToEntity.getLog(s);


        ResultSet rs_User = MysqlClient.selectUserById(log.getUserId()); //查找用户
        if (rs_User != null){
            while (rs_User.next()){
                String productId = String.valueOf(log.getProductId());

                String sex = rs_User.getString("sex");
                HbaseClient.increamColumn("prod",productId,"sex",sex); //产品的性别特征

                String age = rs_User.getString("age");
                HbaseClient.increamColumn("prod",productId,"age", AgeUtil.getAgeType(age)); //产品的年龄特征
            }
        }
        return null;
    }
}
