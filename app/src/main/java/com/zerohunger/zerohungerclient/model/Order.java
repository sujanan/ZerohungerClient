package com.zerohunger.zerohungerclient.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Order {
    public String clientId;
    public String itemId;
    public Double price;
    public Long quantity;
    public Long startedTime;

    public Order() {
    }

    public Order(String clientId, String itemId, Double price, Long quantity, Long startedTime) {
        this.clientId = clientId;
        this.itemId = itemId;
        this.price = price;
        this.quantity = quantity;
        this.startedTime = startedTime;
    }
}
