package com.demo.util;

import com.demo.domain.LogEntity;

/**
 *  日志字符串转日志实体
 */
public class LogToEntity {

    public static LogEntity getLog(String s){
        //System.out.println(s);

        String[] values = s.split(",");
        if (values.length < 2) {
            System.out.println("Message is not correct");
            return null;
        }

        LogEntity log = new LogEntity();
        log.setUserId(Integer.parseInt(values[0])); //用户编号
        log.setProductId(Integer.parseInt(values[1])); //产品编号
        log.setTime(Long.parseLong(values[2])); //时间
        log.setAction(values[3]); //操作：

        return log;
    }
}
