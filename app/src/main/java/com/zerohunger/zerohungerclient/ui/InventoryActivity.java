package com.zerohunger.zerohungerclient.ui;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.zerohunger.zerohungerclient.R;
import com.zerohunger.zerohungerclient.model.Order;
import com.zerohunger.zerohungerclient.ui.InventoryItemFragment.OnListFragmentInteractionListener;
import com.zerohunger.zerohungerclient.ui.adapter.DummyInventoryItem;
import com.zerohunger.zerohungerclient.ui.adapter.MyInventoryItemRecyclerViewAdapter;

public class InventoryActivity extends AppCompatActivity implements OnListFragmentInteractionListener {

    private DummyInventoryItem mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        FloatingActionButton doOrder = findViewById(R.id.do_order);

        doOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mItem == null) {
                    return;
                }
                makeTheOrder();
            }
        });
    }

    private void makeTheOrder() {
        String traderId;
        String clientId;
        String clientName;
        String itemId;
        String itemnName;
        Double price;
        Long quantity;
        Long startedTime;

        traderId = mItem.getTraderId();
        itemId = mItem.getId();
        itemnName = mItem.getName();
        price = mItem.getPrice();
        quantity = MyInventoryItemRecyclerViewAdapter.qqq;
        clientId = FirebaseAuth.getInstance().getUid();
        clientName = MainActivity.userName;
        startedTime = System.currentTimeMillis() / 1000L;

        Order order = new Order(
                clientId,
                clientName,
                itemId,
                itemnName,
                price,
                quantity,
                startedTime);

        FirebaseDatabase.getInstance().getReference("orders").child(traderId).push().setValue(order);
        finish();
    }

    @Override
    public void onListFragmentInteraction(DummyInventoryItem item) {
        mItem = item;
    }
}
