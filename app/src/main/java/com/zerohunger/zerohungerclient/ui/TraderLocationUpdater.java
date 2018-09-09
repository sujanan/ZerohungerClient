package com.zerohunger.zerohungerclient.ui;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.zerohunger.zerohungerclient.model.TraderLocation;

import java.util.List;
import java.util.Observable;

public class TraderLocationUpdater extends Observable {

    private static final String DATABASE_REFERENCE = "traderLocations";

    GenericTypeIndicator<List<TraderLocation>> genericTypeIndicator;

    public TraderLocationUpdater() {
        genericTypeIndicator = new GenericTypeIndicator<List<TraderLocation>>();

        FirebaseDatabase.getInstance().getReference(DATABASE_REFERENCE)
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    TraderLocationWrapper wrapper =
                            new TraderLocationWrapper(
                                    dataSnapshot.getValue(genericTypeIndicator));
                    notifyObservers(wrapper);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
    }
}
