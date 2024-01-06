package com.easym.vegie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.easym.vegie.R
import com.easym.vegie.Utils.Utils
import com.easym.vegie.model.userquotation.Result

class SavedQuotationsAdapter(
        val context: Context,
        val list : List<Result>,
        val action : Action
)  : RecyclerView.Adapter<SavedQuotationsAdapter.RecyclerViewItem>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewItem {
    val view = LayoutInflater.from(context).inflate(R.layout.single_saved_quotations_item,
            parent,false)
        return RecyclerViewItem(view)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerViewItem, position: Int) {

        //val serialNo = position+1
        holder.tv_QuotationNo.setText(list.get(position).id)
        //holder.tv_SavedDate.setText(list.get(position).qu_date)
        holder.tv_SavedDate.setText(Utils(context!!).formatDate(list.get(position).qu_date))

        holder.iv_DeleteSavedQuatation.setOnClickListener({
            action.deleteQuatation(position)
        })

        holder.ll_Parent.setOnClickListener({
            action.moveForward(position)
        })


    }

    inner class RecyclerViewItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tv_QuotationNo : TextView
        val tv_SavedDate :TextView
        val iv_DeleteSavedQuatation : ImageView
        val ll_Parent : LinearLayout

        init {
            tv_QuotationNo = itemView.findViewById(R.id.tv_QuotationNo)
            tv_SavedDate = itemView.findViewById(R.id.tv_SavedDate)
            iv_DeleteSavedQuatation = itemView.findViewById(R.id.iv_DeleteSavedQuatation)
            ll_Parent = itemView.findViewById(R.id.ll_Parent)
        }
    }

    interface Action {
        fun deleteQuatation(position: Int)
        fun moveForward(position: Int)
    }

}