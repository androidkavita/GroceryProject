package com.easym.vegie.adapter

import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.easym.vegie.R
import com.easym.vegie.model.productbrand.ProductBrandResult
import com.squareup.picasso.Picasso

class BrandRVAdapter(
        val context : Context,
        val list : List<ProductBrandResult>
): RecyclerView.Adapter<BrandRVAdapter.RecyclerViewItem>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewItem {
        val view = LayoutInflater.from(context).inflate(R.layout.single_brand_list_item,
                parent,false)
        return RecyclerViewItem(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerViewItem, position: Int) {

        if(!list.get(position).other_name.equals("false")) {

            holder.tv_BrandTitle.setText(list.get(position).brandName+
                    " / "+list.get(position).other_name + "\n" + list.get(position).price + " / "+ list.get(position).unit)

        }else{
            holder.tv_BrandTitle.setText(list.get(position).brandName+ "\n" + list.get(position).price + " / "+ list.get(position).unit)
        }

        if(list.get(position).brand_image != null &&
                !TextUtils.isEmpty(list.get(position).brand_image)){

            Picasso.get()
                    .load(list.get(position).brand_image)
                    .into(holder.iv_Brand)

        }

    }

    inner class RecyclerViewItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val iv_Brand : ImageView
        val tv_BrandTitle : TextView

        init {
            iv_Brand = itemView.findViewById(R.id.iv_Brand)
            tv_BrandTitle = itemView.findViewById(R.id.tv_BrandTitle)
        }
    }
}