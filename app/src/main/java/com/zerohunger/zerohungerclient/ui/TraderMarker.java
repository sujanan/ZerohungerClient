package com.zerohunger.zerohungerclient.ui;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.zerohunger.zerohungerclient.model.TraderLocation;

import java.util.Observable;
import java.util.Observer;

public class TraderMarker implements Observer {

    private String traderId;
    private Marker marker;

    public TraderMarker(String traderId, Marker marker) {
        this.traderId = traderId;
        this.marker = marker;
    }

    @Override
    public void update(Observable o, Object arg) {
        TraderLocation location;
        TraderLocationWrapper wrapper;

        if (arg instanceof TraderLocationWrapper) {
            wrapper = (TraderLocationWrapper) arg;
        } else {
            return;
        }
        location = wrapper.getLocation(traderId);
        if (location != null) {
            marker.setPosition(
                    new LatLng(
                            Float.parseFloat(location.lat),
                            Float.parseFloat(location.lng)
                    )
            );
        }
    }
}
