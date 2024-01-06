package com.easym.vegie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easym.vegie.R;
import com.easym.vegie.model.getorder.Result;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyOrderAdapter extends RecyclerView.Adapter<MyOrderAdapter.MyViewHolder> {

    Context context;
    List<Result> list;

    public MyOrderAdapter(Context mContext, List<Result> mList) {
        this.context = mContext;
        this.list = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorder_list_new_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int position) {

        holder.tv_OrderNo.setText(""+list.get(position).getOrderId());

        String orderIdString = list.get(position).getCreatedDate();
        String [] orderDate = orderIdString.split(" ");


        holder.tv_OrderDate.setText(getFormattedDate(orderDate[0]));
        holder.tv_OrderAmount.setText(list.get(position).getFinalAmount());
        holder.tv_OrderStatus.setText(list.get(position).getOrderStatus());

        if(list.get(position).getOrderStatus().equals("Cancel")){
            holder.tv_OrderStatus.setTextColor(context.getResources().getColor(R.color.red));
        }else if(list.get(position).getOrderStatus().equals("Delivered")){
            holder.tv_OrderStatus.setTextColor(context.getResources().getColor(R.color.deliveredTextColor));
        }else {
            holder.tv_OrderStatus.setTextColor(context.getResources().getColor(R.color.ongoingTextColor));
        }

        if(position == list.size()-1){
            holder.lastLineView.setVisibility(View.VISIBLE);
        }else{
            holder.lastLineView.setVisibility(View.GONE);
        }
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

        TextView tv_OrderNo,tv_OrderDate,tv_OrderAmount,tv_OrderStatus;
        View lastLineView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_OrderNo = itemView.findViewById(R.id.tv_OrderNo);
            tv_OrderDate = itemView.findViewById(R.id.tv_OrderDate);
            tv_OrderAmount = itemView.findViewById(R.id.tv_OrderAmount);
            tv_OrderStatus = itemView.findViewById(R.id.tv_OrderStatus);
            lastLineView = itemView.findViewById(R.id.LastLineView);

        }
    }

    public String getFormattedDate( String incomingDate) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        String formatedDate = "";
        try {
            date = fmt.parse(incomingDate);
            SimpleDateFormat fmtOut = new SimpleDateFormat("dd-MM-yyyy");
            formatedDate = fmtOut.format(date);
        } catch (ParseException exception) {
            exception.printStackTrace();
        }

        return formatedDate;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}

