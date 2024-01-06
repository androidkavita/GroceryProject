package com.easym.vegie.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.easym.vegie.R;

public class SelectPaymentModeRVAdapter extends RecyclerView.Adapter {

    int CreditDebitCardViewType = 0;
    int NetBankingViewType = 1;
    int CashOnDelivery = 2;
    int UPIPayment = 3;

    Context context;

    public SelectPaymentModeRVAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == CreditDebitCardViewType){

            View view = LayoutInflater.from(context).inflate(
                    R.layout.credit_card_debit_card_payment_option_layout,parent,
                    false);
            return new RecyclerViewHolder(view);

        }else if(viewType == NetBankingViewType){

            View view = LayoutInflater.from(context).inflate(
                    R.layout.net_banking_payment_option_layout,parent,
                    false);
            return new RecyclerViewHolder(view);

        }else if(viewType == CashOnDelivery){

            View view = LayoutInflater.from(context).inflate(
                    R.layout.cash_on_delivery_payment_option,parent,
                    false);
            return new RecyclerViewHolder(view);

        }else {

            View view = LayoutInflater.from(context).inflate(
                    R.layout.upi_payment_option,parent,
                    false);
            return new RecyclerViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    @Override
    public int getItemViewType(int position) {

        if(position == 0) return CreditDebitCardViewType;
        else if(position == 1) return NetBankingViewType;
        else if(position == 2) return CashOnDelivery;
        else return UPIPayment;

    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder{

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
