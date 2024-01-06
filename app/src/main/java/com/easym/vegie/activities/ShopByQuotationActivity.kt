package com.easym.vegie.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.easym.vegie.R
import com.easym.vegie.Utils.Utility
import com.easym.vegie.adapter.NewShopByQuotationRVAdapter
import com.easym.vegie.databinding.ActivityShopByQuotationBinding
import com.easym.vegie.model.shopbyquotation.Product
import com.google.gson.Gson
import kotlinx.android.synthetic.main.custom_actionbar_layout.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.ConnectException

class ShopByQuotationActivity : BaseActivity(), View.OnClickListener {

    lateinit var binding: ActivityShopByQuotationBinding
    lateinit var layoutManager: LinearLayoutManager

     var newShopByQuotationRVAdapter : NewShopByQuotationRVAdapter ?= null

    var LANGUAGECHANGEREQUESTCODE = 30

    var source: String = ""
    var menu_Id: String = ""
    var qty: String = ""
    var brand_Id : String = ""


    var mUpdatedQty = ""
    var mUpdatedPrice = ""
    var isAddedToCart = false

    var cartCount = 0
    var totalAmount = 0
    var minimumOrderLimit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_by_quotation)

        iv_Back.setOnClickListener(this)
        iv_Cart.setOnClickListener(this)
        iv_Search.setOnClickListener(this)


        binding.ivChangeLang.setOnClickListener(this)

        binding.buttonProceedToCheckOut.setOnClickListener {

            if (!TextUtils.isEmpty(tv_CartCount.text.toString())) {

                val intent = Intent(this, FragmentContainerActivity::class.java)
                intent.putExtra("FragmentName", "My Cart List Fragment")
                intent.putExtra("couponDiscount", "0")
                intent.putExtra("couponCode", "")
                intent.putExtra("checkout_type", "cart")
                intent.putExtra("quoatationId", "")
                startActivity(intent)

            } else {
                Toast.makeText(this, "No item found in your cart", Toast.LENGTH_SHORT).show()
            }

        }

        val bundle = intent.extras
        if (bundle != null) {

            source = bundle.getString("source")!!
            menu_Id = bundle.getString("menu_Id")!!
            qty = bundle.getString("qty")!!
            brand_Id = bundle.getString("brand_Id")!!
        }


        binding.tvShopByQuotation.isEnabled = false
        binding.tvShopByCategory.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()

        getCartCount()
        getQuotationProductList("")

    }

    override fun onClick(p0: View?) {

        when (p0!!.id) {

            R.id.iv_Back -> finish()

            R.id.iv_Cart -> {

                if (cartCount == 0) {

                    val intent = Intent(this, FragmentContainerActivity::class.java)
                    intent.putExtra("FragmentName", "My Cart List Fragment")

                    if (!source.equals("")) {
                        intent.putExtra("source", source)
                        intent.putExtra("menu_Id", menu_Id)
                        intent.putExtra("qty", qty)
                        intent.putExtra("brand_Id", brand_Id)
                    }
                    startActivity(intent)

                } else {

                    if (totalAmount >= minimumOrderLimit) {

                        val intent = Intent(this, FragmentContainerActivity::class.java)
                        intent.putExtra("FragmentName", "My Cart List Fragment")

                        if (!source.equals("")) {
                            intent.putExtra("source", source)
                            intent.putExtra("menu_Id", menu_Id)
                            intent.putExtra("qty", qty)
                            intent.putExtra("brand_Id", brand_Id)
                        }
                        startActivity(intent)

                    } else {
                        val intent = Intent(this, FragmentContainerActivity::class.java)
                        intent.putExtra("FragmentName", "My Cart List Fragment")

                        if (!source.equals("")) {
                            intent.putExtra("source", source)
                            intent.putExtra("menu_Id", menu_Id)
                            intent.putExtra("qty", qty)
                            intent.putExtra("brand_Id", brand_Id)
                        }
                        startActivity(intent)

/*                        utils.simpleAlert(
                            this,
                            getString(R.string.minimum_order_amount),
                            getString(R.string.minimum_order_amount_limit_is) + " " + minimumOrderLimit
                        )*/
                    }
                }

            }

            R.id.tv_shopByCategory -> {
                val intent4 = Intent(this,
                        ShopByCategoryActivity::class.java)
                if (!source.equals("")) {
                    intent4.putExtra("source", source)
                    intent4.putExtra("menu_Id", menu_Id)
                    intent4.putExtra("qty", qty)
                    intent4.putExtra("brand_Id", brand_Id)

                }
                startActivity(intent4)
                finish()

            }

            R.id.iv_ChangeLang -> {
                val intent3 = Intent(this, FragmentContainerActivity::class.java)
                intent3.putExtra("FragmentName", "Change Language Fragment")
                intent3.putExtra("IsForResult", "True")
                startActivityForResult(intent3, LANGUAGECHANGEREQUESTCODE)
            }

            R.id.iv_Search -> {
                val intent2 = Intent(this, FragmentContainerActivity::class.java)
                intent2.putExtra("FragmentName", "Search Fragment")
                startActivity(intent2)
            }
        }
    }

    private fun getQuotationProductList(selectedBrandId: String) {

        if (Utility.getInstance().checkInternetConnection(this)) {
            //Log.e("UserId", userPref.user.id + "")
            apiService.getProductQuotation(
                    "quotation",
                    userPref.user.id!!,
                    userPref.user.token!!,
                    userPref.getUserPreferLanguageCode())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe({ this.showProgressDialog() })
                    .doOnCompleted({ this.hideProgressDialog() })
                    .subscribe({

                        when {
                            it.responseCode.equals("200") -> {
                                val list = it.data.quotationList.result
                                var total = 0

                                val listProduct = arrayListOf<Product>()

                                for (i in 0..list.size - 1) {

                                    //For Category Tilte
                                    val product = Product()
                                    val categoryName = list.get(i).name
                                    val productList = list.get(i).product
                                    val other_category_name = list.get(i).other_category_name
                                    product.view_type = "CategoryTitle"
                                    product.category_name = categoryName
                                    product.other_category_name = other_category_name
                                    listProduct.add(product)

                                    //For Header Item
                                    val product1 = Product()
                                    product1.view_type = "HeaderTitle"
                                    product1.category_name = ""
                                    listProduct.add(product1)

                                    //For Product Item
                                    for (x in 0..productList.size - 1) {
                                        val product = productList.get(x)
                                        product.view_type = ""
                                        product.category_name = ""
                                        product.qty_in_cart = productList.get(x).qty_in_cart
                                        listProduct.add(product)
                                        if (product.is_cart) {

                                            val unitPriceDouble = java.lang.Double.valueOf(String.format("%.2f", java.lang.Double.valueOf(productList.get(x).price)))
                                            val qtyDouble = java.lang.Double.valueOf(String.format("%.2f", java.lang.Double.valueOf(productList.get(x).qty_in_cart)))

                                            val mUpdatedPrice = unitPriceDouble * qtyDouble

                                            val roundedPrice = java.lang.Double.valueOf(String.format("%.2f", mUpdatedPrice))

                                            total = (total + (roundedPrice).toFloat()).toInt()
                                        }
                                    }
                                }

                                //For Total
                                val product = Product()
                                product.view_type = "Total"
                                listProduct.add(product)


                                Log.e("ListProductSize", "" + listProduct.size)
                                Log.e("ListProduct", Gson().toJson(listProduct))



                                layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


                                newShopByQuotationRVAdapter = NewShopByQuotationRVAdapter(
                                    this, listProduct, object : NewShopByQuotationRVAdapter.Cart {

                                        override fun updateQty() {
                                            getCartCount()
                                        }

                                        override fun removeFromCart(id: String?, brandId: String?) {
                                            Log.e("BrandId", "" + brandId)

                                            if (!TextUtils.isEmpty(brandId)) {
                                                removeItemFromCart(id!!, brandId!!)
                                            } else {
                                                removeItemFromCart(id!!, "0")
                                            }
                                        }

                                        override fun addToCart(menuId: String?, qty: String?, brandId: String?, updatedPrice: String?) {

                                            if (!TextUtils.isEmpty(brandId)) {
                                                addIntoCart(menuId!!, qty!!, brandId!!, updatedPrice)
                                            } else {
                                                addIntoCart(menuId!!, qty!!, "0", updatedPrice)
                                            }
                                        }

                                    }, apiService, userPref, total, selectedBrandId, utils)

                                /*  val viewTypeList = arrayOf("Header","ProductItem","TotalAmount")

                                                  shopByCategoryRVAdapter = ShopByQuotationRVAdapter(this,

                                                          viewTypeList,list,total.toString(), object : ShopByQuotationRVAdapter.Cart {

                                                              override fun removeFromCart(menuId: String?) {
                                                                  removeItemFromCart(menuId!!)
                                                              }
                                                              override fun addToCart(menuId: String?, qty: String?, brandId: String?) {
                                                                  addIntoCart(menuId!!,qty!!,brandId!!)
                                                              }
                                                          },apiService,userPref)*/
                                // binding.rvShopByQuotation.adapter = shopByCategoryRVAdapter


                                binding.rvShopByQuotation.adapter = newShopByQuotationRVAdapter
                                binding.rvShopByQuotation.layoutManager = layoutManager
                                binding.rvShopByQuotation.setItemViewCacheSize(listProduct.size)

                            }
                            it.getResponseCode() == "403" -> {
                                utils.openLogoutDialog(this, userPref)
                            }
                            else -> {
                                hideProgressDialog()
                                Utility.simpleAlert(this, getString(R.string.info_dialog_title), it.getResponseMessage())
                            }
                        }

                    }, {

                        try {

                            if (it is ConnectException) {
                                hideProgressDialog()
                                Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection))
                            } else {
                                hideProgressDialog()
                                Utility.simpleAlert(this, getString(R.string.error), it.message)
                            }

                        } catch (ex: Exception) {
                            Log.e("", "Within Throwable Exception::" + it.message)
                        }

                    })
        } else {
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection))
        }

    }


    private fun addIntoCart(menuId: String, qty: String, brandId: String, updatedPrice: String?) {

        Log.e("QtyOfItemAdded", "InCart" + qty);

        if (Utility.getInstance().checkInternetConnection(this)) {

            val rbUserId  = RequestBody.create("text/plain".toMediaTypeOrNull(), userPref.user.id!!)
            val rbMenuId  = RequestBody.create("text/plain".toMediaTypeOrNull(), menuId)
            val rbQty = RequestBody.create("text/plain".toMediaTypeOrNull(), qty)
            val rbVarient = RequestBody.create("text/plain".toMediaTypeOrNull(), "")
            val rbBrandId = RequestBody.create("text/plain".toMediaTypeOrNull(), brandId)
            val rbToken = RequestBody.create("text/plain".toMediaTypeOrNull(), userPref.user.token!!)



            apiService.addCart(
                    rbUserId,
                    rbMenuId, rbQty,
                    rbVarient, rbBrandId,
                    rbToken)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgressDialog() }
                    .doOnCompleted { hideProgressDialog() }
                    .subscribe({

                        if (it.responseCode.equals("200")) {

                            mUpdatedQty = qty
                            mUpdatedPrice = updatedPrice!!
                            isAddedToCart = true

                            getCartCount()

                            if (TextUtils.isEmpty(brandId) || brandId.equals("0")) {
                                getQuotationProductList(brandId)
                            }

                            Toast.makeText(this, it.responseMessage, Toast.LENGTH_SHORT).show()

                        } else if (it.responseCode == "403") {
                            utils.openLogoutDialog(this, userPref)
                        } else {

                            hideProgressDialog()
                            Utility.simpleAlert(this, getString(R.string.info_dialog_title), it.responseMessage)
                        }

                    }, {

                        try {

                            if (it is ConnectException) {
                                hideProgressDialog()
                                Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection))
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
            hideProgressDialog()
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection))
        }

    }

    private fun removeItemFromCart(menuId: String, brandId: String) {

        if (Utility.getInstance().checkInternetConnection(this)) {
            apiService.removeFromCart(
                    userPref.user.id!!,
                    menuId,
                    userPref.user.token!!,
                    brandId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgressDialog() }
                    .doOnCompleted { hideProgressDialog() }
                    .subscribe({

                        if (it.responseCode.equals("200")) {

                            isAddedToCart = false
                            getCartCount()
                            if (TextUtils.isEmpty(brandId) || brandId.equals("0")) {
                                getQuotationProductList(brandId)
                            }

                            Toast.makeText(this, it.responseMessage, Toast.LENGTH_SHORT).show()

                        } else if (it.getResponseCode() == "403") {
                            utils.openLogoutDialog(this, userPref)
                        } else {

                            hideProgressDialog()
                            Utility.simpleAlert(this, getString(R.string.info_dialog_title), it.responseMessage)
                        }

                    }, {

                        try {

                            if (it is ConnectException) {
                                hideProgressDialog()
                                Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection))
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
            hideProgressDialog()
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection))
        }

    }

    private fun getCartCount() {

        if (Utility.getInstance().checkInternetConnection(this)) {
            apiService.userCartCount(
                    userPref.user.id!!,
                    userPref.user.token!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({

                        if (it.responseCode.equals("200")) {

                            //Toast.makeText(this,it.responseMessage,Toast.LENGTH_SHORT).show()
                            cartCount = it.getData().getCount_cart()
                            totalAmount = if (it.getData().getTotal_amount() != null) {
                                it.getData().getTotal_amount()
                            } else {
                                0
                            }

                            minimumOrderLimit = it.getData().getMinimum_order_limit()

                            if (cartCount != 0) {
                                tv_CartCount.setText("" + cartCount)
                                Log.d("UserCartCountApi", "" + cartCount)
                            }else {
                                tv_CartCount.text = ""

                            }

                        }
                        else if (it.getResponseCode() == "403") {
                            utils.openLogoutDialog(this, userPref)
                        } else {

                            tv_CartCount.setText("")
                            Log.d("UserCartCountApi", "" + it.responseMessage)

                        }

                    }, {

                        try {

                            if (it is ConnectException) {
                                Log.d("UserCartCountApi", "" + getString(R.string.check_network_connection))
                            } else {
                                Log.d("UserCartCountApi", "" + it.message)
                            }

                        } catch (ex: Exception) {
                            Log.e("", "Within Throwable Exception::" + it.message)
                        }

                    })

        } else {
            Log.d("UserCartCountApi", "" + getString(R.string.check_network_connection))
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LANGUAGECHANGEREQUESTCODE && resultCode == Activity.RESULT_OK) {

            Log.e("Lang Code", userPref.getUserPreferLanguageCode() + "")

            getCartCount()
            getQuotationProductList("")

        }
    }


    override fun onPause() {
        super.onPause()

        if(newShopByQuotationRVAdapter != null){
            if(newShopByQuotationRVAdapter!!.popupWindow != null){
                newShopByQuotationRVAdapter!!.popupWindow.dismiss()
            }
        }

    }
}