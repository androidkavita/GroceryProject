package com.easym.vegie.Utils

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.text.Html
import android.text.Spanned
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.easym.vegie.R
import com.easym.vegie.SessionData.SessionManager
import com.easym.vegie.activities.LoginPageActivity
import com.easym.vegie.sharePref.UserPref
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Singleton

@Singleton
class Utils(private val context: Context) {

    fun toaster(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show()
    }

    fun logger(message: String) {
        Log.e("Win-Millionaire-Log", message)
    }

    fun simpleAlert(context: Context, title: String, message: String) {
        val builder = AlertDialog.Builder(context, R.style.AlertDialogTheme)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("Close", null)
        builder.create()
        builder.show()
    }

    fun hideKeyboard(view: View) {
        val imm = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun savedDate(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(date)
    }

    fun showDate(date: Date): String {
        val sdf = SimpleDateFormat("dd-MMM-yyyy")
        return sdf.format(date)
    }

    fun savedDate1(date: Date): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        return sdf.format(date)
    }

    fun showDate1(date: Date): String {
        val sdf = SimpleDateFormat("dd-MMM-yyyy")
        return sdf.format(date)
    }

    fun fromHtml(html: String): Spanned {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(html)
        }
    }

    fun formatDate(inputDate: String) : String {
        val inputFormat: DateFormat = SimpleDateFormat("yyyy-MM-dd")
        val outputFormat: DateFormat = SimpleDateFormat("dd/MM/yyyy")
        val date: Date = inputFormat.parse(inputDate)
        val outputDateStr: String = outputFormat.format(date)
        return outputDateStr
    }

    /**
     * checking internet connection
     */
    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }


    fun getFormattedDate(incomingDate: String, incomingFormat: String): String {
        val fmt = SimpleDateFormat(incomingFormat)
        var date: Date? = null
        var formatedDate = ""
        try {
            date = fmt.parse(incomingDate)
            val fmtOut = SimpleDateFormat("dd MMM")
            formatedDate = fmtOut.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return formatedDate
    }

    fun getDate(incomingDate: String, incomingFormat: String): String {
        val fmt = SimpleDateFormat(incomingFormat)
        var date: Date? = null
        var formatedDate = ""
        try {
            date = fmt.parse(incomingDate)
            val fmtOut = SimpleDateFormat("dd-MM-yyyy")
            formatedDate = fmtOut.format(date)
        } catch (e: ParseException) {
            e.printStackTrace()
        }

        return formatedDate
    }

    fun getDateAndTime(date: Date) : String{
        val formattedDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date)
        return formattedDate
    }

    fun getDate(date: Date) : String{
        val formattedDate = SimpleDateFormat("yyyy-MM-dd").format(date)
        return formattedDate
    }

     fun openLogoutDialog(context: Context,userPref : UserPref) {

         signOut(context,userPref)

    }

    private fun signOut(context: Context,userPref: UserPref) {
        SessionManager(context).clearUserSession()
        userPref.clearPref()
        val intent = Intent(context, LoginPageActivity::class.java)
        context.startActivity(intent)
        (context as Activity).finish()
    }


    @SuppressLint("HardwareIds")
    fun getDeviceId(): String {
        return Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    }

    companion object {

        var userType: String? = null
        var serviceID: String? = null
    }

}