package com.demo.domain;

import java.util.Arrays;

/**
 * 车联网，物联网，医院HIS，OA，商城等高端代码获取或者代码有疑问，加wx：17725354261，技术群：256860212，今日头条：大数据java架构师，公众号：前劲科技
 */
public class UserScoreEntity {
    private String userId;
    private Double[] color;
    private Double[] country;
    private Double[] style;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Double[] getColor() {
        return color;
    }

    public void setColor(Double[] color) {
        this.color = color;
    }

    public Double[] getCountry() {
        return country;
    }

    public void setCountry(Double[] country) {
        this.country = country;
    }

    public Double[] getStyle() {
        return style;
    }

    public void setStyle(Double[] style) {
        this.style = style;
    }

    @Override
    public String toString() {
        return "UserScoreEntity{" +
                "userId='" + userId + '\'' +
                ", color=" + Arrays.toString(color) +
                ", country=" + Arrays.toString(country) +
                ", style=" + Arrays.toString(style) +
                '}';
    }
}
