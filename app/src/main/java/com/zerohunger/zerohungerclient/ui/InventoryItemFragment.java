package com.zerohunger.zerohungerclient.ui;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.zerohunger.zerohungerclient.R;
import com.zerohunger.zerohungerclient.constants.CommonConstants;
import com.zerohunger.zerohungerclient.constants.DatabaseConstants;
import com.zerohunger.zerohungerclient.model.Inventory;
import com.zerohunger.zerohungerclient.model.Item;
import com.zerohunger.zerohungerclient.ui.adapter.DummyInventoryItem;
import com.zerohunger.zerohungerclient.ui.adapter.MyInventoryItemRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InventoryItemFragment extends Fragment {

    private OnListFragmentInteractionListener mListener;
    private String mInventoryRefId;
    private List<DummyInventoryItem> mValues;
    private HashMap<String, DummyInventoryItem> mValueMap;
    private MyInventoryItemRecyclerViewAdapter mAdapter;
    private DatabaseReference mDatabaseInventory;
    private DatabaseReference mDatabaseItems;

    public InventoryItemFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mValues = new ArrayList<>();
        mValueMap = new HashMap<>();
        mInventoryRefId = getActivity().getIntent().getStringExtra(CommonConstants.EXTRA_INVENTORY_ACTIVITY);
        initFirebase();
    }

    private void initFirebase() {
        mDatabaseInventory = FirebaseDatabase
                .getInstance()
                .getReference(DatabaseConstants.INVENTORIES)
                .child(mInventoryRefId);
        mDatabaseItems = FirebaseDatabase
                .getInstance()
                .getReference(DatabaseConstants.ITEMS);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_inventoryitem_list, container, false);

        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            mAdapter = new MyInventoryItemRecyclerViewAdapter(mValues, mListener);
            recyclerView.setAdapter(mAdapter);
            addInventoryListener();
        }
        return view;
    }

    private void addInventoryListener() {
        mDatabaseInventory.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final Inventory inventory = dataSnapshot.getValue(Inventory.class);
                DummyInventoryItem item = mValueMap.get(inventory.itemId);

                if (item == null) {
                    mDatabaseItems.child(inventory.itemId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            Item i = dataSnapshot.getValue(Item.class);
                            DummyInventoryItem newItem = new DummyInventoryItem();

                            newItem.setName(i.name);
                            newItem.setPrice(inventory.price);
                            newItem.setQuantity(inventory.quantity);
                            mValues.add(newItem);
                            mValueMap.put(inventory.itemId, newItem);

                            int index = mValues.size() - 1;

                            mAdapter.notifyItemInserted(index);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                } else {
                    int index = mValues.indexOf(item);

                    item.setQuantity(inventory.quantity);
                    item.setPrice(inventory.price);
                    mAdapter.notifyItemChanged(index);
                }
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
        void onListFragmentInteraction(DummyInventoryItem mItem);
    }
}
