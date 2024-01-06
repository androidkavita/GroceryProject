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
import com.easym.vegie.model.DashboardModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SeasonAdapter extends RecyclerView.Adapter<SeasonAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<DashboardModel.SeasonalData> seasonalDataArrayList = new ArrayList<>();

    public SeasonAdapter(Context context, ArrayList<DashboardModel.SeasonalData> seasonalDataArrayList) {
        this.context = context;
        this.seasonalDataArrayList = seasonalDataArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvPrice.setText("₹" + seasonalDataArrayList.get(position).getPrice());
        holder.tvUnit.setText(seasonalDataArrayList.get(position).getQuantity() + seasonalDataArrayList.get(position).getUnit());
        holder.tvProductName.setText(seasonalDataArrayList.get(position).getDescription());

        Picasso.get().load(seasonalDataArrayList.get(position).getImage()).into(holder.productImage);

    }

    @Override
    public int getItemCount() {
        return seasonalDataArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPrice, tvUnit, tvProductName;
        private ImageView productImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvUnit = itemView.findViewById(R.id.tv_unit);
            tvProductName = itemView.findViewById(R.id.tv_producttitle);
            productImage = itemView.findViewById(R.id.img_product);
        }
    }
}

