package com.easym.vegie.sharePref

import android.content.Context
import android.content.SharedPreferences
import com.easym.vegie.SessionData.SharedPreferenceKeys
import com.easym.vegie.model.UserModel
import com.google.gson.Gson
import javax.inject.Singleton

@Singleton
class UserPref(context: Context) {
    private val preferences: SharedPreferences =
            context.getSharedPreferences("userPref", Context.MODE_PRIVATE)
    private val gson: Gson = Gson()

    var user: UserModel
        get() = gson.fromJson<Any>(
                preferences.getString("data", null),
                UserModel::class.java
        ) as UserModel
        set(user) {
            val gson = Gson()
            val loginRes = gson.toJson(user)
            preferences.edit().putString("data", loginRes).apply()
        }


    fun clearPref() {
        preferences.edit().clear().apply()
    }

    fun clearDialog(){
        val showDiloag = false
    }
    fun isUserPreferLanguageSet(): Boolean {
        return preferences.getBoolean("IsUserPreferLanguageSet",false)
    }

    fun setUserPreferLanguage(userLanguage : String,languageCode : String)  {

        val editor = preferences.edit()

        editor.putBoolean("IsUserPreferLanguageSet",true)
        editor.putString("UserPreferLanguage",userLanguage)
        editor.putString("UserPreferLanguageCode",languageCode)
        editor.apply()

    }

    fun getUserPreferLanguage() : String{
        return preferences.getString("UserPreferLanguage","English").toString()
    }

    fun getUserPreferLanguageCode() : String{
        return preferences.getString("UserPreferLanguageCode","en").toString()
    }

    fun getWhatsappNumber(): String? {
        return preferences.getString(SharedPreferenceKeys.getInstance().whatsapp, "")
    }

    fun setWhatsappNumber(number: String?) {
        val editor = preferences.edit()
        editor.putString(SharedPreferenceKeys.getInstance().whatsapp, number)
        editor.apply()
    }
}