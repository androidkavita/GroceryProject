package com.skbfinance


import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.easym.vegie.R
import com.easym.vegie.Utils.Utils
import com.easym.vegie.api.ApiService1
import com.easym.vegie.sharePref.UserPref
import dagger.android.support.AndroidSupportInjection
import javax.inject.Inject


open class BaseFragment : Fragment() {

    @Inject lateinit var utils: Utils
    @Inject lateinit var userPref: UserPref
    @Inject lateinit var apiService: ApiService1

    var dialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidSupportInjection.inject(this)
        super.onCreate(savedInstanceState)
    }

    protected fun replaceFragment(fragment: Fragment) {
        val fragmentTransaction = requireActivity().supportFragmentManager.beginTransaction()
        fragmentTransaction.setCustomAnimations( R.anim.pop_enter, R.anim.pop_exit,R.anim.enter, R.anim.exit)
        fragmentTransaction.replace(R.id.content_frame, fragment, fragment.javaClass.name).commit()
    }

    protected fun showProgressDialog() {
        if (dialog == null)
            dialog = Dialog(requireContext())
        dialog!!.setContentView(R.layout.progress_dialog)
        dialog!!.setCancelable(false)
        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        if (dialog != null && !dialog!!.isShowing)
            dialog!!.show()

    }

    protected fun hideProgressDialog() {
        if (dialog != null && dialog!!.isShowing)
            dialog!!.dismiss()
        /* if (progressDialog != null && progressDialog!!.isShowing)
             progressDialog!!.dismiss()*/
    }

    override fun onDestroy() {
        super.onDestroy()
        if (dialog != null && dialog!!.isShowing)
            dialog!!.dismiss()
    }

}
