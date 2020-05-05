package com.cuseniordesign909.vpantry.signed_out_user_interface

    .import

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cuseniordesign909.vpantry.SignedOutActivity
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.data_representations.SignUpandSignInCredentials
import com.cuseniordesign909.vpantry.data_representations.ResponseStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUp() : Fragment(), View.OnClickListener {
    private var signUp : View? = null
    private var signUpFirstName : EditText? = null
    private var signUpLastName : EditText? = null
    private var signUpEmail : EditText? = null
    private var signUpPassword : EditText? = null
    private var passwordCheck : EditText? = null
    private var signUpNickName : EditText? = null
    private var signUpButton: Button? = null
    private var firstNameError: TextView? = null
    private var emailError: TextView? = null
    private var passwordError: TextView? = null
    private var checkError: TextView? = null
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
        signUpNickName = signUp?.findViewById(R.id.signUpNickName)
        passwordCheck = signUp?.findViewById(R.id.passwordCheck)
        firstNameError = signUp?.findViewById(R.id.firstNameError)
        firstNameError?.visibility = View.GONE
        emailError = signUp?.findViewById(R.id.emailError)
        emailError?.visibility = View.GONE
        passwordError = signUp?.findViewById(R.id.passwordError)
        passwordError?.visibility = View.GONE
        checkError = signUp?.findViewById(R.id.checkError)
        checkError?.visibility = View.GONE
        signUpButton = signUp?.findViewById(R.id.signUpButton)
        signUpButton?.setOnClickListener(this)
        return signUp
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity)?.supportActionBar?.hide()
    }
    override fun onClick(v: View?) {
        firstNameError?.visibility = View.GONE
        emailError?.visibility = View.GONE
        passwordError?.visibility = View.GONE
        checkError?.visibility = View.GONE
        var credentials = SignUpandSignInCredentials(
            signUpEmail?.text.toString(),
            signUpPassword?.text.toString(),
            signUpFirstName?.text.toString(),
            signUpLastName?.text.toString(),
            signUpNickName?.text.toString(),
            passwordCheck?.text.toString()
        )
        CoroutineScope(IO).async {
            connect(credentials)
        }
    }
    private fun connect(credentials : SignUpandSignInCredentials){
        var api = SignedOutActivity.retrofit?.create(NetworkConnection::class.java)
        var call = api?.signUp(credentials)
        call?.enqueue(object : Callback<ResponseStatus>{
            override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                var signUpStatus : ResponseStatus? = response.body()
                if(signUpStatus?.success == true){
                    Toast.makeText(context, signUpStatus?.message, Toast.LENGTH_SHORT).show()
                    fragmentManager?.popBackStack()
                } else
                    insertErrors(signUpStatus)
            }
            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                t.printStackTrace()
            }
        })
    }
    private fun insertErrors(signUpStatus : ResponseStatus?){
        if(signUpStatus?.email != null){
            emailError?.visibility = View.VISIBLE
            emailError?.text = signUpStatus?.email
        }
        if(signUpStatus?.password != null){
            passwordError?.visibility = View.VISIBLE
            passwordError?.text = signUpStatus?.password
        }
        if(signUpStatus?.given_name != null){
            firstNameError?.visibility = View.VISIBLE
            firstNameError?.text = signUpStatus?.given_name
        }
        if(signUpStatus?.password2 != null){
            checkError?.visibility = View.VISIBLE
            checkError?.text = signUpStatus?.password2
        }
    }
}