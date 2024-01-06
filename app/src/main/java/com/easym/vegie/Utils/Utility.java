package com.easym.vegie.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Patterns;

import com.easym.vegie.R;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utility {

    private Utility() {
    }

    private static Utility utility = null;

    public static Utility getInstance() {
        return utility == null ? (utility = new Utility()) : utility;
    }

    private boolean isNetworkAvailable(Context mContext) {
        int[] networkTypes = {ConnectivityManager.TYPE_MOBILE,
                ConnectivityManager.TYPE_WIFI,
                ConnectivityManager.TYPE_BLUETOOTH};
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
            for (int networkType : networkTypes) {
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                if (activeNetworkInfo != null &&
                        activeNetworkInfo.getType() == networkType)
                    return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    public boolean checkInternetConnection(Context mContext) {
        if (isNetworkAvailable(mContext)) {
            return true;
        } else {
            return false;
        }
    }

    public static void simpleAlert(Context context, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Close", null);
        builder.create();
        builder.show();
    }

    public boolean isEmpty(String str) {
        if (TextUtils.isEmpty(str))
            return true;
        else
            return false;
    }

    public static boolean isEmail(String text) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern p = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    public boolean isValidMail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public boolean isValidMobile(String phone ){
        if(phone.length() == 10) {
            return true;
        } else {
            return false;
        }
    }
}
