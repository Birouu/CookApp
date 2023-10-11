package com.example.kouizine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;

public class ItemOrderAdapter extends RecyclerView.Adapter<ItemOrderAdapter.MyViewHolder>{
    private final RecyclerViewInterface recyclerViewInterface;
    Context context;
    ArrayList<Items> userArrayList;

    public ItemOrderAdapter(Context context, ArrayList<Items> userArrayList, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.userArrayList = userArrayList;
        this.recyclerViewInterface=recyclerViewInterface;
    }

    @NonNull
    @Override
    public ItemOrderAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.itemorder_lay ,parent,false);
        return new MyViewHolder(v,recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Items items = userArrayList.get(position);
        holder.itemname.setText(items.itemsP);
        holder.itemdescription.setText(items.itemdescriP);
        holder.price.setText(items.priceP);
        holder.client.setText(items.ClientID);

    }

    @Override
    public int getItemCount() {
        return userArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView itemname ,itemdescription ,price,client;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            itemname = itemView.findViewById(R.id.itemname);
            itemdescription = itemView.findViewById(R.id.itemdescription);
            price = itemView.findViewById(R.id.price);
            client = itemView.findViewById(R.id.ClientOrder);



            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface!=null){
                        int pos = getAdapterPosition();
                        if (pos!= RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }
}
