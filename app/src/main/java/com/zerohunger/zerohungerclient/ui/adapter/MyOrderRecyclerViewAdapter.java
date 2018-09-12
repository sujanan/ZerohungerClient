package com.zerohunger.zerohungerclient.ui.adapter;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zerohunger.zerohungerclient.R;
import com.zerohunger.zerohungerclient.ui.OrderFragment.OnListFragmentInteractionListener;

import org.w3c.dom.Text;

import java.util.List;


public class MyOrderRecyclerViewAdapter extends RecyclerView.Adapter<MyOrderRecyclerViewAdapter.ViewHolder> {

    private List<DummyOrderItem> mValues;
    private OnListFragmentInteractionListener mListener;

    public MyOrderRecyclerViewAdapter(List<DummyOrderItem> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_order, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.itemName.setText(mValues.get(position).getItemName());
        holder.price.setText("Price: $" + mValues.get(position).getPrice().toString());
        holder.quantity.setText("Quantity: " + mValues.get(position).getQuantity().toString());

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
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public DummyOrderItem mItem;
        public TextView itemName;
        public TextView price;
        public TextView quantity;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            itemName = view.findViewById(R.id.order_item_name);
            price = view.findViewById(R.id.order_item_price);
            quantity = view.findViewById(R.id.order_item_quantity);
        }

        @Override
        public String toString() {
            return super.toString();
        }
    }
}
