package com.akoppu.pipetteaid20;

/**
 * Created by cngos on 11/15/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class ProtocolListAdapter extends RecyclerView.Adapter<ProtocolListAdapter.ViewHolder> {

    private ArrayList<Semiprotocol> sp;
    private ArrayList<String> id;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tv;
        Button b;
        public ViewHolder(View v) {
            super(v);
            this.tv = v.findViewById(R.id.list_item_name);
            this.b = v.findViewById(R.id.startProtocol);
        }
    }

    public ProtocolListAdapter(ArrayList<Semiprotocol> sp, ArrayList<String> id, Context c) {
        this.sp = sp;
        this.id = id;
        this.context = c;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.protocol_list_card_view, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.tv.setText(sp.get(position).getName());
        holder.b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, LabBenchActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.putExtra("ID", id.get(holder.getAdapterPosition()));
                context.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return sp.size();
    }
}
