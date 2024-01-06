package com.easym.vegie.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.easym.vegie.R
import com.easym.vegie.Utils.Utils
import com.easym.vegie.api.ApiService1
import com.easym.vegie.sharePref.UserPref
import dagger.android.AndroidInjection
import javax.inject.Inject

open class BaseActivity : AppCompatActivity() {

    var dialog: Dialog?=null

    @Inject lateinit var apiService: ApiService1
    @Inject lateinit var userPref: UserPref
    @Inject lateinit var utils: Utils

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    protected fun showProgressDialog() {
        if (dialog == null)
            dialog = Dialog(this)
        dialog!!.setContentView(R.layout.progress_dialoge)
        dialog!!.setCancelable(false)

        if (dialog != null && !dialog!!.isShowing)
            dialog!!.show()
    }

    protected fun hideProgressDialog() {
        if (dialog != null && dialog!!.isShowing)
            dialog!!.dismiss()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialog != null && dialog!!.isShowing)
            dialog!!.dismiss()
    }
}