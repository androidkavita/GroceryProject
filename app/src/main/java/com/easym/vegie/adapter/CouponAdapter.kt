package com.easym.vegie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.easym.vegie.R
import com.easym.vegie.databinding.CouponViewLayoutBinding
import com.easym.vegie.model.applyCouponModel.ApplyCouponData
import java.util.*

class CouponAdapter(
    val context: Context,
    var listData: ArrayList<ApplyCouponData> = ArrayList(),
) : RecyclerView.Adapter<CouponAdapter.MyViewHolder>() {
    private var listener: OnItemClickListener? = null

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var binding: CouponViewLayoutBinding = DataBindingUtil.bind(view)!!

        init {
            view.setOnClickListener(this)
            binding.btnApply.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (listener != null)
                listener!!.onItemClick(adapterPosition, v)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.coupon_view_layout, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val model = listData[position]
        holder.binding.tvCouponCode.text = model.coupon_code
        holder.binding.tvTitle.text = model.coupon_description

    }

    override fun getItemCount(): Int {
        return listData.size
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, view: View)
    }
}