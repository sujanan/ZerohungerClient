package com.zerohunger.zerohungerclient.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Inventory {
    public String traderId;
    public String itemId;
    public Double price;
    public Long quantity;
    public Long startedTime;
    public Double lat;
    public Double lng;

    public Inventory() {
    }

    public Inventory(
            String traderId,
            String itemId,
            Double price,
            Long quantity,
            Long startedTime,
            Double lat,
            Double lng)
    {
        this.traderId = traderId;
        this.itemId = itemId;
        this.price = price;
        this.quantity = quantity;
        this.startedTime = startedTime;
        this.lat = lat;
        this.lng = lng;
    }
}
