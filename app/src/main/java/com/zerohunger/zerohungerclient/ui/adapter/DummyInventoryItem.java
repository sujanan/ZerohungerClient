package com.zerohunger.zerohungerclient.ui.adapter;

public class DummyInventoryItem {

    private String name;
    private Double price;
    private Long quantity;

    public DummyInventoryItem() {
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public Long getQuantity() {
        return quantity;
    }
}
