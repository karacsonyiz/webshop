package com.training360.yellowcode.dbTables;

public class Dashboard {
    private long userCount;
    private long activeProductCount;
    private long productCount;
    private long activeOrderCount;
    private long orderCount;

    public Dashboard(long userCount, long activeProductCount, long productCount, long activeOrderCount, long orderCount) {
        this.userCount = userCount;
        this.activeProductCount = activeProductCount;
        this.productCount = productCount;
        this.activeOrderCount = activeOrderCount;
        this.orderCount = orderCount;
    }

    public Dashboard() {
    }

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getActiveProductCount() {
        return activeProductCount;
    }

    public void setActiveProductCount(long activeProductCount) {
        this.activeProductCount = activeProductCount;
    }

    public long getProductCount() {
        return productCount;
    }

    public void setProductCount(long productCount) {
        this.productCount = productCount;
    }

    public long getActiveOrderCount() {
        return activeOrderCount;
    }

    public void setActiveOrderCount(long activeOrderCount) {
        this.activeOrderCount = activeOrderCount;
    }

    public long getOrderCount() {
        return orderCount;
    }

    public void setOrderCount(long orderCount) {
        this.orderCount = orderCount;
    }
}
