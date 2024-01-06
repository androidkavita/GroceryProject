package com.easym.vegie.adapter

import com.easym.vegie.R
import com.transferwise.sequencelayout.SequenceAdapter
import com.transferwise.sequencelayout.SequenceStep

//OrderSequenceAdapter

class OrderSequenceAdapter(private val items: List<OrderSequenceItem>)
    : SequenceAdapter<OrderSequenceAdapter.OrderSequenceItem>() {

    override fun getCount(): Int {
        return items.size
    }

    override fun getItem(position: Int): OrderSequenceItem {
        return items[position]
    }

    override fun bindView(sequenceStep: SequenceStep, item: OrderSequenceItem) {
        with(sequenceStep) {

            setActive(item.isActive)
            setTitle(item.title)
            setSubtitle(item.subTitle)
            if(item.isActive){
                setTitleTextAppearance(R.style.TextAppearance_AppCompat_Title)
            }

             setAnchor(item.anchor)
            //setAnchorTextAppearance(...)
            // setTitleTextAppearance()
            //setSubtitleTextAppearance(...)
        }
    }

    data class OrderSequenceItem(val isActive: Boolean, val title: String, val subTitle : String,
    val anchor : String)
}