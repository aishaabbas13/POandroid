package com.cuseniordesign909.vpantry.user_interface

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cuseniordesign909.vpantry.R

class SignIn : Fragment(), View.OnClickListener {
    private var signIn : View? = null
    private var signUpEmail : EditText? = null
    private var signUpPassword : EditText? = null
    private var signInButton: Button? = null
    private var createAccount: TextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        signIn = inflater.inflate(R.layout.signin, container, false)
        signUpEmail = signIn?.findViewById(R.id.signUpEmail)
        signUpPassword = signIn?.findViewById(R.id.signUpPassword)
        createAccount = signIn?.findViewById(R.id.createAccount)
        signInButton = signIn?.findViewById(R.id.signUpButton)
        signInButton?.setOnClickListener(this)
        createAccount?.setOnClickListener(this)
        return signIn
    }

    override fun onClick(v: View?) {
        println("Changing screens!")
        if(v?.id == R.id.signInButton) {
            var email: String = signUpEmail?.text.toString()
            var password: String = signUpPassword?.text.toString()
        }
        else if(v?.id == R.id.createAccount){
            activity!!.supportFragmentManager.beginTransaction().add(R.id.fragmentManager, SignUp(), "").addToBackStack("").commit()
        }
    }
}