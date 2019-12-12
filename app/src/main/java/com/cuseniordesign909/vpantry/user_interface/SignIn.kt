package com.cuseniordesign909.vpantry.user_interface

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.cuseniordesign909.vpantry.MainActivity
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.data_representations.SignInCredentials
import com.cuseniordesign909.vpantry.data_representations.SignInStatus
import com.cuseniordesign909.vpantry.data_representations.SignUpStatus
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import com.cuseniordesign909.vpantry.user_interface.import.SignUp
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignIn : Fragment(), View.OnClickListener {
    private var signIn : View? = null
    private var signUpEmail : EditText? = null
    private var signUpPassword : EditText? = null
    private var signInButton: Button? = null
    private var createAccount: TextView? = null
    private var signInError: TextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        signIn = inflater.inflate(R.layout.signin, container, false)
        signUpEmail = signIn?.findViewById(R.id.signInEmail)
        signUpPassword = signIn?.findViewById(R.id.signInPassword)
        createAccount = signIn?.findViewById(R.id.createAccount)
        signInButton = signIn?.findViewById(R.id.signInButton)
        signInError = signIn?.findViewById(R.id.signInError)
        signInError?.visibility = View.GONE
        signInButton?.setOnClickListener(this)
        createAccount?.setOnClickListener(this)
        return signIn
    }

    override fun onClick(v: View?) {
        if(v?.id == R.id.signInButton) {
            println("Sign in button pressed!!!")
            signInError?.visibility = View.GONE
            var credentials = SignInCredentials(signUpEmail?.text.toString(), signUpPassword?.text.toString())
            var api = MainActivity.retrofit?.create(NetworkConnection::class.java)
            var call = api?.signIn(credentials)
            call?.enqueue(object : Callback<SignInStatus> {
                override fun onResponse(
                    call: Call<SignInStatus>,
                    response: Response<SignInStatus>
                ) {
                    print("response.code() =  ")
                    println(response.code())
                    var signInStatus : SignInStatus? = response.body()
                    var success = signInStatus?.msg
                    println("success = $success")
                    if(signInStatus?.success == true){
                        Toast.makeText(activity, signInStatus?.msg, Toast.LENGTH_SHORT).show()
                    } else if (signInStatus?.success == false){
                        signInError?.text = signInStatus?.msg
                        signInError?.visibility = View.VISIBLE
                    }
                }

                override fun onFailure(call: Call<SignInStatus>, t: Throwable) {
                    t.printStackTrace()
                    println("onFailure was called")
                }
            })
        }
        else if(v?.id == R.id.createAccount){
            activity!!.supportFragmentManager.beginTransaction().add(R.id.fragmentManager, SignUp(), "").addToBackStack("").commit()
        }
    }
}