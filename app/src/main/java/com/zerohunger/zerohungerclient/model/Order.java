package com.zerohunger.zerohungerclient.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Order {
    public String clientId;
    public String clientName;
    public String itemId;
    public String itemName;
    public Double price;
    public Long quantity;
    public Long startedTime;

    public Order() {
    }


    public Order(String clientId, String clientName, String itemId, String itemName, Double price, Long quantity, Long startedTime) {
        this.clientId = clientId;
        this.clientName = clientName;
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.quantity = quantity;
        this.startedTime = startedTime;
    }
}
