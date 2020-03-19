package com.cuseniordesign909.vpantry

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuseniordesign909.vpantry.signed_out_user_interface.SignIn
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class SignedOutActivity : AppCompatActivity(){
    companion object KeyFunctions {
        var retrofit: Retrofit? = null
        var preferences: SharedPreferences? = null
    }
    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signed_out)
        initializeRetrofit()
        preferences = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        supportFragmentManager.beginTransaction()
            .add(R.id.signedOutFragmentManager, SignIn(), "").commit()
    }
    private fun initializeRetrofit(){
        var okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
        retrofit = Retrofit.Builder().baseUrl(getString(R.string.backend_url)).addConverterFactory(
            GsonConverterFactory.create()).client(okHttpClient).build()
    }
    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount > 0)
            supportFragmentManager.popBackStack()
        else
            super.onBackPressed()
    }
}