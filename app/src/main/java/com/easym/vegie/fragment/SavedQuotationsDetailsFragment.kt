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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.easym.vegie.R
import com.easym.vegie.Utils.ShareBy
import com.easym.vegie.Utils.Utility
import com.easym.vegie.Utils.Utils
import com.easym.vegie.activities.FragmentContainerActivity
import com.easym.vegie.activities.ShopByCategoryActivity
import com.easym.vegie.activities.UserAddressListActivity
import com.easym.vegie.adapter.SavedQuotationsDetailsListAdapter
import com.easym.vegie.databinding.SavedQuotationsDetailsFragmentLayoutBinding
import com.easym.vegie.model.userquatationdetails.Product
import com.skbfinance.BaseFragment
import com.uttampanchasara.pdfgenerator.CreatePdf
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File
import java.net.ConnectException
import android.content.DialogInterface
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction


class SavedQuotationsDetailsFragment : BaseFragment(), View.OnClickListener {

    lateinit var binding: SavedQuotationsDetailsFragmentLayoutBinding
    lateinit var myCartAdapter: SavedQuotationsDetailsListAdapter
    lateinit var quotationId: String

    lateinit var totalAmount: String
    var totalAmountToPay: Double = 0.0
    var minimumAmountToPay: Double = 0.0

    var count = 0
    var LANGUAGECHANGEREQUESTCODE = 30
    val REQUESTSTORAGEPERMISSION = 255

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.saved_quotations_details_fragment_layout, container, false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        quotationId = requireArguments().getString("QuotationId")!!
        val quotationDate = requireArguments().getString("QuotationSavedDate")!!
        Log.e("QuotationId", quotationId + "")

        count = 0
        getQuatationListItem()
        getCartCount()

        binding.ivChangeLang.setOnClickListener(this)
        //binding.tvQuationTitle.setText("Quotation - "+quotationId)
        binding.tvQuationTitle.text = "Saved Date : " + Utils(requireContext()).formatDate(quotationDate)

    }

    private fun getQuatationListItem() {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.userQuatationList(
                quotationId,
                userPref.user.token!!,
                userPref.getUserPreferLanguageCode()
            )

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { this.showProgressDialog() }
                .doOnCompleted { this.hideProgressDialog() }
                .subscribe({
                    when {
                        it.getResponseCode()!! == "200" -> {
                            totalAmount = it!!.getData()!!.getTotal().toString()
                            totalAmountToPay = it.getData()!!.getTotal()!!
                            minimumAmountToPay = it.getData()!!.getLimitAmount()!!

                            binding.tvMinimumOrderAmount.text = "Minimum order amount is Rs. " + it.getData()!!.getLimitAmount()
                            binding.tvMinimumOrderAmount.visibility = View.VISIBLE

                            val list = it.getData()!!.getResult()
                            val plist = arrayListOf<Product>()
                            val listProduct = arrayListOf<Product>()

                            var serialNo = 1

                            //For Header Item
                            val product1 = Product()
                            product1.setView_type("HeaderTitle")
                            product1.setCategory_name("")
                            listProduct.add(product1)

                            for (i in 0..list!!.size - 1) {

                                //For Category Tilte
                                val product = Product()
                                val categoryName = list[i]!!.getName()!!
                                val productList = list[i]!!.getProduct()!!
                                val otherCategoryName = list[i]!!.getOtherCategoryName()!!

                                product.setView_type("CategoryTitle")
                                product.setCategory_name(categoryName)
                                product.setOther_category_name(otherCategoryName)
                                listProduct.add(product)

                                //For Product Item
                                for (x in 0..productList.size - 1) {

                                    val product = productList.get(x)
                                    plist.add(product!!)

                                    product.setView_type("")
                                    product.setCategory_name("")
                                    product.setSerialNo(serialNo.toString())
                                    serialNo++
                                    listProduct.add(product)
                                }

                                val product2 = Product()
                                product2.setView_type("CategorySubTotal")
                                product2.setCategory_name("")
                                product2.setCategorySubtotal(list[i]!!.getSubtotal()!!)
                                listProduct.add(product2)

                            }

                            //For Total
                            val product = Product()
                            product.setView_type("Total")
                            listProduct.add(product)

                            val linearLayoutManager = LinearLayoutManager(
                                context,
                                LinearLayoutManager.VERTICAL, false
                            )
                            myCartAdapter =
                                SavedQuotationsDetailsListAdapter(context, listProduct, totalAmount,
                                    object : SavedQuotationsDetailsListAdapter.Action {

                                        override fun removeItem(position: Int) {
                                            removeItemFromQuotation(
                                                listProduct[position].getQMenuId()!!,
                                                listProduct[position].getQBrandId()
                                            )

                                        }

                                        override fun updateQty(
                                            qty: String,
                                            position: Int,
                                            brandId: String,
                                        ) {

                                            updateProductQty(
                                                qty,
                                                listProduct[position].getQMenuId()!!,
                                                brandId
                                            )
                                        }

                                        override fun addMore() {
                                            val menuIdsList: List<String> =
                                                plist.map { it.getQMenuId()!! }
                                            val qty_List: List<String> =
                                                plist.map { it.getQTotalQty()!! }
                                            val brand_id_List: List<String> =
                                                plist.map { it.getQBrandId() }

                                            val menu_Id = TextUtils.join(",", menuIdsList)
                                            val qty = TextUtils.join(",", qty_List)
                                            val brand_Id = TextUtils.join(",", brand_id_List)

                                            val intent =
                                                Intent(context, ShopByCategoryActivity::class.java)
                                            intent.putExtra("source", "AddMore")
                                            intent.putExtra("menu_Id", menu_Id)
                                            intent.putExtra("qty", qty)
                                            intent.putExtra("brand_Id", brand_Id)
                                            intent.putExtra("quotationId", quotationId)
                                            startActivity(intent)

                                            // Log.e("menu_Id",""+menu_Id)
                                            // Log.e("qty",""+qty)
                                            //Code for adding more items in list
                                            //and saved it as new quotation

                                        }
                                    })

                            if (count == 0) {
                                binding.tvPreviousTotalAmount.text = totalAmount
                                binding.tvUpdatedPriceTotal.text = ""

                            } else {
                                binding.tvUpdatedPriceTotal.text = totalAmount
                            }

                            count++

                            binding.rvMyCart.adapter = myCartAdapter
                            binding.rvMyCart.layoutManager = linearLayoutManager

                            /* binding.buttonSaveAsQuatation.setOnClickListener(View.OnClickListener {

                                             val menuIdsList: List<String> = plist!!.map { it!!.getQMenuId()!! }
                                             val qty_List : List<String> = plist.map { it!!.getQTotalQty()!! }

                                             val menu_Id = TextUtils.join(",", menuIdsList)
                                             val qty = TextUtils.join(",",qty_List)

                                             Log.e("menu_Id",""+menu_Id)
                                             Log.e("qty",""+qty)

                                             saveAsQuotation(menu_Id,qty)


                                         })*/

                            binding.buttonShare.setOnClickListener {

                                if (checkStoragePermission()) {
                                    callGenerateQuotationPdfAPI()
                                } else {
                                    askStoragePermission()
                                }
                            }

                            binding.rvMyCart.visibility = View.VISIBLE
                            binding.llParent.visibility = View.VISIBLE

                            binding.buttonProceedToPay.setOnClickListener(this)


                        }
                        it.getResponseCode() == "403" -> {
                            utils.openLogoutDialog(requireContext(), userPref)
                        }
                        else -> {

                            binding.tvMinimumOrderAmount.visibility = View.GONE
                            binding.rvMyCart.visibility = View.GONE
                            binding.llParent.visibility = View.GONE


                            hideProgressDialog()
                            Utility.simpleAlert(
                                context,
                                getString(R.string.info_dialog_title),
                                it.getResponseMessage()
                            )
                        }
                    }

                }, {

                    try {

                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context,
                                getString(R.string.error),
                                getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(
                                context,
                                "",
                                getString(R.string.something_went_wrong)
                            )
                        }

                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }
                })
        } else {
            Utility.simpleAlert(
                context,
                getString(R.string.error),
                getString(R.string.check_network_connection)
            )
        }
    }

    private fun removeItemFromQuotation(menuId: String, brandId: String) {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.removeUserQuatationItem(
                quotationId,
                menuId,
                userPref.user.token!!,
                brandId
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { this.showProgressDialog() }
                .doOnCompleted { this.hideProgressDialog() }
                .subscribe({
                    when {
                        it.responseCode!! == "200" -> {
                            getQuatationListItem()
                            getCartCount()
                        }
                        it.responseCode == "403" -> {
                            utils.openLogoutDialog(requireContext(), userPref)
                        }
                        else -> {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context,
                                getString(R.string.info_dialog_title),
                                it.responseMessage
                            )
                        }
                    }

                }, {

                    try {

                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context,
                                getString(R.string.error),
                                getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(
                                context,
                                "",
                                getString(R.string.something_went_wrong)
                            )
                        }

                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }

                })
        } else {

            Utility.simpleAlert(
                context,
                getString(R.string.error),
                getString(R.string.check_network_connection)
            )
        }
    }

    private fun updateProductQty(qty: String, menuId: String, brandId: String) {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.updateQuatationItem(
                qty,
                quotationId,
                menuId,
                userPref.user.token!!, brandId
            )

                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { this.showProgressDialog() }
                .doOnCompleted { this.hideProgressDialog() }
                .subscribe({

                    when {
                        it.responseCode!! == "200" -> {
                            getQuatationListItem()
                            getCartCount()
                        }
                        it.responseCode == "403" -> {
                            utils.openLogoutDialog(requireContext(), userPref)
                        }
                        else -> {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context,
                                getString(R.string.info_dialog_title),
                                it.responseMessage
                            )
                        }
                    }

                }, {

                    try {

                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context,
                                getString(R.string.error),
                                getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(
                                context,
                                "",
                                getString(R.string.something_went_wrong)
                            )
                        }

                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }
                })
        } else {

            Utility.simpleAlert(
                context, getString(R.string.error),
                getString(R.string.check_network_connection)
            )
        }
    }


    private fun getCartCount() {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.userCartCount(
                userPref.user.id!!,
                userPref.user.token!!
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({

                    if (it.responseCode.equals("200")) {

                        val total = it.data.count_cart
                        if (total != 0) {
                            //tv_CartCount.setText(""+total)

                            val activity = requireActivity() as FragmentContainerActivity
                            activity.cartCount = it.data.count_cart
                            activity.totalAmount = if (it.data.total_amount != null) {
                                it.data.total_amount
                            } else {
                                0
                            }
                            activity.minimumOrderLimit = it.data.minimum_order_limit

                            requireActivity().findViewById<TextView>(R.id.tv_CartCount).text =
                                "" + total
                            Log.d("UserCartCountApi", "" + total)
                        } else {
                            requireActivity().findViewById<TextView>(R.id.tv_CartCount).text = ""

                        }

                    } else if (it.responseCode == "403") {
                        utils.openLogoutDialog(requireContext(), userPref)
                    } else {
                        //tv_CartCount.setText("")
                        requireActivity().findViewById<TextView>(R.id.tv_CartCount).text = ""
                        Log.d("UserCartCountApi", "" + it.responseMessage)
                    }

                }, {

                    try {

                        if (it is ConnectException) {
                            Log.d(
                                "UserCartCountApi",
                                "" + getString(R.string.check_network_connection)
                            )
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

    private fun saveAsQuotation(menuId: String, qty: String) {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.addUserQuotation(
                userPref.user.id!!,
                "quotation-" + System.currentTimeMillis(),
                menuId,
                qty,
                userPref.user.token!!,
                "",
                ""
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({

                    if (it.responseCode.equals("200")) {

                        Toast.makeText(context, it.responseMessage, Toast.LENGTH_SHORT).show()

                    } else if (it.responseCode == "403") {
                        utils.openLogoutDialog(requireContext(), userPref)
                    } else {
                        hideProgressDialog()
                        Utility.simpleAlert(
                            context,
                            getString(R.string.info_dialog_title),
                            it.responseMessage
                        )
                    }

                }, {

                    try {
                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context,
                                getString(R.string.error),
                                getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(
                                context,
                                "",
                                getString(R.string.something_went_wrong)
                            )
                        }

                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }

                })

        } else {
            hideProgressDialog()
            Utility.simpleAlert(
                context,
                getString(R.string.error),
                getString(R.string.check_network_connection)
            )
        }

    }

    private fun getCheckQauntiy(
        address_id: String,
        quotation_id: String,
        checkout_type: String,
        dialog: DialogInterface
    ) {
        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.checkQuality(
                userPref.user.id!!,
                userPref.user.token!!,
                address_id,
                quotation_id,
                checkout_type
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe { showProgressDialog() }
                .doOnCompleted { hideProgressDialog() }
                .subscribe({
                    if (it.responseCode.equals("200")) {
                        val intent = Intent(context, UserAddressListActivity::class.java)
                        intent.putExtra("couponDiscount", "0")
                        intent.putExtra("couponCode", "")
                        intent.putExtra("checkout_type", "quotation")
                        intent.putExtra("quoatationId", quotation_id)
                        startActivity(intent)
                        // Toast.makeText(context,it.responseMessage, Toast.LENGTH_SHORT).show()
                    } else if (it.responseCode == "403") {
                        utils.openLogoutDialog(requireContext(), userPref)
                    } else if (it.responseCode == "400") {
/*                            val intent = Intent(context, HomePageActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)*/

                        val builder = AlertDialog.Builder(requireContext())
                        builder.setMessage("${it.data}${it.responseMessage + " Please review your order"}")
                        builder.setCancelable(true)

//                            builder.setPositiveButton("Ok") { dialog, id ->
//                                dialog.cancel()
//                            }

                        builder.setNegativeButton("Ok") { dialog, id ->
                            dialog.dismiss()

                            val fragment: Fragment = SavedQuotationsFragment()
                            val fragmentManager: FragmentManager = requireActivity().supportFragmentManager
                            val fragmentTransaction: FragmentTransaction =
                                fragmentManager.beginTransaction()
                            fragmentTransaction.replace(R.id.container, fragment)
                            fragmentTransaction.addToBackStack(null)
                            fragmentTransaction.commit()


                            //       getCheckQauntiy(address_id, quotation_id, checkout_type,dialog)
                        }

                        val alert = builder.create()
                        alert.show()


                    } else {
                        hideProgressDialog()
                        Utility.simpleAlert(
                            context,
                            getString(R.string.info_dialog_title),
                            it.responseMessage
                        )
                    }


                }, {
                    try {

                        if (it is ConnectException) {
                            hideProgressDialog()
                            Utility.simpleAlert(
                                context,
                                getString(R.string.error),
                                getString(R.string.check_network_connection)
                            )
                        } else {
                            hideProgressDialog()
                            it.printStackTrace()
                            Utility.simpleAlert(
                                context,
                                "",
                                getString(R.string.something_went_wrong)
                            )
                        }

                    } catch (ex: Exception) {
                        Log.e("", "Within Throwable Exception::" + it.message)
                    }

                })

        } else {
            hideProgressDialog()
            Utility.simpleAlert(
                context,
                getString(R.string.error),
                getString(R.string.check_network_connection)
            )
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

            R.id.buttonProceedToPay -> {
                if (totalAmountToPay >= minimumAmountToPay) {
                    getCheckQauntiy("", quotationId, "quotation", dialog!!)
                    /*  val intent = Intent(context, UserAddressListActivity::class.java)
                      intent.putExtra("couponDiscount", "0")
                      intent.putExtra("couponCode", "")
                      intent.putExtra("checkout_type", "quotation")
                      intent.putExtra("quoatationId", quotationId)
                      startActivity(intent)*/
                } else {
                    Toast.makeText(
                        context,
                        "Please maintain the minimum order amount",
                        Toast.LENGTH_SHORT
                    ).show()

                }

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == LANGUAGECHANGEREQUESTCODE && resultCode == Activity.RESULT_OK) {

            Log.e("Lang Code", userPref.getUserPreferLanguageCode() + "")

            getQuatationListItem()

        }
    }

    override fun onResume() {
        super.onResume()
        getQuatationListItem()
    }

    private fun callGenerateQuotationPdfAPI() {

        apiService.generateShareQuotationPage(
            userPref.user.id.toString(),
            quotationId,
            userPref.getUserPreferLanguageCode(),
            userPref.user.token!!
        ).subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSubscribe { showProgressDialog() }
            .doOnCompleted { hideProgressDialog() }
            .subscribe({

                if (it.responseCode.equals("200")) {

                    // Toast.makeText(context,it.responseMessage, Toast.LENGTH_SHORT).show()
                    generateQuotationPdf(it.data.toString())
                    showProgressDialog()


                } else if (it.responseCode == "403") {
                    utils.openLogoutDialog(requireContext(), userPref)
                } else {
                    hideProgressDialog()
                    Utility.simpleAlert(
                        context,
                        getString(R.string.info_dialog_title),
                        it.responseMessage
                    )
                }

            }, {

                try {

                    if (it is ConnectException) {
                        hideProgressDialog()
                        Utility.simpleAlert(
                            context,
                            getString(R.string.error),
                            getString(R.string.check_network_connection)
                        )
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

    private fun generateQuotationPdf(content: String) {

        //val folder = File(Environment.getExternalStorageDirectory(), getString(R.string.app_name))
        val folder = commonDocumentDirPath(getString(R.string.app_name))

        if (!folder!!.exists()) {
            folder.mkdirs()
        }

        val fileName = "Quotation_" + quotationId
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

                        hideProgressDialog()

                        Log.e("FilePath", filePath)

                        val fileNew = File(file.path + "/" + fileName + ".pdf")

                        val photoURI: Uri = FileProvider.getUriForFile(
                            context!!,
                            context!!.packageName.toString()
                                    + ".provider", fileNew
                        )

                        Log.e("Pdfuri", "" + photoURI)

                        val shareBy = ShareBy(context!!, object : ShareBy.Action {

                            override fun shareOnGmail() {
                                sharePdfOnGmail(photoURI)
                            }

                            override fun shareOnWhatsApp() {
                                shareByWhatsApp(photoURI)
                            }

                        }).showShareByPop()

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

    private fun checkStoragePermission(): Boolean {

        return (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun askStoragePermission() {

        requestPermissions(
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), REQUESTSTORAGEPERMISSION
        )
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

                callGenerateQuotationPdfAPI()

            } else {
                Toast.makeText(requireContext(), "Permission required", Toast.LENGTH_SHORT).show()
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
            File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                    .toString() + "/" + FolderName
            )
        } else {
            File(Environment.getExternalStorageDirectory().toString() + "/" + FolderName)
        }
        return dir

    }
}