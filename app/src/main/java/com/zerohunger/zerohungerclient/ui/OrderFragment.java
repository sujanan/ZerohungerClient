package com.zerohunger.zerohungerclient.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerohunger.zerohungerclient.R;
import com.zerohunger.zerohungerclient.constants.DatabaseConstants;
import com.zerohunger.zerohungerclient.model.Item;
import com.zerohunger.zerohungerclient.model.Order;
import com.zerohunger.zerohungerclient.ui.adapter.DummyOrderItem;
import com.zerohunger.zerohungerclient.ui.adapter.MyOrderRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class OrderFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private List<DummyOrderItem> mValues;
    private HashMap<String, DummyOrderItem> mValueMap;
    private MyOrderRecyclerViewAdapter mAdapter;
    private DatabaseReference mItemRef;
    private DatabaseReference mOrderRef;

    public OrderFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mValues = new ArrayList<>();
        mValueMap = new HashMap<>();
        initFirebase();
    }

    private void initFirebase() {
        mItemRef = FirebaseDatabase.getInstance().getReference(DatabaseConstants.ITEMS);
        mOrderRef = FirebaseDatabase.getInstance().getReference("orders");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new MyOrderRecyclerViewAdapter(mValues, mListener);
            recyclerView.setAdapter(mAdapter);
            addOrderListener();
        }
        return view;
    }

    private void addOrderListener() {
        mOrderRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                addOrderListener_x(children);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addOrderListener_x(Iterable<DataSnapshot> dataSnapshot) {
        for (DataSnapshot s : dataSnapshot) {
            String uId = FirebaseAuth.getInstance().getUid();
            Iterable<DataSnapshot> onlyChild = s.getChildren();

            for (DataSnapshot ss : onlyChild) {
                String clientId = (String) ss.child("clientId").getValue();
                if (uId == null) {
                    return;
                }
                if (uId.equals(clientId)) {
                    addOrderListener_y(ss);
                }
            }

        }
    }

    private void addOrderListener_y(DataSnapshot dataSnapshot) {
        final Order order = dataSnapshot.getValue(Order.class);

        if (order == null) {
            return;
        }
        Log.d("HELLO: ", order.itemId);
        mItemRef.child(order.itemId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Item item = dataSnapshot.getValue(Item.class);
                DummyOrderItem orderItem = new DummyOrderItem();

                orderItem.setItemName(item.name);
                orderItem.setPrice(order.price);
                orderItem.setQuantity(order.quantity);

                mValues.add(orderItem);
                mValueMap.put(order.itemId, orderItem);

                int index = mValues.size() - 1;

                mAdapter.notifyItemInserted(index);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(DummyOrderItem item);
    }
}
