package com.demo.flink.map;

import com.demo.domain.LogEntity;
import com.demo.util.LogToEntity;
import org.apache.flink.api.common.functions.MapFunction;

/**
 *
 */
public class TopProductMapFunction implements MapFunction<String, LogEntity> {
    @Override
    public LogEntity map(String s) throws Exception {
        LogEntity log = LogToEntity.getLog(s);
        return log;
    }
}
