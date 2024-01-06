package com.easym.vegie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.easym.vegie.R;
import com.easym.vegie.model.coupon.GetCouponListData;

import java.util.List;

public class OffersAdapter extends RecyclerView.Adapter<OffersAdapter.MyViewHolder> {

    Context context;
    List<GetCouponListData> list;
    CopyCoupon copyCoupon;

    public OffersAdapter(Context context, List<GetCouponListData> list,CopyCoupon mCoupon) {
        this.context = context;
        this.list = list;
        this.copyCoupon = mCoupon;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.offers_layout_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.tv_CouponCode.setText(list.get(position).getCouponCode());
        holder.tv_CouponDescription.setText(list.get(position).getCouponDescription());
        holder.tv_CopyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyCoupon.copyCouponCode(list.get(position).getCouponCode());
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_CouponCode,tv_CopyCode,tv_CouponDescription;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_CouponCode = itemView.findViewById(R.id.tv_CouponCode);
            tv_CopyCode = itemView.findViewById(R.id.tv_CopyCode);
            tv_CouponDescription = itemView.findViewById(R.id.tv_CouponDescription);

        }
    }

   public interface CopyCoupon{
        void copyCouponCode(String code);
    }

}
