package com.easym.vegie.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.easym.vegie.R
import com.easym.vegie.Utils.Utility
import com.easym.vegie.activities.FragmentContainerActivity
import com.easym.vegie.activities.ShopByCategoryActivity
import com.easym.vegie.activities.ShopByQuotationActivity
import com.easym.vegie.adapter.SavedQuotationsAdapter
import com.easym.vegie.databinding.SavedQuotationsFragmentLayoutBinding
import com.skbfinance.BaseFragment
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.ConnectException

class SavedQuotationsFragment  : BaseFragment(), View.OnClickListener {

    lateinit var binding : SavedQuotationsFragmentLayoutBinding
    lateinit var adapter : SavedQuotationsAdapter


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        binding = DataBindingUtil.inflate(inflater, R.layout.saved_quotations_fragment_layout,
                container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvShopByCategory.setOnClickListener(this)
        binding.tvShopByQuotation.setOnClickListener(this)

    }

    override fun onResume() {
        super.onResume()
        getSavedQuotations()

    }

    private fun getSavedQuotations(){

        if (Utility.getInstance().checkInternetConnection(context)) {
            apiService.getUserQuotation(
                    userPref.user.id!!,
                    userPref.user.token!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe({ this.showProgressDialog() })
                    .doOnCompleted({ this.hideProgressDialog() })
                    .subscribe({

                        if (it.responseCode.equals("200")) {
                            val quotationList = it.data.quotationList.result
                            val layoutManager = LinearLayoutManager(context,
                                    LinearLayoutManager.VERTICAL, false)

                            adapter = SavedQuotationsAdapter(requireContext(), quotationList,
                                    object : SavedQuotationsAdapter.Action {
                                        override fun deleteQuatation(position: Int) {
                                            deleteSavedQuotations(quotationList[position].id)
                                        }

                                        override fun moveForward(position: Int) {
                                            val intent = Intent(context, FragmentContainerActivity::class.java)
                                            intent.putExtra("FragmentName",
                                                    "Saved Quotations Details Fragment")
                                            intent.putExtra("QuotationId", quotationList[position].id)
                                            intent.putExtra("QuotationSavedDate",
                                                    quotationList[position].qu_date)
                                            startActivity(intent)

                                        }
                                    })

                            binding.rvSavedQuotations.layoutManager = layoutManager
                            binding.rvSavedQuotations.adapter = adapter


                        } else if (it.responseCode == "403") {
                            utils.openLogoutDialog(requireContext(), userPref)
                        } else {

                            hideProgressDialog()
                            Utility.simpleAlert(context, getString(R.string.info_dialog_title), it.getResponseMessage())

                            binding.rvSavedQuotations.visibility = View.GONE

                        }
                    }, {

                        try {

                            if (it is ConnectException) {
                                hideProgressDialog()
                                Utility.simpleAlert(context, getString(R.string.error), getString(R.string.check_network_connection))
                            } else {
                                hideProgressDialog()
                                it.printStackTrace()
                                Utility.simpleAlert(context, "", getString(R.string.something_went_wrong))
                            }

                        } catch (ex: Exception) {
                            Log.e("", "Within Throwable Exception::" + it.message)
                        }

                    })
        } else {
            Utility.simpleAlert(context, getString(R.string.error), getString(R.string.check_network_connection))
        }

    }

    private fun deleteSavedQuotations(quatation_id: String){

        if (Utility.getInstance().checkInternetConnection(context)) {

            apiService.removeUserQuotation(
                    quatation_id,
                    userPref.user.token!!)

                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe({ this.showProgressDialog() })
                    .doOnCompleted({ this.hideProgressDialog() })
                    .subscribe({

                        if (it.responseCode.equals("200")) {

                            getSavedQuotations()


                        } else if (it.responseCode == "403") {
                            utils.openLogoutDialog(requireContext(), userPref)
                        } else {
                            hideProgressDialog()
                            Utility.simpleAlert(context, getString(R.string.info_dialog_title), it.getResponseMessage())
                        }
                    }, {

                        try {

                            if (it is ConnectException) {
                                hideProgressDialog()
                                Utility.simpleAlert(context, getString(R.string.error), getString(R.string.check_network_connection))
                            } else {
                                hideProgressDialog()
                                it.printStackTrace()
                                Utility.simpleAlert(context, "", getString(R.string.something_went_wrong))
                            }

                        } catch (ex: Exception) {
                            Log.e("", "Within Throwable Exception::" + it.message)
                        }

                    })
        } else {

            Utility.simpleAlert(context, getString(R.string.error), getString(R.string.check_network_connection))
        }

    }

    override fun onClick(v: View?) {

        when(v!!.id){

            R.id.tv_shopByCategory -> {
                startActivity(Intent(context, ShopByCategoryActivity::class.java))
            }

            R.id.tv_shopByQuotation -> {
                startActivity(Intent(context, ShopByQuotationActivity::class.java))
            }
        }
    }
}