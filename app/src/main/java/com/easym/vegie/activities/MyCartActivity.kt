package com.easym.vegie.activities

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.easym.vegie.R
import com.easym.vegie.Utils.Utility
import com.easym.vegie.databinding.ActivityMyCartBinding
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.custom_actionbar_layout.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.ConnectException

class MyCartActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityMyCartBinding
    var amount: String = ""
    var couponDiscount: String = "0"
    var couponCode: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_my_cart)

        iv_Back.setOnClickListener(this)
        tv_Title.text = getString(R.string.my_cart)
        tv_Title.visibility = View.VISIBLE

        iv_Search.visibility = View.GONE
        iv_Cart.visibility = View.GONE

        getMyCart()

        binding.tvApply.setOnClickListener(this)

    }

    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.iv_Back -> finish()
            R.id.tv_Apply -> showApplyCouponDialog(amount)
            R.id.button_Checkout -> {
                val intent = Intent(this, CheckOutScreenActivity::class.java)
                intent.putExtra("couponDiscount", couponDiscount)
                intent.putExtra("couponCode", couponCode)
                intent.putExtra("checkout_type", "cart")
                intent.putExtra("quoatationId", "")
                startActivity(intent)
            }
        }
    }


    private fun getMyCart() {

        if (Utility.getInstance().checkInternetConnection(this)) {

            apiService.getMyCart(
                userPref.user.id!!,
                userPref.user.token!!, "", ""
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({ this.showProgressDialog() })
                .doOnCompleted({ this.hideProgressDialog() })
                .subscribe({

                    if (it.responseCode.equals("200")) {

                        val mycartResult = it.data.result

                        binding.tvSubTotal.text = mycartResult.subTotal
                        binding.tvDeliveryCharge.text = mycartResult.deliveryCharge
                        binding.tvTaxes.text = mycartResult.tax
                        binding.tvTotalMyCart.text = "" + mycartResult.total

                        amount = "" + mycartResult.total

                        binding.buttonCheckout.visibility = View.VISIBLE
                        binding.buttonCheckout.setOnClickListener(this)
                        binding.llCartParent.visibility = View.VISIBLE

                    } else if (it.responseCode == "403") {
                        utils.openLogoutDialog(this, userPref)
                    } else {

                        binding.llCartParent.visibility = View.GONE

                        hideProgressDialog()
                        Utility.simpleAlert(
                            this,
                            getString(R.string.info_dialog_title),
                            it.responseMessage
                        )
                    }
                }, {

                    try {

                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                this,
                                getString(R.string.error),
                                getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(this, "", getString(R.string.something_went_wrong))
                        }

                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }

                })
        } else {
            Utility.simpleAlert(
                this,
                getString(R.string.error),
                getString(R.string.check_network_connection)
            )
        }

    }

    private fun showApplyCouponDialog(amount: String) {

        if (amount.equals("")) {
            Toast.makeText(this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show()
            return
        }

        val alertDialogBuilder = AlertDialog.Builder(this)
        val view = LayoutInflater.from(this).inflate(R.layout.apply_code_popup, null)
        val et_CouponCode = view.findViewById<EditText>(R.id.et_CouponCode)
        val btn_Cancel = view.findViewById<Button>(R.id.btn_Cancel)
        val btn_apply = view.findViewById<Button>(R.id.btn_apply)

        alertDialogBuilder.setView(view)
        val alertDialog = alertDialogBuilder.show()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
        btn_apply.setOnClickListener {

            val mCouponCode = et_CouponCode.text.toString().trim()

            if (!TextUtils.isEmpty(mCouponCode)) {

                couponCode = mCouponCode
                applyCouponCode(couponCode, amount, alertDialog)

            } else {
                Snackbar.make(view, "Please enter valid Coupon Code", Snackbar.LENGTH_SHORT).show()
            }
        }

        btn_Cancel.setOnClickListener {
            alertDialog.cancel()
        }
    }

    private fun applyCouponCode(couponCode: String, amount: String, alertDialog: AlertDialog) {

        if (Utility.getInstance().checkInternetConnection(this)) {
            Log.e("UserId", userPref.user.id + "")

            apiService.coupon(
                couponCode,
                amount,
                userPref.user.token!!
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { this.showProgressDialog() }
                .doOnCompleted { this.hideProgressDialog() }
                .subscribe({
                    if (it.responseCode.equals("200")) {

                        it.data.data.status
                        if (it.data.data.status.equals("error")) {

                            Toast.makeText(this, it.data.data.msg, Toast.LENGTH_SHORT).show()

                        } else {

                            binding.tvApply.text = "Applied"
                            binding.tvCouponDescription.text = "Coupon Discount " + it.data.data.result.amount

                            couponDiscount = "" + it.data.data.result.amount

                            val amountInt = amount.toInt()
                            val discountedTotal = amountInt - (it.data.data.result.amount).toInt()

                            binding.tvTotalMyCart.text = "" + discountedTotal

                            alertDialog.dismiss()

                        }


                    } else if (it.responseCode == "403") {
                        utils.openLogoutDialog(this, userPref)
                    } else {
                        hideProgressDialog()
                        Utility.simpleAlert(
                            this,
                            getString(R.string.info_dialog_title),
                            it.responseMessage
                        )
                    }
                }, {

                    try {

                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                this,
                                getString(R.string.error),
                                getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(this, "", getString(R.string.something_went_wrong))
                        }

                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }

                })
        } else {
            Utility.simpleAlert(
                this,
                getString(R.string.error),
                getString(R.string.check_network_connection)
            )
        }

    }

    override fun onResume() {
        super.onResume()
        getMyCart()
    }

}