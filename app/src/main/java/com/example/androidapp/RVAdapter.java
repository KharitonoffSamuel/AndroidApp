package com.example.androidapp;


import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ItemViewHolder> {

    List<String> dataList;

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public ItemViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    public RVAdapter(List<String> dataList) {
        this.dataList = dataList;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list_item, parent,
                false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        holder.checkBox.setText(dataList.get(position));
        //https://www.youtube.com/watch?v=5YFPkFaLcIo
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }



    public void removeItem(int position) {
        dataList.remove(position);
        notifyItemRemoved(position);
    }

    public void swapItems(int firstPosition, int secondPosition) {
        Collections.swap(dataList, firstPosition, secondPosition);
        notifyItemMoved(firstPosition, secondPosition);
    }
}
