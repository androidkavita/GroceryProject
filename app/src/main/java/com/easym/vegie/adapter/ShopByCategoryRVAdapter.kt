package com.easym.vegie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.easym.vegie.R
import com.easym.vegie.model.home.CategoryName
import com.squareup.picasso.Picasso

class ShopByCategoryRVAdapter(
        val context : Context,
        val list : List<CategoryName>
) : RecyclerView.Adapter<ShopByCategoryRVAdapter.RecyclerViewItem>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewItem {

        val view = LayoutInflater.from(context).inflate(R.layout.single_shop_by_category_item,
                parent,false)

        return RecyclerViewItem(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerViewItem, position: Int) {

        Picasso.get()
                .load(list[position].image)
                .placeholder(R.drawable.image_loading)
                .into(holder.iv_CategoryImage)

        holder.tv_CategoryName.setText(list.get(position).name)
    }

    inner class RecyclerViewItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val iv_CategoryImage : ImageView
        val tv_CategoryName : TextView
        init {
            iv_CategoryImage = itemView.findViewById(R.id.iv_CategoryImage)
            tv_CategoryName = itemView.findViewById(R.id.tv_CategoryName)
        }
    }

}