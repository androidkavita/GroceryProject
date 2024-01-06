package com.easym.vegie.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import com.easym.vegie.R
import com.easym.vegie.Utils.Utility
import com.easym.vegie.activities.FragmentContainerActivity
import com.easym.vegie.model.language.LanguageListResult
import com.skbfinance.BaseFragment
import kotlinx.android.synthetic.main.change_language_fragment_layout.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.net.ConnectException


class ChangeLanguageFragment : BaseFragment(), View.OnClickListener, RadioGroup.OnCheckedChangeListener {

    lateinit var languageList : List<LanguageListResult>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = LayoutInflater.from(context).inflate(R.layout.change_language_fragment_layout,
                container, false)
        return view

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getLanguageList()

    }

    fun addRadioButtons(number: Int, languageList: List<LanguageListResult>) {
        radiogroup.setOrientation(LinearLayout.VERTICAL)

        val userPreferLang = userPref.getUserPreferLanguage()

        //
        for (i in 0..number) {
            val rdbtn = RadioButton(context)
            rdbtn.id = i
            rdbtn.text = languageList.get(i).name
            rdbtn.setPadding(0, 30, 0, 30)

            if(userPreferLang.equals(languageList.get(i).name)){
                rdbtn.isChecked = true
            }

            radiogroup.setOnCheckedChangeListener(this)
            radiogroup.addView(rdbtn)
        }
    }


    private fun getLanguageList(){

        if (Utility.getInstance().checkInternetConnection(context)) {
            //Log.e("UserId", userPref.user.id + "")
            apiService.getLanguage(
                    userPref.user.token!!)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnSubscribe({ this.showProgressDialog() })
                    .doOnCompleted({ this.hideProgressDialog() })
                    .subscribe({

                        if (it.responseCode.equals("200")) {

                            languageList = it.data.languageList.result
                            addRadioButtons(languageList.size - 1, languageList)

                        } else if (it.getResponseCode() == "403") {
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

    }

    override fun onCheckedChanged(group: RadioGroup?, checkedId: Int) {

        when(checkedId){

            0 -> {
                // Log.e("RadioButton","English")
                userPref.setUserPreferLanguage(languageList.get(0).name,
                        languageList.get(0).lan_code)
                (activity as FragmentContainerActivity).userPreferLanguageCode = languageList.get(0).lan_code
            }

            1 -> {
                //Log.e("RadioButton","Hindi")
                userPref.setUserPreferLanguage(languageList.get(1).name,
                        languageList.get(1).lan_code)

            }
            2 -> {
                //Log.e("RadioButton","Kannada")
                userPref.setUserPreferLanguage(languageList.get(2).name,
                        languageList.get(2).lan_code)
            }
            3 -> {
                //Log.e("RadioButton","Telugu")
                userPref.setUserPreferLanguage(languageList.get(3).name,
                        languageList.get(3).lan_code)

            }
            4 -> {
                //Log.e("RadioButton", "Tamil")
                userPref.setUserPreferLanguage(languageList.get(4).name,
                        languageList.get(4).lan_code)
            }
            5 -> {
                //Log.e("RadioButon","Malayalam")
                userPref.setUserPreferLanguage(languageList.get(5).name,
                        languageList.get(5).lan_code)
            }
        }
    }
}