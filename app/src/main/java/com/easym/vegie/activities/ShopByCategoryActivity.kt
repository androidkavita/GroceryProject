package com.easym.vegie.activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.easym.vegie.R
import com.easym.vegie.Utils.Utility
import com.easym.vegie.adapter.ViewPagerAdapter
import com.easym.vegie.databinding.ActivityShopByCategoryBinding
import com.easym.vegie.fragment.MainCategoryShoppingFragment
import com.google.gson.Gson
import kotlinx.android.synthetic.main.custom_actionbar_layout.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.ConnectException

class ShopByCategoryActivity : BaseActivity(), View.OnClickListener {

    lateinit  var binding : ActivityShopByCategoryBinding
    lateinit var adapter : ViewPagerAdapter

    var LANGUAGECHANGEREQUESTCODE = 30
   // lateinit var mainCategoryShoppingFragment: MainCategoryShoppingFragment

    var source : String = ""
    var menu_Id : String = ""
    var qty : String = ""
    var brand_Id : String = ""
    var categoryPosition = ""
    var quotationId = ""

    var cartCount = 0
    var totalAmount = 0
    var minimumOrderLimit = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_shop_by_category)
        val bundle = intent.extras
        if (intent.hasExtra("quotationId")){
            quotationId = bundle!!.getString("quotationId")!!

        }
        setupViewPager(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        iv_Back.setOnClickListener(this)
        iv_Search.setOnClickListener(this)
        iv_Cart.setOnClickListener(this)
        binding.ivChangeLang.setOnClickListener(this)


        if(bundle != null){
            if(intent.hasExtra("source")){
                source = bundle.getString("source")!!
            }

            if(intent.hasExtra("menu_Id")) {
                menu_Id = bundle.getString("menu_Id")!!
            }

            if(intent.hasExtra("qty")) {
                qty = bundle.getString("qty")!!
            }

            if(intent.hasExtra("brand_Id")) {
                brand_Id = bundle.getString("brand_Id")!!
            }

            if(intent.hasExtra("position")) {
                categoryPosition = bundle.getString("position")!!
            }


        }

        binding.tvShopByCategory.isEnabled = false
        binding.tvShopByQuotation.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        getCartCount()

        setupViewPager(binding.viewPager)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        Log.e("OnResumeCalled", "Called")

    }


    private fun setupViewPager(viewPager: ViewPager) {
       getCategory(viewPager)
    }

    private fun getCategory(viewPager: ViewPager) {
        if (Utility.getInstance().checkInternetConnection(this)) {
            apiService.getCategorySubCategory(
                    userPref.user.id!!,
                    userPref.user.token!!,
                    userPref.getUserPreferLanguageCode())
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { this.showProgressDialog() }
                .doOnCompleted { this.hideProgressDialog() }
                .subscribe({
                        when {
                            it.responseCode.equals("200") -> {
                                val getProductCategoryWise = it.data
                                val categoriesName = getProductCategoryWise.caegoryName
                                val size = categoriesName.size
                                adapter = ViewPagerAdapter(supportFragmentManager)
                                for (item in categoriesName) {
                                    val mainCategoryFragment = MainCategoryShoppingFragment()
                                    val bundle = Bundle()
                                    val categoryResponse = Gson().toJson(item)

                                    bundle.putString("CategoryResponse", categoryResponse)
                                    bundle.putString("quotationId", quotationId)
                                    mainCategoryFragment.arguments = bundle

                                    Log.e("CategoryResponse", categoryResponse)

                                    if (!item.other_name.equals("false")) {
                                        adapter.addFragment(mainCategoryFragment, item.name + "\n" + item.other_name)
                                    } else {
                                        adapter.addFragment(mainCategoryFragment, item.name)
                                    }
                                    viewPager.adapter = adapter

                                    if (!TextUtils.isEmpty(categoryPosition)) {
                                        viewPager.currentItem = Integer.parseInt(categoryPosition)
                                    }
                                }
                            }

                            it.responseCode == "403" -> {
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
                                it.printStackTrace()
                                Utility.simpleAlert(this, "", getString(R.string.something_went_wrong))
                            }

                        } catch (ex: Exception) {
                            Log.e("", "Within Throwable Exception::" + it.message)
                        }

                    })
        } else {
            Utility.simpleAlert(this, getString(R.string.error), getString(R.string.check_network_connection))
        }
    }



    override fun onClick(v: View?) {
        val id = v!!.id

        when(id){
            R.id.iv_Back -> {
                finish()
            }

            R.id.iv_Cart -> {
                // startActivity(Intent(this, MyCartActivity::class.java))
                /*Intent intent4 = new Intent(mContext, MyCartActivity.class);
                startActivity(intent4);*/
                if (cartCount == 0) {
                    val intent = Intent(this, FragmentContainerActivity::class.java)
                    intent.putExtra("FragmentName", "My Cart List Fragment")
                    if (source != "") {
                        intent.putExtra("source", source)
                        intent.putExtra("menu_Id", menu_Id)
                        intent.putExtra("qty", qty)
                        intent.putExtra("brand_Id", brand_Id)
                        intent.putExtra("quotationId", quotationId)
                    }
                    startActivity(intent)
                } else {
                    if (totalAmount >= minimumOrderLimit) {
                        val intent = Intent(this, FragmentContainerActivity::class.java)
                        intent.putExtra("FragmentName", "My Cart List Fragment")
                        if (source != "") {
                            intent.putExtra("source", source)
                            intent.putExtra("menu_Id", menu_Id)
                            intent.putExtra("qty", qty)
                            intent.putExtra("brand_Id", brand_Id)
                            intent.putExtra("quotationId", quotationId)
                        }
                        startActivity(intent)

                    } else {
                        val intent = Intent(this, FragmentContainerActivity::class.java)
                        intent.putExtra("FragmentName", "My Cart List Fragment")
                        if (source != "") {
                            intent.putExtra("source", source)
                            intent.putExtra("menu_Id", menu_Id)
                            intent.putExtra("qty", qty)
                            intent.putExtra("brand_Id", brand_Id)
                            intent.putExtra("quotationId", quotationId)
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
            R.id.iv_Search -> {
                val intent2 = Intent(this, FragmentContainerActivity::class.java)
                intent2.putExtra("FragmentName", "Search Fragment")
                startActivity(intent2)
            }

            R.id.tv_shopByQuotation -> {

                val intent4 = Intent(this, ShopByQuotationActivity::class.java)
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
        }
    }

    private fun getCartCount(){

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
                            }else{
                                tv_CartCount.text = ""

                            }

                        } else if (it.getResponseCode() == "403") {
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

        if(requestCode == LANGUAGECHANGEREQUESTCODE && resultCode == Activity.RESULT_OK) {

            Log.e("Lang Code", userPref.getUserPreferLanguageCode() + "")
            finish()
            val intent = Intent(this, ShopByCategoryActivity::class.java)
            if(!source.equals("")){
                intent.putExtra("source", source)
                intent.putExtra("menu_Id", menu_Id)
                intent.putExtra("qty", qty)
            }
            startActivity(intent)
        }
    }
}