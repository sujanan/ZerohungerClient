package com.zerohunger.zerohungerclient.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zerohunger.zerohungerclient.R;
import com.zerohunger.zerohungerclient.ui.InventoryItemFragment.OnListFragmentInteractionListener;
import com.zerohunger.zerohungerclient.ui.adapter.DummyInventoryItem;

public class InventoryActivity extends AppCompatActivity implements OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
    }

    @Override
    public void onListFragmentInteraction(DummyInventoryItem item) {
    }
}
