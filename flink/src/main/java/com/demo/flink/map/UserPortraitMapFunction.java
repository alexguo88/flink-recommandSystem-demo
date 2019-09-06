package com.demo.flink.map;

import com.demo.util.HbaseClient;
import com.demo.util.MysqlClient;
import com.demo.domain.LogEntity;
import com.demo.util.LogToEntity;
import org.apache.flink.api.common.functions.MapFunction;

import java.sql.ResultSet;

/**
 *  用户画像处理：
 */
public class UserPortraitMapFunction implements MapFunction<String, String> {
    @Override
    public String map(String s) throws Exception {
        LogEntity log = LogToEntity.getLog(s);
        ResultSet rst = MysqlClient.selectById(log.getProductId());
        if (rst != null){
            while (rst.next()){
                String userId = String.valueOf(log.getUserId());

                HbaseClient.increamColumn("user",userId,"country",rst.getString("country"));
                HbaseClient.increamColumn("user",userId,"color", rst.getString("color"));
                HbaseClient.increamColumn("user",userId,"style",rst.getString("style"));
            }

        }
        return null;
    }
}
