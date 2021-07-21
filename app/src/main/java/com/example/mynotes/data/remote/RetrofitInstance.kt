package com.example.mynotes.data.remote

import android.content.Context
import com.example.mynotes.data.remote.Interceptor.AddCookiesInterceptor
import com.example.mynotes.data.remote.Interceptor.ReceivedCookiesInterceptor
import com.example.mynotes.utils.Contants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

class RetrofitInstance(val context:Context) {


//    companion object{
        val httpLoggingIntercepter = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)


        private val retrofit by lazy {
            Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(client)
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
        }

        val client by lazy {
            OkHttpClient.Builder()
                    .addInterceptor(AddCookiesInterceptor(context))
                    .addInterceptor(ReceivedCookiesInterceptor(context))
                    .addInterceptor(httpLoggingIntercepter)
                    .build()
            /*
            OkHttpClient.Builder()
                    .addInterceptor(httpLoggingIntercepter)
                    .cookieJar(JavaNetCookieJar(CookieHandler.getDefault()))
                    .connectTimeout(15, TimeUnit.SECONDS)
                    .readTimeout(20, TimeUnit.SECONDS)
                    .writeTimeout(20, TimeUnit.SECONDS)
                    .build()

             */
        }


        val api by lazy {
            retrofit.create(NoteApi::class.java)
        }
//    }
}