package com.training360.yellowcode.dbTables;

public class OrderItem {

    private long id;
    private long orderId;
    private long productId;
    private String productName;
    private String productAddress;
    private String producer;
    private long productPrice;
    private long quantity;

    public OrderItem(long id, long orderId, long productId, String productName, String productAddress,
                     String producer, long productPrice, long quantity) {
        this.id = id;
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productAddress = productAddress;
        this.producer = producer;
        this.productPrice = productPrice;
        this.quantity = quantity;
    }

    public OrderItem(long orderId, long productId, String productName, String productAddress, String producer, long productPrice) {
        this.orderId = orderId;
        this.productId = productId;
        this.productName = productName;
        this.productAddress = productAddress;
        this.producer = producer;
        this.productPrice = productPrice;
    }

    public OrderItem() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductAddress() {
        return productAddress;
    }

    public void setProductAddress(String productAddress) {
        this.productAddress = productAddress;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public long getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(long productPrice) {
        this.productPrice = productPrice;
    }

    public long getQuantity() {
        return quantity;
    }

    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }
}
