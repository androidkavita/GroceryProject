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

public class TopDiscountedAdapter extends RecyclerView.Adapter<TopDiscountedAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<DashboardModel.DiscountedData> discountedDataArrayList = new ArrayList<>();

    public TopDiscountedAdapter(Context context, ArrayList<DashboardModel.DiscountedData> discountedDataArrayList) {
        this.context = context;
        this.discountedDataArrayList = discountedDataArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top_discounted_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.tvPrice.setText("₹" + discountedDataArrayList.get(position).getPrice());
        holder.tvUnit.setText("₹" + discountedDataArrayList.get(position).getPrice());
        holder.tvQuantity.setText(discountedDataArrayList.get(position).getQuantity() + discountedDataArrayList.get(position).getUnit());
        holder.tvProductName.setText(discountedDataArrayList.get(position).getDescription());
        holder.tvDiscountedMsg.setText(discountedDataArrayList.get(position).getDiscount() + "%OFF");

        Picasso.get().load(discountedDataArrayList.get(position).getImage()).into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return discountedDataArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPrice, tvUnit, tvProductName, tvDiscountedMsg, tvQuantity;
        private ImageView productImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvUnit = itemView.findViewById(R.id.tv_unit);
            tvProductName = itemView.findViewById(R.id.tv_producttitle);
            productImage = itemView.findViewById(R.id.img_product);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            tvDiscountedMsg = itemView.findViewById(R.id.txt_offer);
        }
    }

}
