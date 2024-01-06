package com.easym.vegie.SessionData;

import android.content.Context;

/**
 * Created by Arti on 28th Sept 2020 at 14:57.
 */
public class SessionManager {

    // Shared setPreferenceName to store and retrieve values
    private SharedPreferencesUtility appSp = null;
    static SessionManager session = null;

    public static SessionManager getInstance(Context mContext) {
        return session == null ? (session = new SessionManager(mContext)) : session;
    }


    // Context
    private Context mContext = null;

    /**
     * @param mContext Constructor with Activity Context
     */
    public SessionManager(Context mContext) {
        this.mContext = mContext;
        appSp = new SharedPreferencesUtility(mContext);
    }

    /*
     * create user login session
     * */

    public void userLoginSession(String id, String username, String email, String device_id, String device_token, String country_code, String mobile_number, String password, String token,String support, boolean loginFlag) {
        appSp.setString(SharedPreferenceKeys.getInstance().id, id);
        appSp.setString(SharedPreferenceKeys.getInstance().username, username);
        appSp.setString(SharedPreferenceKeys.getInstance().email, email);
        appSp.setString(SharedPreferenceKeys.getInstance().device_id, device_id);
        appSp.setString(SharedPreferenceKeys.getInstance().device_token, device_token);
        appSp.setString(SharedPreferenceKeys.getInstance().country_code, country_code);
        appSp.setString(SharedPreferenceKeys.getInstance().mobile_number, mobile_number);
        appSp.setString(SharedPreferenceKeys.getInstance().password, password);
        appSp.setString(SharedPreferenceKeys.getInstance().token, token);
        appSp.setString(SharedPreferenceKeys.getInstance().token, token);
        appSp.setString(SharedPreferenceKeys.getInstance().support, support);
        appSp.setBoolean(SharedPreferenceKeys.getInstance().isLoggedIn, loginFlag);

    }

    public boolean isLoggedIn() {
        return appSp.getBoolean(SharedPreferenceKeys.getInstance().isLoggedIn, false);
    }





    /*
     * Clear customer session
     * */
    public void clearUserSession() {
        appSp.removeValue(SharedPreferenceKeys.getInstance().id);
        appSp.removeValue(SharedPreferenceKeys.getInstance().username);
        appSp.removeValue(SharedPreferenceKeys.getInstance().email);
        appSp.removeValue(SharedPreferenceKeys.getInstance().device_id);
        appSp.removeValue(SharedPreferenceKeys.getInstance().device_token);
        appSp.removeValue(SharedPreferenceKeys.getInstance().country_code);
        appSp.removeValue(SharedPreferenceKeys.getInstance().mobile_number);
        appSp.removeValue(SharedPreferenceKeys.getInstance().password);
        appSp.removeValue(SharedPreferenceKeys.getInstance().token);
        appSp.removeValue(SharedPreferenceKeys.getInstance().isLoggedIn);

    }

}
