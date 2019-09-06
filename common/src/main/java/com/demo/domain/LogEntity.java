package com.demo.domain;

/**
 * 日志对象
 */
public class LogEntity {

    private int userId;
    private int productId;
    private Long time;
    private String action; //1 -> 浏览  2 -> 分享  3 -> 购物

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
