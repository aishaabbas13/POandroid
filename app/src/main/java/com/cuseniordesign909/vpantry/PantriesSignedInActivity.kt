package com.cuseniordesign909.vpantry

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cuseniordesign909.vpantry.pantries_signed_in_user_interface.PantriesList
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class PantriesSignedInActivity : AppCompatActivity(){
    companion object KeyFunctions {
        var retrofit: Retrofit? = null
        var preferences: SharedPreferences? = null
    }
    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signed_in)
        initializeRetrofit()
        preferences = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        supportFragmentManager.beginTransaction().add(R.id.signedInFragmentManager, PantriesList(), "").commit()
    }
    private fun initializeRetrofit(){
        var preferences = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        var token = preferences!!.getString(getString(R.string.token), null)
        var okHttpClient = OkHttpClient.Builder()
            okHttpClient.addInterceptor(object : Interceptor{
                override fun intercept(chain: Interceptor.Chain): Response {
                    val original: Request = chain.request()

                    val request: Request = original.newBuilder()
                        .header("token", token)
                        .header("Accept", "application/json")
                        .method(original.method(), original.body())
                        .build()

                    return chain.proceed(request)
                }
            })
            var client : OkHttpClient = okHttpClient.connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
        retrofit = Retrofit.Builder().baseUrl(getString(R.string.backend_url)).addConverterFactory(
            GsonConverterFactory.create()).client(client).build()
    }
    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.fragments[supportFragmentManager.backStackEntryCount - 1].onResume()
            supportFragmentManager.popBackStack()
        }
        else
            super.onBackPressed()
    }
}