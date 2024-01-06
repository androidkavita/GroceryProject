package com.easym.vegie.Utils

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import com.easym.vegie.R

class ShareBy(
        val context : Context,
        val action : Action
) {

    fun showShareByPop(){

        val alertDialog = AlertDialog.Builder(context).create()
        alertDialog.setCancelable(false)
        val view = LayoutInflater.from(context).inflate(R.layout.share_by_pop,null)
        val gmail = view.findViewById<LinearLayout>(R.id.ll_Gmail)
        val whatsApp = view.findViewById<LinearLayout>(R.id.ll_WhatsApp)
        val ivClose = view.findViewById<ImageView>(R.id.iv_Close)

        alertDialog.setView(view)
        alertDialog.show()

        ivClose.setOnClickListener({
            alertDialog.dismiss()
        })

        gmail.setOnClickListener({
            alertDialog.dismiss()
            action.shareOnGmail()
        })


        whatsApp.setOnClickListener({
            alertDialog.dismiss()
            action.shareOnWhatsApp()
        })

        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));


    }

    interface Action {
        fun shareOnGmail()
        fun shareOnWhatsApp()
    }

}