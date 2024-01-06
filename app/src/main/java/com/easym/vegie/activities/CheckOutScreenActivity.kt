package com.easym.vegie.activities

import android.annotation.SuppressLint
import android.app.*
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.widget.*
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.easym.vegie.R
import com.easym.vegie.Utils.Utility
import com.easym.vegie.Utils.Utils
import com.easym.vegie.adapter.CouponAdapter
import com.easym.vegie.adapter.SelectPaymentModeRVAdapter
import com.easym.vegie.databinding.ActivityCheckOutScreenBinding
import com.easym.vegie.model.CommonResponseData
import com.easym.vegie.model.applyCouponModel.ApplyCouponData
import com.easym.vegie.model.applycoupon.ApplyCoupon
import com.easym.vegie.model.checkout.Checkout
import com.easym.vegie.model.limitday.LimitDay
import com.easym.vegie.model.placeorder.AddOrder
import com.google.android.material.snackbar.Snackbar
import com.razorpay.PaymentResultListener
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.ConnectException
import java.util.*

class CheckOutScreenActivity : BaseActivity(), View.OnClickListener, PaymentResultListener,
    RadioGroup.OnCheckedChangeListener {

    var TAG = "CheckOutScreen"
    private var binding: ActivityCheckOutScreenBinding? = null

    // ShippingAddressRVAdapter addressAdapter;
    var paymentModeRVAdapter: SelectPaymentModeRVAdapter? = null
    var iv_Back: ImageView? = null
    var iv_Search: ImageView? = null
    var iv_Cart: ImageView? = null
    var tv_Title: TextView? = null
    var tv_AddLocation: TextView? = null
    var couponDiscount: String? = "0"
    var couponCode: String? = ""
    var checkoutType: String? = ""
    var quoatationId: String? = ""
    var subTotal = ""
    var deliveryCharge = ""
    var taxAmount = ""
    var totalPrice = 0.0
    var addressId: String? = ""
    var lat: String? = ""
    var longi: String? = ""
    private var couponAdapter: CouponAdapter? = null
    var couponList: ArrayList<ApplyCouponData> = ArrayList()

//    var expectedDeliveryDate = ""
//    var advancePayable = 0.0
//    var remainPayable = 0.0

    var isCouponApplied = false
    var rgPaymentMethod: RadioGroup? = null
    var rbNetBanking: RadioButton? = null
    var rbCash: RadioButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this,
            R.layout.activity_check_out_screen
        )

        /*
         To ensure faster loading of the Checkout form,
          call this method as early as possible in your checkout flow.
         */
        com.razorpay.Checkout.preload(applicationContext)
        iv_Back = findViewById(R.id.iv_Back)
        iv_Search = findViewById(R.id.iv_Search)
        iv_Cart = findViewById(R.id.iv_Cart)
        tv_Title = findViewById(R.id.tv_Title)
        tv_AddLocation = findViewById(R.id.tv_AddLocation)
        rgPaymentMethod = findViewById(R.id.rgPaymentMethod)
        rbNetBanking = findViewById(R.id.rbNetBanking)
        rbCash = findViewById(R.id.rbCash)
        iv_Back!!.setOnClickListener { finish() }
        iv_Search!!.visibility = View.GONE
        iv_Cart!!.visibility = View.GONE
        tv_Title!!.text = "CHECKOUT"
        tv_Title!!.visibility = View.VISIBLE
        // binding!!.tvExpectedDeliveryDate.setOnClickListener(this)
        binding!!.frameCross.setOnClickListener(this)

        val bundle = intent.extras
        if (bundle != null) {
            couponDiscount = bundle.getString("couponDiscount")
            couponCode = bundle.getString("couponCode")
            checkoutType = bundle.getString("checkout_type")
            quoatationId = bundle.getString("quoatationId")
            addressId = bundle.getString("addressId")
            lat = bundle.getString("lat")
            longi = bundle.getString("longi")
        }
        checkoutData
        rgPaymentMethod!!.setOnCheckedChangeListener(this)

        /* rgPaymentMethod!!.setOnCheckedChangeListener(RadioGroup.OnCheckedChangeListener { group, checkedId ->
             // checkedId is the RadioButton selected
             val rb: RadioButton = findViewById(checkedId) as RadioButton
             // textViewChoice.setText("You Selected " + rb.getText())
             //Toast.makeText(getApplicationContext(), rb.getText(), Toast.LENGTH_SHORT).show();
         })*/

/*        binding!!.tvApply.setOnClickListener {
            callCouponDialog()
        }*/
    }

    //Toast.makeText(this, ""+response.getResponseMessage(), Toast.LENGTH_SHORT).show();


    //Payment Mode
    private val checkoutData: Unit
        get() {
            Log.e("Calling", "CheckoutData")
            if (Utility.getInstance().checkInternetConnection(this)) {
                apiService.checkOut(
                    userPref.user.id!!,
                    couponDiscount!!,
                    userPref.user.token!!,
                    checkoutType!!,
                    quoatationId!!,
                    lat!!,
                    longi!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgressDialog() }
                    .doOnCompleted { hideProgressDialog() }
                    .subscribe({ response: Checkout ->
                        if (response.responseCode == "200") {
                            if (response.checkoutData.data.status != "error") {
                                val result = response.checkoutData.data.result
                                subTotal = result.subTotal
                                deliveryCharge = result.deliveryCharge
                                taxAmount = result.tax
                                couponDiscount = result.couponDiscount
                                totalPrice = result.total
                                /*advancePayable = result.advance_payable
                                remainPayable = result.remain_payable*/
                                binding!!.tvSubTotal.text = result.subTotal
                                binding!!.tvDeliveryCharges.text = result.deliveryCharge
                                binding!!.tvTaxAmount.text = result.tax
                                binding!!.tvCouponDiscount.text = result.couponDiscount
                                binding!!.tvTotalPrice.text = "" + result.total
                                /*  binding!!.tvPayableAmount.text = "" + result.advance_payable
                                  binding!!.tvRemainingAmount.text = "" + result.remain_payable*/
                                binding!!.tvApply.setOnClickListener(this)
                            }

                            //Payment Mode
                            val llm2 = LinearLayoutManager(
                                this,
                                LinearLayoutManager.VERTICAL, false
                            )
                            paymentModeRVAdapter = SelectPaymentModeRVAdapter(this)
                            binding!!.rvSelectPaymentMode.adapter = paymentModeRVAdapter
                            binding!!.rvSelectPaymentMode.layoutManager = llm2
                            binding!!.buttonPayNow.visibility = View.VISIBLE
                            binding!!.buttonPayNow.setOnClickListener(this)
                        } else if (response.responseCode == "403") {
                            utils.openLogoutDialog(this, userPref)
                        } else {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                this,
                                getString(R.string.info_dialog_title),
                                response.responseMessage
                            )
                            //Toast.makeText(this, ""+response.getResponseMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }) { e: Throwable ->
                        try {
                            if (e is ConnectException) {
                                hideProgressDialog()
                                Utility.simpleAlert(
                                    this,
                                    getString(R.string.error),
                                    getString(R.string.check_network_connection)
                                )
                            } else {
                                hideProgressDialog()
                                e.printStackTrace()
                                Utility.simpleAlert(
                                    this,
                                    "",
                                    getString(R.string.something_went_wrong)
                                )
                            }
                        } catch (ex: Exception) {
                            Log.e("", "Within Throwable Exception::" + e.message)
                        }
                    }
            } else {
                hideProgressDialog()
                Utility.simpleAlert(
                    this,
                    getString(R.string.error),
                    getString(R.string.check_network_connection)
                )
            }
        }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.tv_AddLocation -> {
                val intent5 = Intent(this, FragmentContainerActivity::class.java)
                intent5.putExtra("FragmentName", "Add Address Fragment")
                startActivity(intent5)
            }
            R.id.button_PayNow -> if (
                addressId != "" /*&& expectedDeliveryDate != ""*/) {
                if (rbNetBanking!!.isChecked) {
                    startPayment()

                } else if (rbCash!!.isChecked) {

                    addOrder("", "1")
                }
            } else {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please select shipping address", Snackbar.LENGTH_SHORT
                ).show()

                /* if (addressId == "") {
                     Snackbar.make(
                         findViewById(android.R.id.content),
                         "Please select shipping address", Snackbar.LENGTH_SHORT
                     ).show()
                 } else {
                     Snackbar.make(
                         findViewById(android.R.id.content),
                         "Please select delivery date and time", Snackbar.LENGTH_SHORT
                     ).show()
                 }*/

            }
            //R.id.tv_ExpectedDeliveryDate -> showDateTimePicker()
            R.id.tvApply -> {
//                if (!isCouponApplied)
                //  showApplyCouponDialog("" + totalPrice)
                callCouponDialog()
            }
            R.id.frameCross -> {
                binding!!.tvApply.text = getString(R.string.apply)
                binding!!.tvCouponDescription.text = getString(R.string.coupon_info)
                couponDiscount = "0"
                couponCode = ""
                isCouponApplied = false
                binding!!.frameCross.visibility = View.GONE
                checkoutData
            }
        }
    }


    private fun addOrder(transcationId: String, isCod: String) {
        if (Utility.getInstance().checkInternetConnection(this)) {
            apiService.addOrder(
                userPref.user.id!!,
                addressId!!,
                couponCode!!,
                subTotal,
                deliveryCharge,
                taxAmount,
                couponDiscount!!,
                "" + totalPrice,
                userPref.user.token!!,
                checkoutType!!,
                quoatationId!!,
                transcationId,
//                "" + advancePayable,
//                "" + remainPayable,
                /* expectedDeliveryDate,*/
                lat!!,
                longi!!,
                isCod
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({ response: AddOrder ->
                    if (response.getResponseCode() == "200") {

                        GlobalScope.launch {
                            apiService.sendOrderDetail(
                                userPref.user.id!!,
                                response.getData()!!.getOrderId().toString(),
                                addressId!!
                            )

                        }

                        Toast.makeText(this, "" + response.getResponseMessage(), Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, MyOrderDetailsActivity::class.java)
                        intent.putExtra("OrderId", response.getData()!!.getOrderId().toString())
                        intent.putExtra("source", "OrderPlaced")
                        intent.putExtra("Cod", "Cod")
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()


                    } else if (response.getResponseCode() == "403") {
                        utils.openLogoutDialog(this, userPref)
                    } else {
                        hideProgressDialog()
                        Utility.simpleAlert(
                            this,
                            getString(R.string.info_dialog_title),
                            response.getResponseMessage()
                        )
                    }
                }) { e: Throwable ->
                    try {
                        if (e is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                this,
                                getString(R.string.error),
                                getString(R.string.check_network_connection)
                            )
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
            Utility.simpleAlert(
                this,
                getString(R.string.error),
                getString(R.string.check_network_connection)
            )
        }
    }

    private fun removeShippingAddress(addressId: String) {
        if (Utility.getInstance().checkInternetConnection(this)) {
            apiService.removeUserAddress(
                addressId,
                userPref.user.token!!
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({ response: CommonResponseData ->
                    if (response.responseCode == "200") {
                        checkoutData
                    } else if (response.responseCode == "403") {
                        utils.openLogoutDialog(this, userPref)
                    } else {
                        hideProgressDialog()
                        Utility.simpleAlert(
                            this,
                            getString(R.string.info_dialog_title),
                            response.responseMessage
                        )
                    }
                }) { e: Throwable ->
                    if (e is ConnectException) {
                        hideProgressDialog()
                        Utility.simpleAlert(
                            this,
                            getString(R.string.error),
                            getString(R.string.check_network_connection)
                        )
                    } else {
                        hideProgressDialog()
                        e.printStackTrace()
                        Utility.simpleAlert(this, "", getString(R.string.something_went_wrong))
                    }
                }
        } else {
            hideProgressDialog()
            Utility.simpleAlert(
                this,
                getString(R.string.error),
                getString(R.string.check_network_connection)
            )
        }
    }

    fun startPayment() {
        /*
          You need to pass current activity in order to let Razorpay create CheckoutActivity
         */
//        val co = com.razorpay.Checkout()
//        try {
//            val payTotal = totalPrice * 100
//            val options = JSONObject()
//            options.put("name", "Reception cart")
//            options.put("description", "Purchase payment")
//            //You can omit the image option to fetch the image from dashboard
//            options.put("image", "https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
//            options.put("currency", "INR")
//            options.put("amount", payTotal)
//            val preFill = JSONObject()
//            preFill.put("email", userPref.user.email)
//            preFill.put("contact", userPref.user.mobile_number)
//            options.put("prefill", preFill)
//            co.open(this, options)
//        } catch (e: Exception) {
//            Toast.makeText(this, "Error in payment: " + e.message, Toast.LENGTH_SHORT)
//                .show()
//            e.printStackTrace()
//        }


        val activity: Activity = this
        val co = com.razorpay.Checkout()
//        co.setKeyID("rzp_test_d5XuHL2O7ABANw")
        co.setKeyID("rzp_live_AFzYeow7DiAood")
        try {
            val payTotal = totalPrice * 100
            var options = JSONObject()
            options.put("name","Reception cart")
            options.put("description","Demoing Charges")
            //You can omit the image option to fetch the image from dashboard
            options.put("image","https://s3.amazonaws.com/rzp-mobile/images/rzp.png")
            options.put("theme.color", "#3399cc");
            options.put("currency","INR")
            options.put("amount",payTotal)
            options.put("send_sms_hash",true);

            val prefill = JSONObject()
            prefill.put("email",userPref.user.email)
            prefill.put("contact",userPref.user.mobile_number)
            options.put("prefill",prefill)

            co.open(this@CheckOutScreenActivity,options)
        }catch (e: Exception){
            Toast.makeText(activity,"Error in payment: "+ e.message, Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    /**
     * The name of the function has to be
     * onPaymentSuccess
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */

    override fun onPaymentSuccess(razorpayPaymentID: String) {
        try {
            Log.e("PaymentId", razorpayPaymentID.toString())
            addOrder(razorpayPaymentID, "0")
            startActivity(Intent(this, HomePageActivity::class.java))
            // Toast.makeText(this, "Payment Successful: " + razorpayPaymentID, Toast.LENGTH_SHORT).show();
        } catch (e: Exception) {
            Log.e(TAG, "Exception in onPaymentSuccess", e)
        }
    }

    /**
     * The name of the function has to be
     * onPaymentError
     * Wrap your code in try catch, as shown, to ensure that this method runs correctly
     */
    override fun onPaymentError(code: Int, response: String) {
        try {
            Toast.makeText(this, "Payment Unsuccessful", Toast.LENGTH_SHORT).show()
            Log.e("PaymentFailed", "" + code + "" + response)
            //Toast.makeText(this, "Payment failed: " + code + " " + response, Toast.LENGTH_SHORT).show();
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e(TAG, "Exception in onPaymentError", e)
        }
    }

    fun showDateTimePicker() {
        val currentDate = Calendar.getInstance()
        val date = Calendar.getInstance()
        val datePickerDialog =
            DatePickerDialog(this@CheckOutScreenActivity, { view, year, monthOfYear, dayOfMonth ->
                date[year, monthOfYear] = dayOfMonth
                TimePickerDialog(this@CheckOutScreenActivity, { view, hourOfDay, minute ->
                    val datetime = Calendar.getInstance()
                    val c = Calendar.getInstance()
                    datetime[Calendar.HOUR_OF_DAY] = hourOfDay
                    datetime[Calendar.MINUTE] = minute
                    date[Calendar.HOUR_OF_DAY] = hourOfDay
                    date[Calendar.MINUTE] = minute
                    val d = Utils(this@CheckOutScreenActivity).getDateAndTime(date.time)
                    val selectedDate = Utils(this@CheckOutScreenActivity).getDate(date.time)
                    checkDayLimit(selectedDate, d)
                    Log.v(TAG, "The choosen one " + date.time)
                    Log.e("The Selected Date", d)
                }, currentDate[Calendar.HOUR_OF_DAY], currentDate[Calendar.MINUTE], false).show()
            }, currentDate[Calendar.YEAR], currentDate[Calendar.MONTH], currentDate[Calendar.DATE])
        val futureDate = Calendar.getInstance()
        futureDate.add(Calendar.DATE, 1)
        datePickerDialog.datePicker.minDate = futureDate.timeInMillis
        datePickerDialog.show()
    }


    private fun callCouponDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.coupon_list)
        val window: Window = dialog.window!!
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT)

        val closeImg = dialog.findViewById<ImageView>(R.id.closeImg)
        val rcyCoupon = dialog.findViewById<RecyclerView>(R.id.rcyCoupon)

        if (Utility.getInstance().checkInternetConnection(this)) {
            apiService.get_coupon_list(
                userPref.user.id!!,
                userPref.user.token!!,
            )
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({ commonResponse ->
                    if (commonResponse.responseStatus == "success") {
                        couponList.clear()
                        couponList.addAll(commonResponse.data)
                        couponAdapter = CouponAdapter(this, couponList)
                        rcyCoupon.adapter = couponAdapter

                        couponAdapter!!.setOnItemClickListener(object :
                            CouponAdapter.OnItemClickListener {
                            @SuppressLint("LogNotTimber")
                            override fun onItemClick(position: Int, view: View) {
                                when (view.id) {
                                    R.id.btnApply -> {
                                        applyCouponCode(couponList[position].coupon_code,
                                            binding!!.tvSubTotal.text.toString(), dialog)
                                    }
                                }
                            }
                        })

                    } else {
                        utils.simpleAlert(this, "", commonResponse.responseMessage)
                    }
                }, {
                    hideProgressDialog()
                    if (it is ConnectException) {

                    } else {
                        utils.simpleAlert(this, "", it.message.toString())
                    }
                }
                )
        }

        closeImg.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun showApplyCouponDialog(amount: String) {
        if (amount == "") {
            Toast.makeText(this, "Oops, Something went wrong", Toast.LENGTH_SHORT).show()
            return
        }
        val alertDialog = AlertDialog.Builder(this).create()
        val view = LayoutInflater.from(this).inflate(R.layout.apply_code_popup, null)
        val et_CouponCode = view.findViewById<EditText>(R.id.et_CouponCode)
        val btn_Cancel = view.findViewById<Button>(R.id.btn_Cancel)
        val btn_apply = view.findViewById<Button>(R.id.btn_apply)
        alertDialog.setView(view)
        btn_apply.setOnClickListener {
            val mCouponCode = et_CouponCode.text.toString().trim { it <= ' ' }
            if (!TextUtils.isEmpty(mCouponCode)) {

                //  couponCode = mCouponCode;
                // applyCouponCode(mCouponCode, amount, alertDialog)
            } else {
                Snackbar.make(view, "Please enter valid Coupon Code", Snackbar.LENGTH_SHORT).show()
            }
        }
        btn_Cancel.setOnClickListener { alertDialog.dismiss() }
        alertDialog.show()
        alertDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }


    private fun applyCouponCode(mCouponCode: String, amount: String, alertDialog: Dialog) {
        if (Utility.getInstance().checkInternetConnection(this)) {
            // Log.e("UserId", userPref.user.id + "");
            apiService.coupon(
                mCouponCode,
                amount,
                userPref.user.token!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({ response: ApplyCoupon ->
                    if (response.responseCode == "200") {
                        val couponData = response.data.data
                        if (couponData.status == "error") {
                            Toast.makeText(this, couponData.msg, Toast.LENGTH_SHORT).show()
                        } else {
                            binding!!.tvApply.text = "Applied"
                            binding!!.tvCouponDescription.text =
                                "You get discount of " + couponData.result.amount
                            couponDiscount = "" + couponData.result.amount
                            couponCode = mCouponCode
                            isCouponApplied = true
                            checkoutData
                            binding!!.frameCross.visibility = View.VISIBLE

                            /*  double discountedTotal = totalPrice - couponData.getResult().getAmount();
                                binding.tvTotalPrice.setText(""+discountedTotal);
                                binding.tvCouponDiscount.setText(""+couponData.getResult().getAmount());
                                double newAdvancePayable = discountedTotal * 0.10;
                                double newRemainingPayable = discountedTotal - newAdvancePayable;
                                advancePayable = newAdvancePayable;
                                remainPayable = newRemainingPayable;
                                binding.tvPayableAmount.setText(""+advancePayable);
                                binding.tvRemainingAmount.setText(""+remainPayable);
                                totalPrice = discountedTotal;
                                couponCode = mCouponCode;
                                couponDiscount = String.valueOf(couponData.getResult().getAmount());
                                isCouponApplied = true;*/

                            alertDialog.dismiss()
                        }
                    } else if (response.responseCode == "403") {
                        utils.openLogoutDialog(this, userPref)
                    } else {
                        hideProgressDialog()
                        Utility.simpleAlert(
                            this,
                            getString(R.string.info_dialog_title),
                            response.responseMessage
                        )
                    }
                }) { error: Throwable ->
                    try {
                        if (error is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                this,
                                getString(R.string.error),
                                getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            error.printStackTrace()
                            Utility.simpleAlert(this, "", getString(R.string.something_went_wrong))
                        }
                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + error.message)
                    }
                }
        } else {
            Utility.simpleAlert(
                this,
                getString(R.string.error),
                getString(R.string.check_network_connection)
            )
        }
    }

    private fun checkDayLimit(date: String, d: String) {
        Log.e("Date", "" + date)
        if (Utility.getInstance().checkInternetConnection(this)) {
            apiService.limitDay(
                date,
                userPref.user.token!!
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({ response: LimitDay ->
                    if (response.responseCode == "200") {
                        val todayOrder = response.data.today_order
                        val dayLimit = response.data.day_limit
                        val tOrder = todayOrder.toInt()
                        val tDayLimit = dayLimit.toInt()
                        Log.e("tOrder", "" + tOrder)
                        Log.e("tDayLimit", "" + tDayLimit)
                        if (tOrder < tDayLimit) {

                            //expectedDeliveryDate = date;
                            // expectedDeliveryDate = d
                            // binding!!.tvExpectedDeliveryDate.text = "Delivery date and time $d"
                        } else {
                            Utility.simpleAlert(
                                this,
                                getString(R.string.info_dialog_title),
                                getString(R.string.order_limit_reached)
                            )
                        }
                    } else if (response.responseCode == "403") {
                        utils.openLogoutDialog(this, userPref)
                    } else {
                        hideProgressDialog()
                        Utility.simpleAlert(
                            this,
                            getString(R.string.info_dialog_title),
                            response.responseMessage
                        )
                    }
                }) { error: Throwable ->
                    try {
                        if (error is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                this,
                                getString(R.string.error),
                                getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            error.printStackTrace()
                            Utility.simpleAlert(this, "", getString(R.string.something_went_wrong))
                        }
                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + error.message)
                    }
                }
        } else {
            Utility.simpleAlert(
                this,
                getString(R.string.error),
                getString(R.string.check_network_connection)
            )
        }
    }

    override fun onCheckedChanged(group: RadioGroup, checkedId: Int) {
        if (rbNetBanking!!.isChecked) {
            binding!!.llAdvanceRemainingAmount.visibility = View.VISIBLE
        } else if (rbCash!!.isChecked) {

            binding!!.llAdvanceRemainingAmount.visibility = View.VISIBLE
        }
    }
}