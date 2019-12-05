package com.cuseniordesign909.vpantry

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cuseniordesign909.vpantry.user_interface.SignIn

class MainActivity : AppCompatActivity(){
    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)
        supportFragmentManager.beginTransaction().add(R.id.fragmentManager, SignIn(), "").commit()
    }

    override fun onBackPressed() {
        supportFragmentManager.popBackStack()
    }
}