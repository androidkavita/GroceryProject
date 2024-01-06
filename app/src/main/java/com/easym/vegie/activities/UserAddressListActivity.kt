package com.easym.vegie.activities

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.easym.vegie.R
import com.easym.vegie.Utils.Utility
import com.easym.vegie.adapter.ShippingAddressRVAdapter
import com.easym.vegie.model.CommonResponseData
import com.easym.vegie.model.useraddress.UserAddressData
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_user_address_list.*
import kotlinx.android.synthetic.main.toolbar_main.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.ConnectException

class UserAddressListActivity : BaseActivity() {

    lateinit var addressAdapter: ShippingAddressRVAdapter
    lateinit var addressList: ArrayList<UserAddressData>
    lateinit var layoutManager: LinearLayoutManager

    var addressId = ""
    var lat = ""
    var longi = ""

    var couponDiscount = "0"
    var couponCode = ""
    var checkoutType = ""
    var quoatationId = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_address_list)

        txt_back.text = "Back"
        back_layout.setOnClickListener({ onClick(it) })
        tv_AddLocation.setOnClickListener({ onClick(it) })
        tv_Next.setOnClickListener({ onClick(it) })

        val bundle = intent.extras
        if (bundle != null) {
            couponDiscount = bundle.getString("couponDiscount")!!
            couponCode = bundle.getString("couponCode")!!
            checkoutType = bundle.getString("checkout_type")!!
            quoatationId = bundle.getString("quoatationId")!!
        }

        addressList = ArrayList()
        layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        addressAdapter = ShippingAddressRVAdapter(this, addressList,
            object : ShippingAddressRVAdapter.ChangeShippingAddress {

                override fun onAddressChange(position: Int) {
                    for (i in 0..addressList.size - 1) {
                        addressList.get(i).isSelected = false
                    }

                    addressList.get(position).isSelected = true
                    addressId = addressList.get(position).id
                    lat = addressList.get(position).lat
                    longi = addressList.get(position).longi


                    rv_UserAddress.postDelayed(object : Runnable {
                        override fun run() {
                            addressAdapter.notifyDataSetChanged()
                        }
                    }, 50)

                }

                override fun deleteShippingAddress(position: Int) {
                    removeShippingAddress(addressList.get(position).id)
                }

            })

        rv_UserAddress.layoutManager = layoutManager
        rv_UserAddress.adapter = addressAdapter


    }

    override fun onResume() {
        super.onResume()

        addressId = ""
        lat = ""
        longi = ""
        getUserAddress()

    }

    private fun removeShippingAddress(addressId: String) {
        if (Utility.getInstance().checkInternetConnection(this)) {
            apiService.removeUserAddress(
                addressId,
                userPref.user.token!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({ response: CommonResponseData ->
                    if (response.responseCode == "200") {

                        getUserAddress()

                    } else if (response.responseCode == "403") {
                        utils.openLogoutDialog(this, userPref)
                    } else {
                        hideProgressDialog()
                        Utility.simpleAlert(this,
                            getString(R.string.info_dialog_title),
                            response.responseMessage)
                    }
                }) { e: Throwable ->

                    try {
                        if (e is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(this,
                                getString(R.string.error),
                                getString(R.string.check_network_connection))
                        } else {
                            hideProgressDialog()
                            e.printStackTrace()
                            Utility.simpleAlert(this, "", getString(R.string.something_went_wrong))
                        }
                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + e.message)
                    }
                }
        } else {
            hideProgressDialog()
            Utility.simpleAlert(this,
                getString(R.string.error),
                getString(R.string.check_network_connection))
        }
    }

    private fun onClick(view: View?) {

        when (view!!.id) {

            R.id.back_layout -> {
                finish()
            }

            R.id.tv_AddLocation -> {
                val intent5 = Intent(this, FragmentContainerActivity::class.java)
                intent5.putExtra("FragmentName", "Add Address Fragment")
                startActivity(intent5)
            }

            R.id.tv_Next -> {

                if (!TextUtils.isEmpty(addressId)) {

                    if (!TextUtils.isEmpty(lat) && !TextUtils.isEmpty(longi)) {
                        checkSupplyLocation(lat, longi)
                    } else {
                        Toast.makeText(this,
                            getString(R.string.something_went_wrong),
                            Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Snackbar.make(view, "Please select address", Snackbar.LENGTH_SHORT).show()
                }

            }


        }
    }

    private fun getUserAddress() {

        if (Utility.getInstance().checkInternetConnection(this)) {

            apiService.getUserAddress(
                userPref.user.id!!,
                userPref.user.token!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe({ this.showProgressDialog() })
                .doOnCompleted({ this.hideProgressDialog() })
                .subscribe({

                    if (it.responseCode.equals("200")) {

                        addressList.clear()

                        val userAddressList = it.data
                        addressList.addAll(userAddressList)
                        addressAdapter.notifyDataSetChanged()



                        tvMsg.text = "Choose Address"

                        if (addressList.size > 0) {
                            tv_Next.visibility = View.VISIBLE
                        } else {
                            tv_Next.visibility = View.GONE
                        }

                    } else if (it.responseCode == "403") {
                        utils.openLogoutDialog(this, userPref)
                    } else {

                        addressList.clear()
                        addressAdapter.notifyDataSetChanged()
                        tvMsg.text = "No Address Found"
                        tv_Next.visibility = View.GONE

                        // hideProgressDialog()
                        // Utility.simpleAlert(this, getString(R.string.info_dialog_title), it.getResponseMessage())
                    }
                }, {

                    try {

                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(this,
                                getString(R.string.error),
                                getString(R.string.check_network_connection))
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
            Utility.simpleAlert(this,
                getString(R.string.error),
                getString(R.string.check_network_connection))
        }
    }

    private fun checkSupplyLocation(latitude: String, longitude: String) {

        if (Utility.getInstance().checkInternetConnection(this)) {

            apiService.checkByLocation(
                latitude,
                longitude,
                userPref.user.token!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response: CommonResponseData ->

                    if (response.responseCode == "200") {

                        val intent = Intent(this, CheckOutScreenActivity::class.java)
                        intent.putExtra("couponDiscount", couponDiscount)
                        intent.putExtra("couponCode", couponCode)
                        intent.putExtra("checkout_type", checkoutType)
                        intent.putExtra("quoatationId", quoatationId)
                        intent.putExtra("addressId", addressId)
                        intent.putExtra("lat", lat)
                        intent.putExtra("longi", longi)
                        startActivity(intent)

                    } else if (response.responseCode == "403") {
                        utils.openLogoutDialog(this, userPref)
                    } else {
                        hideProgressDialog()
                        showAlertDialogToChangeLocation()
                    }
                }) { e: Throwable ->

                    try {

                        if (e is ConnectException) {
                            Log.d("HomePageActivity", "Internet Connection issue")
                        } else {
                            Log.d("HomePageActivity", "" + e.message)
                        }

                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + e.message)
                    }

                }
        } else {
            hideProgressDialog()
            Utility.simpleAlert(this,
                getString(R.string.error),
                getString(R.string.check_network_connection))
        }
    }

    private fun showAlertDialogToChangeLocation() {

        val builder = AlertDialog.Builder(this)
        builder.setCancelable(false)
        val view = LayoutInflater.from(this).inflate(R.layout.no_supply_pop_screen_layout, null)
        val iv_Close = view.findViewById<ImageView>(R.id.iv_Close)
        val tv_Description = view.findViewById<TextView>(R.id.tv_Description)
        val btn_ChangeAddress = view.findViewById<Button>(R.id.btn_ChangeAddress)
        builder.setView(view)
        val dialog = builder.show()
        iv_Close.setOnClickListener { dialog.dismiss() }
        btn_ChangeAddress.setOnClickListener {

            val intent5 = Intent(this, FragmentContainerActivity::class.java)
            intent5.putExtra("FragmentName", "Add Address Fragment")
            startActivity(intent5)
            dialog.dismiss()
        }
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

}