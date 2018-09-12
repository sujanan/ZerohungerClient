package com.zerohunger.zerohungerclient.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zerohunger.zerohungerclient.R;
import com.zerohunger.zerohungerclient.ui.adapter.DummyOrderItem;

public class OrderActivity extends AppCompatActivity implements OrderFragment.OnListFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
    }

    @Override
    public void onListFragmentInteraction(DummyOrderItem item) {
    }
}
