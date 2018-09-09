package com.zerohunger.zerohungerclient.model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class TraderLocation {

    public String traderId;
    public String lat;
    public String lng;

    public TraderLocation() {
    }

    public TraderLocation(String traderId, String lat, String lng) {
        this.traderId = traderId;
        this.lat = lat;
        this.lng = lng;
    }
}
