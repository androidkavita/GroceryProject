package com.easym.vegie.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easym.vegie.R;

    public class ReachUsAdapter extends RecyclerView.Adapter<ReachUsAdapter.MyViewHolder> {


        public ReachUsAdapter() {

        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.reach_us_list, parent, false);
            return new MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        }

        @Override
        public int getItemCount() {
            return 0;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public class MyViewHolder extends RecyclerView.ViewHolder {


            public MyViewHolder(@NonNull View itemView) {
                super(itemView);


            }
        }

    }

