package com.zerohunger.zerohungerclient.ui;

import com.zerohunger.zerohungerclient.model.TraderLocation;

import java.util.List;

public class TraderLocationWrapper {

    private List<TraderLocation> traderLocations;

    public TraderLocationWrapper(List<TraderLocation> traderLocations) {
        this.traderLocations = traderLocations;
    }

    public TraderLocation getLocation(String id) {
        for (TraderLocation location : traderLocations) {
            if (id.equals(location.traderId)) {
                return location;
            }
        }

        return null;
    }
}
