package com.training360.yellowcode.dbTables;

public class Category {
    private long id;
    private String name;
    private Long positionNumber;

    public Category(long id, String name, Long positionNumber) {
        this.id = id;
        this.name = name;
        this.positionNumber = positionNumber;
    }

    public Category(String name, Long positionNumber) {
        this.name = name;
        this.positionNumber = positionNumber;
    }

    public Category() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getPositionNumber() {
        return positionNumber;
    }

    public void setPositionNumber(Long positionNumber) {
        this.positionNumber = positionNumber;
    }
}
