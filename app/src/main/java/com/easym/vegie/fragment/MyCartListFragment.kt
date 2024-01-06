package com.easym.vegie.fragment

import android.Manifest
import android.app.Activity
import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.print.PrintAttributes
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.easym.vegie.R
import com.easym.vegie.Utils.ShareBy
import com.easym.vegie.Utils.Utility
import com.easym.vegie.activities.FragmentContainerActivity
import com.easym.vegie.activities.HomePageActivity
import com.easym.vegie.activities.ShopByCategoryActivity
import com.easym.vegie.activities.UserAddressListActivity
import com.easym.vegie.adapter.MyCartListAdapter
import com.easym.vegie.databinding.MyCartListFragmentLayoutBinding
import com.easym.vegie.model.mycartlist.Product
import com.google.gson.Gson
import com.skbfinance.BaseFragment
import com.uttampanchasara.pdfgenerator.CreatePdf
import kotlinx.coroutines.launch
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.net.ConnectException

class MyCartListFragment : BaseFragment(), View.OnClickListener {

    private var limitAmount: Double? = null
    private var total: Double? = null
    lateinit var binding: MyCartListFragmentLayoutBinding
    lateinit var myCartAdapter: MyCartListAdapter

    var LANGUAGECHANGEREQUESTCODE = 30
    var source = ""
    var menuIdStr = ""
    var qtyStr = ""
    var brandId = ""
    var quotationId = ""
    val REQUESTSTORAGEPERMISSION = 255
//    val string:String by lazy { String() }


    var menuIDD: List<String> = ArrayList()
    var list = arrayListOf<Product>()
    var listProduct = arrayListOf<Product>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater,
            R.layout.my_cart_list_fragment_layout,
            container,
            false)
        Log.d("UserId", userPref.user.id + "")
        Toast.makeText(requireContext(), ""+userPref.user.id, Toast.LENGTH_SHORT).show()
        binding.ivChangeLang.setOnClickListener(this)


        val bundle = arguments
        if (bundle != null) {
            source = bundle.getString("source")!!
            menuIdStr = bundle.getString("menu_Id")!!
            qtyStr = bundle.getString("qty")!!
            brandId = bundle.getString("brand_Id")!!
            quotationId = bundle.getString("quotationId")!!

            Log.e("MyCartSource", "" + source)
            Log.e("MyCartMenuId", "" + menuIdStr)
            Log.e("MyCartQty", "" + qtyStr)
            Log.e("quotationId", "" + quotationId)
        }

        return binding.root

    }


    override fun onResume() {
        super.onResume()
        getQuotationProductList()

    }


    private fun getCheckQauntiy(address_id: String, quotation_id: String, checkout_type: String) {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.checkQuality(
                userPref.user.id!!,
                userPref.user.token!!,
                address_id,
                quotation_id,
                checkout_type, )

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({

                    when {
                        it.responseCode.equals("200") -> {
                            val intent = Intent(context, UserAddressListActivity::class.java)
                            intent.putExtra("couponDiscount", "0")
                            intent.putExtra("couponCode", "")
                            intent.putExtra("checkout_type", "cart")
                            intent.putExtra("quoatationId", "")
                            startActivity(intent)
                            //Toast.makeText(context,it.responseMessage, Toast.LENGTH_SHORT).show()

                        }
                        it.responseCode == "403" -> {
                            utils.openLogoutDialog(requireContext(), userPref)
                        }
                        it.responseCode == "400" -> {
                            val intent = Intent(context, HomePageActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)

                            Toast.makeText(context, it.responseMessage, Toast.LENGTH_LONG).show()
                        }
                        else -> {
                            hideProgressDialog()
                            Utility.simpleAlert(context,
                                getString(R.string.info_dialog_title),
                                it.responseMessage)
                        }
                    }

                }, {

                    try {
                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(context,
                                getString(R.string.error),
                                getString(R.string.check_network_connection))
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(context,
                                "",
                                getString(R.string.something_went_wrong))
                        }

                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }

                })

        } else {
            hideProgressDialog()
            Utility.simpleAlert(context,
                getString(R.string.error),
                getString(R.string.check_network_connection))
        }
    }

//
    private fun getQuotationProductList() {

        if (Utility.getInstance().checkInternetConnection(context)) {

            apiService.getCartList(
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
                            list.clear()
                            listProduct.clear()
                            val getCartListData = it.data.getCartList()!!.getResult()!!
                            try {
                                total = it.data.getCartList()!!.getTotal() as Double
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            try {
                                limitAmount = it.data.getCartList()!!.getLimitAmount() as Double
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            binding.tvMinimumOrderAmount.text = "Minimum order amount is Rs. " + it.data.getCartList()!!
                                .getLimitAmount()
                            binding.tvMinimumOrderAmount.visibility = View.VISIBLE


                            var serialNo = 1
                            //For Header Item
                            val product1 = Product()
                            product1.view_type = "HeaderTitle"
                            product1.category_name = ""
                            listProduct.add(product1)

                            for (i in 0..getCartListData.size - 1) {

                                //For Category Tilte
                                val product = Product()
                                val categoryName = getCartListData[i]!!.getName()!!
                                val productList = getCartListData[i]!!.getProduct()!!
                                val otherCategoryName = getCartListData[i]!!.getOtherCategoryName()!!
                                product.view_type = "CategoryTitle"
                                product.category_name = categoryName
                                product.other_category_name = otherCategoryName
                                listProduct.add(product)

                                //For Product Item
                                for (x in 0..productList.size - 1) {

                                    val product = productList[x]
                                    list.add(product!!)
                                    product.view_type = ""
                                    product.category_name = ""
                                    product.serialNo = serialNo.toString()
                                    serialNo++
                                    listProduct.add(product)

                                }

                                val product2 = Product()
                                product2.view_type = "CategorySubTotal"
                                product2.category_name = ""
                                product2.categorySubtotal = (getCartListData[i]!!.getSubtotal() as Double?)!!
                                listProduct.add(product2)

                            }

                            //For Total
                            val product = Product()
                            product.view_type = "Total"
                            listProduct.add(product)

                            Log.e("ListProductSize", "" + listProduct.size)
                            Log.e("ListProduct", Gson().toJson(listProduct))

                            val linearLayoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                            myCartAdapter = MyCartListAdapter(
                                context, listProduct,
                                it.data!!.getCartList()!!.getTotal().toString(),
                                object : MyCartListAdapter.Cart {
                                    override fun removeFromCart(menuId: String?, brandId: String?) {
                                        removeItemFromCart(menuId!!, brandId!!)
                                    }

                                }, object : MyCartListAdapter.AddMore {
                                    override fun addMoreButton() {
                                        if(quotationId!="") {
                                            startActivity(Intent(context,
                                                ShopByCategoryActivity::class.java))
                                        }
                                        else
                                        {
                                            startActivity(Intent(context,
                                                ShopByCategoryActivity::class.java))
                                        }
                                    }

                                    override fun updateQty(
                                        qty: String?,
                                        position: Int,
                                        brandId: String) {
                                        updateProductQty(qty!!, listProduct[position].id, listProduct[position].menuId, brandId)

                                    }
                                })

                            binding.rvMyCart.adapter = myCartAdapter
                            binding.rvMyCart.layoutManager = linearLayoutManager

                            binding.buttonProceedToPay.setOnClickListener {
                                if (limitAmount != null && total != null) {
                                    if (total!! > limitAmount!!) {
                                        getCheckQauntiy("", "", "cart")
                                        /*val intent = Intent(context, UserAddressListActivity::class.java)
                                                    intent.putExtra("couponDiscount", "0")
                                                    intent.putExtra("couponCode", "")
                                                    intent.putExtra("checkout_type", "cart")
                                                    intent.putExtra("quoatationId", "")
                                                    startActivity(intent)*/
                                    } else {
                                        Toast.makeText(context,
                                            "Please maintain the minimum order amount",
                                            Toast.LENGTH_SHORT).show()
                                    }
                                } else {
                                    getCheckQauntiy("", "", "cart")

                                    /* val intent = Intent(context, UserAddressListActivity::class.java)
                                                 intent.putExtra("couponDiscount", "0")
                                                 intent.putExtra("couponCode", "")
                                                 intent.putExtra("checkout_type", "cart")
                                                 intent.putExtra("quoatationId", "")
                                                 startActivity(intent)*/
                                }
                            }


                            binding.buttonSaveAsQuatation.setOnClickListener {
                                val menuIdsList: List<String> = list.map { it.menuId!! }
                                val qty_List: List<String> = list.map { it.qty }
                                val brand_id_list: List<String> = list.map { it.mBrandId }

                                Log.e("@@menuIdsList", "$menuIdsList" )
                                Log.e("@@qty_List", "$qty_List" )
                                Log.e("@@brand_id_list", "$brand_id_list" )

                                val menu_Id_ = TextUtils.join(",", menuIdsList)
                                val qty_ = TextUtils.join(",", qty_List)
                                val brand_Id_ = TextUtils.join(",", brand_id_list)

                                Log.e("@@@menu_Id_", "$menu_Id_" )
                                Log.e("@@@qty_", "$qty_" )
                                Log.e("@@@brand_Id_", "$brand_Id_" )

                            /*    if (source != "" && menuIdStr != "" && qtyStr != "" && brandId != "") {
                                    val menuIdArr = menuIdStr.split(",")
                                    val qtyArr = qtyStr.split(",")
                                    val brandArr = brandId.split(",")

                                    for (i in menuIdArr.indices) {
                                        if (menuIdsList.contains(menuIdArr[i])) {
                                            //Do Nothing
                                        } else {
                                            // Add menuId in string
                                            // Add qty in String
                                            menu_Id_ = menu_Id_ + "," + menuIdArr[i]
                                            qty_ = qty_ + "," + qtyArr[i]
                                            brand_Id_ = brand_Id_ + "," + brandArr[i]
                                        }
                                    }
                                }*/

 /*                               Log.e("@@menu_Id", "" + menu_Id_)
                                Log.e("@@qty", "" + qty_)
                                Log.e("@@brandId", "" + brand_Id_)*/

                                if (quotationId==""){
                                    saveAsQuotation(menu_Id_, qty_, brand_Id_)
                                }else{
                                    updateQuotation(menu_Id_, qty_, brand_Id_)
                                }
                            }

                            binding.buttonShare.setOnClickListener {

                                if (checkStoragePermission()) {
                                    callGenerateMyCartPdfAPI()
                                } else {
                                    askStoragePermission()
                                }
                            }
                            binding.llParent.visibility = View.VISIBLE
                        }

                        it.responseCode == "403" -> { utils.openLogoutDialog(requireContext(), userPref)
                        }

                        it.responseCode == "405" -> {
                            startActivity(Intent(context, ShopByCategoryActivity::class.java))
                            Toast.makeText(context, it.responseMessage, Toast.LENGTH_LONG).show()
                        }

                        else -> {
                            binding.tvMinimumOrderAmount.visibility = View.GONE
                            binding.llParent.visibility = View.GONE

                            hideProgressDialog()
                            Utility.simpleAlert(context,
                                getString(R.string.info_dialog_title),
                                it.responseMessage)
                        }
                    }
                }, {

                    try {
                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(context,
                                getString(R.string.error),
                                getString(R.string.check_network_connection))
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(context,
                                "",
                                getString(R.string.something_went_wrong))
                        }

                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }

                })
        } else {
            Utility.simpleAlert(context,
                getString(R.string.error),
                getString(R.string.check_network_connection))
        }
    }


    private fun removeItemFromCart(menuId: String, brandId: String) {

        if (Utility.getInstance().checkInternetConnection(context)) {
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
                    when {
                        it.responseCode.equals("200") -> {
                            Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT).show()
                            getQuotationProductList()

                        }
                        it.responseCode == "403" -> {
                            utils.openLogoutDialog(requireContext(), userPref)
                        }
                        else -> {
                            hideProgressDialog()
                            Utility.simpleAlert(context,
                                getString(R.string.info_dialog_title),
                                it.responseMessage)
                        }
                    }

                }, {

                    try {
                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(context,
                                getString(R.string.error),
                                getString(R.string.check_network_connection))
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(context,
                                "",
                                getString(R.string.something_went_wrong))
                        }

                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }

                })

        } else {
            hideProgressDialog()
            Utility.simpleAlert(context,
                getString(R.string.error),
                getString(R.string.check_network_connection))
        }

    }

    private fun saveAsQuotation(menuId: String, qty: String, brandId: String) {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.addUserQuotation(
                userPref.user.id!!,
                "quotation-" + System.currentTimeMillis(),
                menuId,
                qty,
                userPref.user.token!!,
                brandId,
                quotationId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({

                    if (it.responseCode.equals("200")) {

                        Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT).show()
                        getQuotationProductList()

                    } else if (it.responseCode == "403") {
                        utils.openLogoutDialog(requireContext(), userPref)
                    } else {

                        hideProgressDialog()
                        Utility.simpleAlert(context,
                            getString(R.string.info_dialog_title),
                            it.responseMessage)
                    }

                }, {

                    try {
                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(context,
                                getString(R.string.error),
                                getString(R.string.check_network_connection))
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(context,
                                "",
                                getString(R.string.something_went_wrong))
                        }
                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }
                })

        } else {
            hideProgressDialog()
            Utility.simpleAlert(context,
                getString(R.string.error),
                getString(R.string.check_network_connection))
        }
    }

    private fun updateQuotation(menuId: String, qty: String, brandId: String) {
        if (Utility.getInstance().checkInternetConnection(context)) {
            if (menuId != null) {
                apiService.updateUserQuotation(
                    userPref.user.id!!,
                    menuId,
                    qty,
                    userPref.user.token!!,
                    brandId,
                    quotationId)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe { showProgressDialog() }
                    .doOnCompleted { hideProgressDialog() }
                    .subscribe({

                        if (it.responseCode.equals("200")) {
                            Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT).show()
                            getQuotationProductList()

                        } else if (it.responseCode == "403") {
                            utils.openLogoutDialog(requireContext(), userPref)
                        } else {

                            hideProgressDialog()
                            Utility.simpleAlert(context,
                                getString(R.string.info_dialog_title),
                                it.responseMessage)
                        }

                    }, {

                        try {
                            if (it is ConnectException) {
                                hideProgressDialog()
                                Utility.simpleAlert(context,
                                    getString(R.string.error),
                                    getString(R.string.check_network_connection))
                            } else {
                                hideProgressDialog()
                                it.printStackTrace()
                                Utility.simpleAlert(context,
                                    "",
                                    getString(R.string.something_went_wrong))
                            }
                        } catch (ex: Exception) {
                            Log.e("", "Within Throwable Exception::" + it.message)
                        }
                    })
            }

        }
        else {
            hideProgressDialog()
            Utility.simpleAlert(context,
                getString(R.string.error),
                getString(R.string.check_network_connection))
        }
    }

    private fun updateProductQty(qty: String, itemId: String, menuId: String, brandId: String) {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.updateCartItem(
                qty,
                itemId,
                menuId,
                userPref.user.token!!, brandId)

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({
                    when {
                        it.responseCode.equals("200") -> {
                            //Toast.makeText(context,it.responseMessage, Toast.LENGTH_SHORT).show()
                            getQuotationProductList()
                        }
                        it.responseCode == "403" -> {
                            utils.openLogoutDialog(requireContext(), userPref)
                        }
                        else -> {
                            hideProgressDialog()
                            Utility.simpleAlert(context,
                                getString(R.string.info_dialog_title),
                                it.responseMessage)
                        }
                    }

                }, {

                    try {
                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(context,
                                getString(R.string.error),
                                getString(R.string.check_network_connection))
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(context,
                                "",
                                getString(R.string.something_went_wrong))
                        }
                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }
                })

        } else {
            hideProgressDialog()
            Utility.simpleAlert(context,
                getString(R.string.error),
                getString(R.string.check_network_connection))
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.iv_ChangeLang -> {
                val intent3 = Intent(context, FragmentContainerActivity::class.java)
                intent3.putExtra("FragmentName", "Change Language Fragment")
                intent3.putExtra("IsForResult", "True")
                startActivityForResult(intent3, LANGUAGECHANGEREQUESTCODE)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LANGUAGECHANGEREQUESTCODE && resultCode == Activity.RESULT_OK) {
            Log.e("Lang Code1", userPref.getUserPreferLanguageCode() + "")
            getQuotationProductList()
        }
    }

    private fun checkStoragePermission(): Boolean {
        return (ActivityCompat.checkSelfPermission(requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun askStoragePermission() {

        requestPermissions(arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
        ), REQUESTSTORAGEPERMISSION)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUESTSTORAGEPERMISSION) {

            if (grantResults.size != 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED
            ) {

                callGenerateMyCartPdfAPI()

            } else {
                Toast.makeText(requireContext(), "Permission required", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun callGenerateMyCartPdfAPI() {

        apiService.generateShareCartPage(
            userPref.user.id.toString(),
            userPref.getUserPreferLanguageCode(),
            userPref.user.token!!
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgressDialog() }
            .doOnCompleted { hideProgressDialog() }
            .subscribe({

                if (it.responseCode.equals("200")) {

                    //  Toast.makeText(context,it.responseMessage, Toast.LENGTH_SHORT).show()
                    generateMyCartPdf(it.data.toString())

                } else if (it.responseCode == "403") {
                    utils.openLogoutDialog(requireContext(), userPref)
                } else {
                    hideProgressDialog()
                    Utility.simpleAlert(context,
                        getString(R.string.info_dialog_title),
                        it.responseMessage)
                }

            }, {

                try {

                    if (it is ConnectException) {
                        hideProgressDialog()
                        Utility.simpleAlert(context,
                            getString(R.string.error),
                            getString(R.string.check_network_connection))
                    } else {
                        hideProgressDialog()
                        it.printStackTrace()
                        Utility.simpleAlert(context, "", getString(R.string.something_went_wrong))
                    }

                } catch (ex: Exception) {
                    Log.e("", "Within Throwable Exception::" + it.message)
                }

            })

    }

    private fun generateMyCartPdf(content: String) {

        lifecycleScope.launch {

            showProgressDialog()

            val folder = commonDocumentDirPath(getString(R.string.app_name))

            if (!folder!!.exists()) {
                folder.mkdirs()
            }

            val fileName = "MyCart_" + System.currentTimeMillis()
            val filePath = folder.absolutePath + "/" + fileName + ".pdf"
            val file = File(filePath)


            Log.e("FilePath", file.path)
            try {

                CreatePdf(requireContext())
                    .setPdfName(fileName)
                    .openPrintDialog(false)
                    .setContentBaseUrl(null)
                    .setPageSize(PrintAttributes.MediaSize.ISO_A4)
                    .setContent(content)
                    .setFilePath(file.path)
                    .setCallbackListener(object : CreatePdf.PdfCallbackListener {
                        override fun onFailure(errorMsg: String) {
                            hideProgressDialog()
                            Toast.makeText(context, errorMsg, Toast.LENGTH_SHORT).show()
                        }

                        override fun onSuccess(filePath: String) {


                            Log.e("FilePath", filePath)

                            val fileNew = File(file.path + "/" + fileName + ".pdf")

                            val photoURI: Uri = FileProvider.getUriForFile(context!!,
                                context!!.packageName.toString()
                                        + ".provider", fileNew)

                            Log.e("Pdfuri", "" + photoURI)

                            val shareBy = ShareBy(context!!, object : ShareBy.Action {


                                override fun shareOnGmail() {
                                    sharePdfOnGmail(photoURI)
                                }

                                override fun shareOnWhatsApp() {
                                    shareByWhatsApp(photoURI)
                                }

                            }).showShareByPop()
                            hideProgressDialog()

                            /*val intent =  Intent(Intent.ACTION_VIEW)
                            intent.setDataAndType(photoURI, "application/pdf")
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            startActivity(intent)*/
                        }
                    })
                    .create()

            } catch (e: Exception) {
                hideProgressDialog()
                e.printStackTrace()
            }
        }


    }

    private fun sharePdfOnGmail(fileUri: Uri) {
        try {
            val emailIntent = Intent(Intent.ACTION_SEND)
            emailIntent.type = "text/plain"
            // emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.customer_support_email_id)))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "subject here")
            emailIntent.putExtra(Intent.EXTRA_TEXT, "body text")
            emailIntent.putExtra(Intent.EXTRA_STREAM, fileUri)
            emailIntent.setPackage("com.google.android.gm")
            startActivity(Intent.createChooser(emailIntent, "Pick an Email provider"))
        } catch (e: Exception) {
            Toast.makeText(context, getString(R.string.application_not_found), Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    }


    private fun shareByWhatsApp(uri: Uri) {
        try {
            val sendIntent = Intent("android.intent.action.SEND")
            sendIntent.component = ComponentName("com.whatsapp", "com.whatsapp.ContactPicker")
            sendIntent.type = "image"
            sendIntent.putExtra(Intent.EXTRA_STREAM, uri)
            // sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators(getString(R.string.customer_support_whatsapp_number)).toString() + "@s.whatsapp.net")
            sendIntent.putExtra(Intent.EXTRA_TEXT, "Sample text")
            startActivity(sendIntent)
        } catch (e: Exception) {
            Toast.makeText(context, getString(R.string.application_not_found), Toast.LENGTH_SHORT)
                .show()
            e.printStackTrace()
        }
    }

    fun commonDocumentDirPath(FolderName: String): File? {

        var dir: File? = null
        dir = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                .toString() + "/" + FolderName)
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/" + FolderName)
        }
        return dir

    }


}