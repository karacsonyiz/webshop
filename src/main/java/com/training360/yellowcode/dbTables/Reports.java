package com.training360.yellowcode.dbTables;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.Locale;

public class Reports {
    private long totalPrice;
    private String date;
    private int month;
    private OrderStatus status;
    private String productName;
    private long productCount;
    private long productPrice;

    public Reports(long totalPrice, LocalDateTime date, OrderStatus status,long productCount) {
        this.totalPrice = totalPrice;
        this.date = date.getMonth().getDisplayName(TextStyle.FULL,new Locale("HU"));
        this.status = status;
        this.productCount = productCount;
    }

    public Reports(String productName,int month,long productCount,long productPrice,long totalPrice) {
        this.month = month;
        this.productName = productName;
        this.productCount = productCount;
        this.productPrice = productPrice;
        this.totalPrice = totalPrice;
    }

    public Reports() {
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(long totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public long getProductCount() {
        return productCount;
    }

    public void setProductCount(long productCount) {
        this.productCount = productCount;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }
}
