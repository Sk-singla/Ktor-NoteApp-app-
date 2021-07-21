package com.example.mynotes.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.mynotes.R
import com.example.mynotes.utils.Contants.COOKIE_KEY
import com.example.mynotes.utils.Contants.COOKIE_SHARED_PREF
import com.example.mynotes.utils.Contants.USER_TOKEN

class SessionManager(context: Context) {

    private var prefs:SharedPreferences =
            context.getSharedPreferences(
                    context.getString(R.string.app_name),
                    Context.MODE_PRIVATE
            )

    val cookieSharedPref = context.getSharedPreferences(
            COOKIE_SHARED_PREF,
            Context.MODE_PRIVATE
    )

    fun saveAuthToken(token:String){
        val editor = prefs.edit()
        editor.putString(USER_TOKEN,token)
        editor.apply()
    }

    fun fetchAuthToken():String? {
        return prefs.getString(USER_TOKEN,null)
    }

    fun clearAuthToken(){
        val editor = prefs.edit()
        editor.putString(USER_TOKEN,"")
        editor.apply()
    }

    fun clearCookie(){
        val editor = cookieSharedPref.edit()
        editor.putStringSet(COOKIE_KEY,null)
        editor.apply()
    }
}