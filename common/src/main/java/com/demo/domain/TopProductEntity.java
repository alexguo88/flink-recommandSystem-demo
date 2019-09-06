package com.demo.domain;

/**
 * 车联网，物联网，医院HIS，OA，商城等高端代码获取或者代码有疑问，加wx：17725354261，技术群：256860212，今日头条：大数据java架构师，公众号：前劲科技
 */
public class TopProductEntity {

    private int productId;
    private int actionTimes;
    private long windowEnd;
    private String rankName;

    public static TopProductEntity of(Integer itemId, long end, Long count) {
        TopProductEntity res = new TopProductEntity();
        res.setActionTimes(count.intValue());
        res.setProductId(itemId);
        res.setWindowEnd(end);
        res.setRankName(String.valueOf(end));
        return res;
    }

    public String getRankName() {
        return rankName;
    }

    public long getWindowEnd() {
        return windowEnd;
    }

    public void setWindowEnd(long windowEnd) {
        this.windowEnd = windowEnd;
    }

    public void setRankName(String rankName) {
        this.rankName = rankName;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getActionTimes() {
        return actionTimes;
    }

    public void setActionTimes(int actionTimes) {
        this.actionTimes = actionTimes;
    }

    @Override
    public String toString() {
        return "TopProductEntity{" +
                "productId=" + productId +
                ", actionTimes=" + actionTimes +
                ", windowEnd=" + windowEnd +
                ", rankName='" + rankName + '\'' +
                '}';
    }
}
