package com.zerohunger.zerohungerclient.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.zerohunger.zerohungerclient.constants.MapConstants;
import com.zerohunger.zerohungerclient.model.Inventory;

import java.util.HashMap;
import java.util.Map;

public class InventoryStateUpdater {

    private GoogleMap map;
    private DatabaseReference database;
    private ChildEventListener childEventListener;
    private HashMap<String, Marker> markerMap;

    public InventoryStateUpdater(final GoogleMap map) {
        this.map = map;
        markerMap = new HashMap<>();
        database = FirebaseDatabase.getInstance().getReference().child("inventory");

        childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                addMarker(dataSnapshot.getKey(), dataSnapshot.getValue(Inventory.class));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                String s = dataSnapshot.getKey();
                removeMarker(s);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public void startReading() {
        database.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                initializeMarkerList(dataSnapshot);
                database.addChildEventListener(childEventListener);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initializeMarkerList(DataSnapshot dataSnapshot) {
        GenericTypeIndicator<HashMap<String, Inventory>> type =
                new GenericTypeIndicator<HashMap<String, Inventory>>() {};
        Map<String, Inventory> inventoryMap = dataSnapshot.getValue(type);

        if (inventoryMap == null) {
            return;
        }
        for (Map.Entry<String, Inventory> entry : inventoryMap.entrySet()) {
            addMarker(entry.getKey(), entry.getValue());
        }
    }

    private void addMarker(String id, Inventory inventory) {
        if (map == null) {
            return;
        }
        LatLng latLng;
        Marker marker;

        latLng = new LatLng(inventory.lat, inventory.lng);
        marker = map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(inventory.traderId)
                .icon(BitmapDescriptorFactory.fromResource(MapConstants.MARKER)));
        markerMap.put(id, marker);
    }

    private void removeMarker(String id) {
        if (map == null) {
            return;
        }
        Marker marker;

        marker = markerMap.get(id);
        marker.remove();
    }

    public void stopReading() {
        database.removeEventListener(childEventListener);
    }
}
