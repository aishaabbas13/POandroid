package com.cuseniordesign909.vpantry.pantries_signed_in_user_interface

import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.PantriesSignedInActivity
import com.cuseniordesign909.vpantry.SignedOutActivity
import com.cuseniordesign909.vpantry.data_representations.SignUpandSignInCredentials
import com.cuseniordesign909.vpantry.data_representations.ResponseStatus
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateUserInfo : Fragment(), View.OnClickListener {

    private var userInfo : View? = null
    private var updateEmailError : TextView? = null
    private var updateFirstNameError : TextView? = null
    private var editEmail : EditText? = null
    private var editFirstName : EditText? = null
    private var editLastName : EditText? = null
    private var editNickName : EditText? = null
    private var updateUserInfoButton : Button? = null
    private var updateUserChangePassword : Button? = null
    private var updateUserDeleteAccount : Button? = null
    private var info : SignUpandSignInCredentials? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        userInfo = inflater.inflate(R.layout.updateuserinfo, container, false)
        updateUserChangePassword = userInfo?.findViewById(R.id.updateUserChangePasswordButton)
        updateUserChangePassword?.setOnClickListener(this)
        updateUserInfoButton = userInfo?.findViewById(R.id.updateUserInfoButton)
        updateUserInfoButton?.setOnClickListener(this)
        updateUserDeleteAccount = userInfo?.findViewById(R.id.updateUserDeleteAccount)
        updateUserDeleteAccount?.setOnClickListener(this)
        initializeEditTexts()
        initializeErrors()
        val b = arguments
        info = SignUpandSignInCredentials(
            b?.getString("email") as String,
            "",
            b?.getString("given_name") as String,
            b?.getString("family_name") as String,
            b?.getString("nickname") as String)
        setText(info)
        return userInfo
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu?.findItem(R.id.app_bar_create_pantry).isVisible = false
        menu?.findItem(R.id.app_bar_sign_out).isVisible = false
        menu?.findItem(R.id.app_bar_user_info).isVisible = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onClick(v: View?) {
        when (v?.id){
            R.id.updateUserInfoButton -> {
                updateEmailError?.visibility = View.GONE
                updateFirstNameError?.visibility = View.GONE
                when (v?.id) {
                    R.id.updateUserInfoButton -> {
                        var updatedUser = SignUpandSignInCredentials(
                            editEmail?.text.toString(),
                            "",
                            editFirstName?.text.toString(),
                            editLastName?.text.toString(),
                            editNickName?.text.toString()
                        )
                        connect(updatedUser)
                    }
                }
            }
            R.id.updateUserChangePasswordButton -> {
                var changePasswordDialog = ChangePassword()
                changePasswordDialog.show(fragmentManager as FragmentManager, "Change Password")
            }
            R.id.updateUserDeleteAccount -> {
                var alertDialog = AlertDialog.Builder(context as Context)
                alertDialog.setTitle("Delete account?")
                alertDialog.setMessage("Are you sure you want to delete your account? This cannot be undone.")
                alertDialog.setCancelable(false)
                alertDialog.setPositiveButton("Delete", object : DialogInterface.OnClickListener{
                    override fun onClick(v: DialogInterface?, p1: Int) {
                        CoroutineScope(Dispatchers.IO).async{
                            connect(null)
                        }
                    }
                })
                alertDialog.setNegativeButton("Cancel", null)
                alertDialog.create().show()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity)?.supportActionBar?.title = "Update User Info"
    }
    private fun initializeErrors(){
        updateEmailError = userInfo?.findViewById(R.id.updateEmailError)
        updateEmailError?.visibility = View.GONE
        updateFirstNameError = userInfo?.findViewById(R.id.updateFirstNameError)
        updateFirstNameError?.visibility = View.GONE
    }
    private fun initializeEditTexts(){
        editEmail = userInfo?.findViewById(R.id.editEmail)
        editFirstName = userInfo?.findViewById(R.id.editFirstName)
        editLastName = userInfo?.findViewById(R.id.editLastName)
        editNickName = userInfo?.findViewById(R.id.editNickName)
    }
    private fun setText(info : SignUpandSignInCredentials?){
        editEmail?.setText(info?.email.toString())
        editFirstName?.setText(info?.given_name.toString())
        editLastName?.setText(info?.family_name.toString())
        editNickName?.setText(info?.nickname.toString())
    }

    override fun onResume(){
        super.onResume()
        (activity as AppCompatActivity)?.supportActionBar?.title = "Update User Info"
    }
    private fun connect(updated : SignUpandSignInCredentials?){
        if(updated != null) {
            var api = PantriesSignedInActivity.retrofit?.create(NetworkConnection::class.java)
            var call = api?.updateUserInfo(updated)
            call?.enqueue(object : Callback<ResponseStatus> {
                override fun onResponse(
                    call: Call<ResponseStatus>,
                    response: Response<ResponseStatus>
                ) {
                    var status: ResponseStatus? = response?.body()
                    if (status?.success == true) {
                        Toast.makeText(context, status?.message, Toast.LENGTH_SHORT).show()
                    } else
                        insertErrors(status)
                }
                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        } else {
            var api = PantriesSignedInActivity.retrofit?.create(NetworkConnection::class.java)
            var call = api?.deleteUser()
            call?.enqueue(object : Callback<ResponseStatus> {
                override fun onResponse(
                    call: Call<ResponseStatus>,
                    response: Response<ResponseStatus>
                ) {
                    var status: ResponseStatus? = response?.body()
                    if (status?.success == true) {
                        Toast.makeText(context, status?.message, Toast.LENGTH_SHORT).show()
                        val editor = PantriesSignedInActivity.preferences!!.edit()
                        editor.putString(getString(R.string.token), null)
                        editor.apply()
                        var intent = Intent(activity?.applicationContext, SignedOutActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                        startActivity(intent)
                        activity?.finish()
                    } else
                        insertErrors(status)
                }
                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    t.printStackTrace()
                }
            })
        }
    }
    private fun insertErrors(status : ResponseStatus?){
        if(status?.email != null){
            updateEmailError?.visibility = View.VISIBLE
            updateEmailError?.text = status?.email
        }
        if(status?.given_name != null){
            updateFirstNameError?.visibility = View.VISIBLE
            updateFirstNameError?.text = status?.given_name
        }
    }
}