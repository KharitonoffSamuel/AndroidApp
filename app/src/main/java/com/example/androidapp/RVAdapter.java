package com.example.androidapp;


import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RVAdapter extends RecyclerView.Adapter<RVAdapter.ItemViewHolder>{

    View view;
    ArrayList<String> dataList;
    Context context;
    CheckboxListener checkboxListener;
    ArrayList<String> dataList_0 = new ArrayList<>();

    public RVAdapter(ArrayList<String> dataList, Context context, CheckboxListener checkboxListener) {
        this.dataList = dataList;
        this.context = context;
        this.checkboxListener = checkboxListener;
    }

    public View getView() {
        return view;
    }

    public class ItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;

        public ItemViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list_item, parent,
                false);

        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        //https://www.youtube.com/watch?v=5YFPkFaLcIo
        if(dataList != null && dataList.size()>0){
            holder.checkBox.setText(dataList.get(position));
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (holder.checkBox.isChecked()){
                        dataList_0.add(dataList.get(position));
                    }
                    else{
                        dataList_0.remove(dataList.get(position));
                    }
                    checkboxListener.onCheckboxChange(dataList_0);
                }
            });
        }
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
