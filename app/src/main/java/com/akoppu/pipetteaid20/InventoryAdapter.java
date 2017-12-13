package com.akoppu.pipetteaid20;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by cngos on 12/1/2017.
 */

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.ViewHolder> {

    private ArrayList<Pair<String, Integer>> items;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv1, tv2;
        public ViewHolder(View v) {
            super(v);
            this.tv1 = v.findViewById(R.id.inventory_item_name);
            this.tv2 = v.findViewById(R.id.quantity);
        }
    }

    public InventoryAdapter(ArrayList<Pair<String, Integer>> items) {
        this.items = items;
    }

    @Override
    public InventoryAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.inventory_list_card_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(InventoryAdapter.ViewHolder holder, int position) {
        holder.tv1.setText(items.get(position).first);
        holder.tv2.setText(items.get(position).second.toString());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
