package com.cuseniordesign909.vpantry

import android.content.res.Resources
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.cuseniordesign909.vpantry.user_interface.SignIn
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(){
    private lateinit var linearLayoutManager: LinearLayoutManager
    companion object NetworkConnection {
        var retrofit: Retrofit? = null
    }
    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        var okHttpClient = OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(15, TimeUnit.SECONDS)
            .build()
       retrofit = Retrofit.Builder().baseUrl(getString(R.string.backend_url)).addConverterFactory(
            GsonConverterFactory.create()).client(okHttpClient).build()
        supportFragmentManager.beginTransaction().add(R.id.fragmentManager, SignIn(), "").commit()
    }
    override fun onBackPressed() {
        supportFragmentManager.popBackStack()
    }
}