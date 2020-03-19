package com.cuseniordesign909.vpantry

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class Launcher : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var preferences = this.getSharedPreferences(getString(R.string.preferences_file), Context.MODE_PRIVATE)
        var token = preferences!!.getString(getString(R.string.token), null)
        println("token from application class Launcher = $token")
        var intent : Intent?
        if(token != null)//signed in
            intent = Intent(applicationContext, PantriesSignedInActivity::class.java)
        else//not signed in
            intent = Intent(applicationContext, SignedOutActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
        startActivity(intent)
        finish()
    }
}