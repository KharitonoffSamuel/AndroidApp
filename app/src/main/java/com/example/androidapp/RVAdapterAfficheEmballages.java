package com.example.androidapp;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class RVAdapterAfficheEmballages extends RecyclerView.Adapter<RVAdapterAfficheEmballages.ViewHolder> {

    ArrayList<String> dataList;
    View view;

    public RVAdapterAfficheEmballages(ArrayList<String> dataList) {
        this.dataList = dataList;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView emballage;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            emballage = view.findViewById(R.id.textViewEmballages);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_list_emballages, parent,
                false);

        return new RVAdapterAfficheEmballages.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVAdapterAfficheEmballages.ViewHolder holder, int position) {
        if(dataList != null && dataList.size()>0) {
            holder.emballage.setText(dataList.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (dataList != null) {
            return dataList.size();
        } else {
            return 0;
        }
    }
}
