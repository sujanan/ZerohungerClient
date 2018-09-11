package com.zerohunger.zerohungerclient.ui;

import com.google.android.gms.maps.model.Marker;

public interface MarkerUpdateStrategy {

    void update(Marker marker, String reference);

    void remove();
}
