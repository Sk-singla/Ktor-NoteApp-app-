package com.example.mynotes.data.remote.Interceptor

import android.content.Context
import android.content.SharedPreferences
import com.example.mynotes.utils.Contants.COOKIE_KEY
import com.example.mynotes.utils.Contants.COOKIE_SHARED_PREF
import okhttp3.Interceptor
import okhttp3.Response

class AddCookiesInterceptor(val context: Context):Interceptor {
    private val cookieSharedPreference:SharedPreferences
    init {
        cookieSharedPreference = context.getSharedPreferences(
                COOKIE_SHARED_PREF,
                Context.MODE_PRIVATE
        )
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val builder = chain.request().newBuilder()
        val preferences = (cookieSharedPreference.getStringSet(COOKIE_KEY,HashSet<String>())) as HashSet<String>
        val original = chain.request()
        if(original.url.toString().contains("notes")) {
            for(cookie in preferences){
                builder.addHeader("Cookie",cookie)
            }
        }
        return chain.proceed(builder.build())
    }
}