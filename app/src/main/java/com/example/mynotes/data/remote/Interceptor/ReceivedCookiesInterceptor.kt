package com.example.mynotes.data.remote.Interceptor

import android.content.Context
import android.content.SharedPreferences
import com.example.mynotes.utils.Contants.COOKIE_KEY
import com.example.mynotes.utils.Contants.COOKIE_SHARED_PREF
import okhttp3.Interceptor
import okhttp3.Response

class ReceivedCookiesInterceptor(val context: Context):Interceptor {

    private val cookieSharedPref:SharedPreferences
    init {
        cookieSharedPref = context.getSharedPreferences(
                COOKIE_SHARED_PREF,
                Context.MODE_PRIVATE
        )
    }


    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        if(!originalResponse.headers("Set-Cookie").isEmpty()) {
            val cookies = (cookieSharedPref.getStringSet(
                    COOKIE_KEY,HashSet<String>()
            ) ) as HashSet<String>

            for(header in originalResponse.headers("Set-Cookie")) {
                cookies?.add(header)
            }
            val e = cookieSharedPref.edit()
            e.putStringSet(COOKIE_KEY,cookies)
            e.apply()
        }
        return originalResponse
    }
}