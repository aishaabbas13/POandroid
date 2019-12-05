package com.cuseniordesign909.vpantry.user_interface

    .import

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.data_representations.SignUpCredentials
import com.cuseniordesign909.vpantry.data_representations.SignUpStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SignUp() : Fragment(), View.OnClickListener {
    private var signUp : View? = null
    private var signUpFirstName : EditText? = null
    private var signUpLastName : EditText? = null
    private var signUpEmail : EditText? = null
    private var signUpPassword : EditText? = null
    private var passwordCheck : EditText? = null
    private var signUpButton: Button? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        signUp = super.onCreateView(inflater, container, savedInstanceState)
        signUp = inflater.inflate(R.layout.signup, container, false)
        signUpFirstName = signUp?.findViewById(R.id.signUpFirstName)
        signUpLastName = signUp?.findViewById(R.id.signUpLastName)
        signUpEmail = signUp?.findViewById(R.id.signUpEmail)
        signUpPassword = signUp?.findViewById(R.id.signUpPassword)
        passwordCheck = signUp?.findViewById(R.id.passwordCheck)

        signUpButton = signUp?.findViewById(R.id.signUpButton)
        signUpButton?.setOnClickListener(this)
        return signUp
    }
    override fun onClick(v: View?) {
        var credentials = SignUpCredentials(signUpFirstName?.text.toString(),
            signUpLastName?.text.toString(),
            signUpEmail?.text.toString(),
            signUpPassword?.text.toString()
        )
        var password2 : String = signUpPassword?.text.toString()
        if(credentials.password == password2){
            CoroutineScope(IO).launch {
                signUp(credentials)
            }
        } else {
            TODO("Inform user that the two passwords don't match.")
        }
    }
    private fun connect(credentials : SignUpCredentials){
        var retrofit: Retrofit = Retrofit.Builder().baseUrl(getString(R.string.backend_url)).addConverterFactory(GsonConverterFactory.create()).build()
        var api = retrofit.create(NetworkConnection::class.java)
        var call = api.signUp(credentials)
        call.enqueue(object : Callback<SignUpStatus>{
            override fun onResponse(call: Call<SignUpStatus>, response: Response<SignUpStatus>) {
                println(call.toString())
                val success = response.raw()
                println("onResponse was called: $success")
            }
            override fun onFailure(call: Call<SignUpStatus>, t: Throwable) {
                println("onFailure was called: $t.message")
            }
        })
    }
    private suspend fun signUp(credentials : SignUpCredentials){
        val result = CoroutineScope(IO).async {
            connect(credentials)
        }
        result.await()
    }
}