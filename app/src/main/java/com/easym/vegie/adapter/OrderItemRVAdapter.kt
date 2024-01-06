package com.easym.vegie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.easym.vegie.R
import com.easym.vegie.model.getorderdetail.ProductList

class OrderItemRVAdapter(
        val context: Context,
        val list : List<ProductList>
) : RecyclerView.Adapter<OrderItemRVAdapter.RecyclerViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.single_order_item,parent,false)
        return RecyclerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {

        if(list.get(position).brand_name == null) {

            holder.tv_ProductTitle.setText(list.get(position).getMenuName())
            holder.tv_Price.setText(list.get(position).getPrice())
            holder.tv_QTYTilte.setText(list.get(position).getQuantity())
            holder.tv_TotalPrice.setText(list.get(position).getSubtotal())

           /* holder.tvOrderItem.setText(list.get(position).getMenuName()
                    + " (" + list.get(position).getQuantity() + " " + list.get(position).getUnit() + ")")*/

        }else{
            /*holder.tvOrderItem.setText(list.get(position).brand_name
                    + " (" + list.get(position).getQuantity() + " " + list.get(position).getUnit() + ")")*/

            holder.tv_ProductTitle.setText(list.get(position).getBrand_name())
            holder.tv_Price.setText(list.get(position).getPrice())
            holder.tv_QTYTilte.setText(list.get(position).getQuantity())
            holder.tv_TotalPrice.setText(list.get(position).getSubtotal())
        }

    }

    class RecyclerViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView) {

        val tv_ProductTitle : TextView
        val tv_Price : TextView
        val tv_QTYTilte : TextView
        val tv_TotalPrice : TextView


        init {
            tv_ProductTitle = itemView.findViewById(R.id.tv_ProductTitle)
            tv_Price = itemView.findViewById(R.id.tv_Price)
            tv_QTYTilte = itemView.findViewById(R.id.tv_QTYTilte)
            tv_TotalPrice = itemView.findViewById(R.id.tv_TotalPrice)

        }
    }
}