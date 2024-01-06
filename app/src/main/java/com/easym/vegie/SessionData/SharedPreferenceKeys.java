package com.easym.vegie.SessionData;

/**
 * Created by Arti on 28/09/2020.
 */

public class SharedPreferenceKeys {
    private SharedPreferenceKeys() {

    }

    private static SharedPreferenceKeys sharedPreferenceKeys = null;

    public static SharedPreferenceKeys getInstance() {
        return sharedPreferenceKeys == null ? (sharedPreferenceKeys = new SharedPreferenceKeys()) : sharedPreferenceKeys;
    }

    /*
     * Keys for customer session
     * */
    public final String id = "id";
    public final String username = "username";
    public final String email = "email";
    public final String device_id = "device_id";
    public final String device_token = "device_token";
    public final String country_code = "country_code";
    public final String mobile_number = "mobile_number";
    public final String password = "password";
    public final String token = "token";
    public final String support = "support";
    public final String whatsapp = "whatsapp";
    final String isLoggedIn = "isLoggedIn";


}