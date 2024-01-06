package com.easym.vegie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easym.vegie.R;
import com.easym.vegie.interfaces.OnItemClickListener;
import com.easym.vegie.model.MenuModel;

import java.util.ArrayList;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MenuHolder> {


    private ArrayList<MenuModel> menuList;
    private Context mContext;
    private OnItemClickListener itemClickListener;


    public MenuAdapter(Context mContext, ArrayList<MenuModel> menuList, OnItemClickListener itemClickListener) {
        this.menuList = menuList;
        this.mContext = mContext;
        this.itemClickListener = itemClickListener;
    }

    @NonNull
    @Override
    public MenuHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drawer_menu_row, parent, false);

        return new MenuHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final MenuHolder holder, int position) {

        final MenuModel menuModel = menuList.get(position);

        if (menuModel != null) {
            holder.txt_menu_name.setText(menuModel.getMenuName());
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onItemClickListener(menuModel.getClicks(), null, holder.getAdapterPosition(), null, null, null, false);
            }
        });

    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    class MenuHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView txt_menu_name;


        MenuHolder(@NonNull View itemView) {
            super(itemView);
            txt_menu_name = itemView.findViewById(R.id.txt_menu_name);
        }
    }
}
