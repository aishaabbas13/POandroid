package com.cuseniordesign909.vpantry.signed_out_user_interface

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cuseniordesign909.vpantry.SignedOutActivity
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.PantriesSignedInActivity
import com.cuseniordesign909.vpantry.data_representations.SignUpandSignInCredentials
import com.cuseniordesign909.vpantry.data_representations.ResponseStatus
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import com.cuseniordesign909.vpantry.signed_out_user_interface.import.SignUp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignIn : Fragment(), View.OnClickListener {
    private var signIn : View? = null
    private var signInEmail : EditText? = null
    private var signInPassword : EditText? = null
    private var signInButton: Button? = null
    private var createAccount: TextView? = null
    private var signInError: TextView? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        signIn = inflater.inflate(R.layout.signin, container, false)
        signInEmail = signIn?.findViewById(R.id.signInEmail)
        signInPassword = signIn?.findViewById(R.id.signInPassword)
        createAccount = signIn?.findViewById(R.id.createAccount)
        signInButton = signIn?.findViewById(R.id.signInButton)
        signInError = signIn?.findViewById(R.id.signInError)
        signInError?.visibility = View.GONE
        signInButton?.setOnClickListener(this)
        createAccount?.setOnClickListener(this)
        return signIn
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity)?.supportActionBar?.hide()
    }
    private fun connect(credentials : SignUpandSignInCredentials){
        var api = SignedOutActivity.retrofit?.create(NetworkConnection::class.java)
        var call = api?.signIn(credentials)
        call?.enqueue(object : Callback<ResponseStatus> {
            override fun onResponse(
                call: Call<ResponseStatus>,
                response: Response<ResponseStatus>
            ) {
                var signInStatus: ResponseStatus? = response.body()
                if (signInStatus?.success == true) {
                    val editor = SignedOutActivity.preferences!!.edit()
                    editor.putString(getString(R.string.token), signInStatus?.token)
                    editor.apply()
                    var intent =
                        Intent(activity?.applicationContext, PantriesSignedInActivity::class.java)
                    activity?.finish()
                    startActivity(intent)
                } else {
                    signInError?.text = signInStatus?.message
                    signInError?.visibility = View.VISIBLE
                }
            }
            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                t.printStackTrace()
                println("onFailure was called")
            }
        })
    }
    override fun onClick(v: View?) {
        signInError?.visibility = View.GONE
        when(v?.id) {
            R.id.signInButton -> {
                signInError?.visibility = View.GONE
                var credentials = SignUpandSignInCredentials(
                    signInEmail?.text.toString(),
                    signInPassword?.text.toString()
                )
                println("email = " + credentials.email)
                println("password = " + credentials.password)
                CoroutineScope(Dispatchers.IO).async {
                    connect(credentials)
                }
            }
            R.id.createAccount -> {
                signInEmail?.setText("")
                signInPassword?.setText("")
                activity!!.supportFragmentManager.beginTransaction()
                    .add(R.id.signedOutFragmentManager, SignUp(), "").addToBackStack("").commit()
            }
        }
    }
}