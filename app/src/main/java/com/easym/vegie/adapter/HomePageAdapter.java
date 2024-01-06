package com.easym.vegie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.easym.vegie.R;
import com.easym.vegie.model.DashboardModel;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class HomePageAdapter extends RecyclerView.Adapter<HomePageAdapter.MyViewHolder> {
    private Context context;
    private ArrayList<DashboardModel.RecommendedData> recommendedDataArrayList = new ArrayList<>();

    public HomePageAdapter(Context context, ArrayList<DashboardModel.RecommendedData> recommendedDataArrayList) {
        this.context = context;
        this.recommendedDataArrayList = recommendedDataArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_page_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HomePageAdapter.MyViewHolder holder, int position) {

        holder.tvPrice.setText("₹" + recommendedDataArrayList.get(position).getPrice());
        holder.tvUnit.setText(recommendedDataArrayList.get(position).getQuantity() + recommendedDataArrayList.get(position).getUnit());
        holder.tvProductName.setText(recommendedDataArrayList.get(position).getDescription());

        Picasso.get().load(recommendedDataArrayList.get(position).getImage()).into(holder.productImage);

      /*  holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, AddWishListActivity.class);
                context.startActivity(intent);
            }
        });*/

    }

    @Override
    public int getItemCount() {
        return recommendedDataArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView tvPrice, tvUnit, tvProductName;
        private ImageView productImage, imageOffer;
        private CardView cardView;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvUnit = itemView.findViewById(R.id.tv_unit);
            tvProductName = itemView.findViewById(R.id.tv_producttitle);
            productImage = itemView.findViewById(R.id.img_product);
            imageOffer = itemView.findViewById(R.id.image_offer);
            cardView = itemView.findViewById(R.id.card_view);

        }
    }
}
