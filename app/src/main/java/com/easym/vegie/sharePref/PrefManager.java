package com.easym.vegie.sharePref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.easym.vegie.model.UserModel;
import com.google.gson.Gson;

import javax.inject.Singleton;

@Singleton
public class PrefManager {
    private final SharedPreferences sharedPreferences;
    private final SharedPreferences.Editor editor;
    public static final String TAG = "GroceryProject";


    public PrefManager(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }

    public void saveUserData(UserModel data) {
        final Gson gson = new Gson();
        String serializedObject = gson.toJson(data);
        editor.putString("data", serializedObject).apply();
    }

    public UserModel getUserData() {
        Gson gson = new Gson();
        String json = sharedPreferences.getString("data", "");
        UserModel obj = gson.fromJson(json, UserModel.class);
        return obj;
    }

    public void clearPref() {
        editor.clear().apply();
    }

}