package com.zerohunger.zerohungerclient.ui;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerohunger.zerohungerclient.constants.DatabaseConstants;
import com.zerohunger.zerohungerclient.model.Item;

public class ItemMarkerUpdate implements MarkerUpdateStrategy {

    private DatabaseReference root;
    private ValueEventListener eventListener;

    @Override
    public void update(final Marker marker, String reference) {
        root = FirebaseDatabase.getInstance().getReference(DatabaseConstants.ITEMS).child(reference);
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Item item = dataSnapshot.getValue(Item.class);
                if (item == null) {
                    return;
                }
                String snippet = marker.getSnippet();
                marker.setSnippet(item.name + ": " + snippet);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
        root.addValueEventListener(eventListener);
    }

    @Override
    public void remove() {
        if (root == null || eventListener == null) {
            return;
        }
        root.removeEventListener(eventListener);
    }
}
