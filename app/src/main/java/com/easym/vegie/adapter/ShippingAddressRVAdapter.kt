package com.easym.vegie.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.easym.vegie.R
import com.easym.vegie.model.useraddress.UserAddressData

class ShippingAddressRVAdapter(
        val context : Context,
        val list : ArrayList<UserAddressData>,
        val changeShippingAddress: ChangeShippingAddress
) : RecyclerView.Adapter<ShippingAddressRVAdapter.RecyclerViewItem>() {

    val country_selcted = arrayOfNulls<Boolean>(list.size)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewItem {
        val view = LayoutInflater.from(context).inflate(R.layout.address_list,parent,false)
        return RecyclerViewItem(view);

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: RecyclerViewItem, position: Int) {

        holder.tv_AddressTitle.setText(list.get(position).address)
        holder.tv_AddressFullDescription.setText(list.get(position).locality+","+
        list.get(position).city+",\n"+list.get(position).state+","+list.get(position).pincode)


        if(list.get(position).isSelected) {
            holder.rb_Address.isChecked = true
        }else{
            holder.rb_Address.isChecked = false
        }

        holder.rb_Address.setOnCheckedChangeListener(object : CompoundButton.OnCheckedChangeListener{
            override fun onCheckedChanged(buttonView: CompoundButton?, isChecked: Boolean) {
                changeShippingAddress.onAddressChange(position)

            }
        })

        holder.iv_Cross.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                changeShippingAddress.deleteShippingAddress(position)

            }
        })


    }

    inner class RecyclerViewItem(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val rb_Address : RadioButton
        val tv_AddressTitle : TextView
        val iv_Cross : ImageView
        val tv_AddressFullDescription : TextView

        init {
            rb_Address = itemView.findViewById(R.id.rb_Address)
            tv_AddressTitle = itemView.findViewById(R.id.tv_AddressTitle)
            iv_Cross = itemView.findViewById(R.id.iv_Cross)
            tv_AddressFullDescription = itemView.findViewById(R.id.tv_AddressFullDescription)
        }
    }

    interface ChangeShippingAddress {
        fun onAddressChange(position: Int)
        fun deleteShippingAddress(position: Int)
    }
}