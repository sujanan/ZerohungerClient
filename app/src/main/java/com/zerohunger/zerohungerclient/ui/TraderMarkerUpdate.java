package com.zerohunger.zerohungerclient.ui;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerohunger.zerohungerclient.constants.DatabaseConstants;
import com.zerohunger.zerohungerclient.model.User;

public class TraderMarkerUpdate implements MarkerUpdateStrategy {

    private DatabaseReference root;
    private ValueEventListener eventListener;

    @Override
    public void update(final Marker marker, String reference) {
        root = FirebaseDatabase.getInstance().getReference(DatabaseConstants.USERS).child(reference);
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user == null) {
                    return;
                }
                marker.setTitle(user.firstName + " " + user.lastName);
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
