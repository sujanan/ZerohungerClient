package com.zerohunger.zerohungerclient.ui.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zerohunger.zerohungerclient.R;
import com.zerohunger.zerohungerclient.ui.InventoryItemFragment.OnListFragmentInteractionListener;

import java.util.List;

public class MyInventoryItemRecyclerViewAdapter extends RecyclerView.Adapter<MyInventoryItemRecyclerViewAdapter.ViewHolder> {

    private final OnListFragmentInteractionListener mListener;
    private List<DummyInventoryItem> mValues;

    public MyInventoryItemRecyclerViewAdapter(List<DummyInventoryItem> values, OnListFragmentInteractionListener listener) {
        mListener = listener;
        mValues = values;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.fragment_inventoryitem,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mValues == null) {
            return;
        }
        holder.mItem = mValues.get(position);
        holder.mItemName.setText(mValues.get(position).getName());
        holder.mItemPrice.setText("Price: $" + mValues.get(position).getPrice().toString());
        holder.mItemQuantity.setText("Available: " + mValues.get(position).getQuantity().toString());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mValues == null) {
            return 0;
        }
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mItemName;
        public final TextView mItemPrice;
        public final TextView mItemQuantity;
        public DummyInventoryItem mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mItemName = view.findViewById(R.id.item_name);
            mItemPrice = view.findViewById(R.id.item_price);
            mItemQuantity = view.findViewById(R.id.item_quantity);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
