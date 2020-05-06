package com.cuseniordesign909.vpantry.pantries_signed_in_user_interface

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.PantriesSignedInActivity
import com.cuseniordesign909.vpantry.data_representations.SignUpandSignInCredentials
import com.cuseniordesign909.vpantry.data_representations.ResponseStatus
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ChangePassword : DialogFragment(), View.OnClickListener, DialogInterface.OnShowListener {
    private var changePassword : View? = null
    private var oldPassword : EditText? = null
    private var oldPasswordError : TextView? = null
    private var newPassword : EditText? = null
    private var newPasswordError : TextView? = null
    private var newPasswordCheck : EditText? = null
    private var newPasswordCheckError : TextView? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        changePassword = activity?.layoutInflater?.inflate(R.layout.changepassword, null)
        initViews()
        var alertDialog = AlertDialog.Builder(activity)
        alertDialog?.setTitle("Change Password")
        alertDialog?.setView(changePassword)
        alertDialog?.setPositiveButton("Confirm Change", null)
        alertDialog?.setNegativeButton("Cancel", null)
        var dialog = alertDialog?.create()
        dialog.setOnShowListener(this)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
    private fun initViews(){
        oldPassword = changePassword?.findViewById(R.id.oldPassword)
        oldPasswordError = changePassword?.findViewById(R.id.oldPasswordError)
        oldPasswordError?.visibility = View.GONE
        newPassword = changePassword?.findViewById(R.id.newPassword)
        newPasswordError = changePassword?.findViewById(R.id.newPasswordError)
        newPasswordError?.visibility = View.GONE
        newPasswordCheck = changePassword?.findViewById(R.id.newPasswordCheck)
        newPasswordCheckError = changePassword?.findViewById(R.id.newPasswordCheckError)
        newPasswordCheckError?.visibility = View.GONE
    }

    override fun onShow(dialog: DialogInterface?) {
        var submit = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        var cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        submit.setOnClickListener(this)
        cancel.setOnClickListener(this)
    }
    override fun onClick(view : View?) {
        oldPasswordError?.visibility = View.GONE
        newPasswordError?.visibility = View.GONE
        newPasswordCheckError?.visibility = View.GONE
        println("view id = " + view?.id)
        println(AlertDialog.BUTTON_POSITIVE)
        println(AlertDialog.BUTTON_NEGATIVE)
        when(view?.id){
            16908313 -> {
                var newPasswords = SignUpandSignInCredentials(
                    oldPassword?.text.toString(),
                    newPassword?.text.toString(),
                    newPasswordCheck?.text.toString()
                )
                println("calling connect(newPasswords)")
                println("oldpassword = "+
                    oldPassword?.text.toString())
                CoroutineScope(Dispatchers.IO).async{
                    connect(newPasswords)
                }
            }
            16908314 -> {
                dismiss()
            }
        }
    }
    private fun connect(newPasswords:SignUpandSignInCredentials?){
        var api = PantriesSignedInActivity.retrofit?.create(NetworkConnection::class.java)
        var call = api?.changePassword(newPasswords as SignUpandSignInCredentials)
        call?.enqueue(object : Callback<ResponseStatus>{
            override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                var changedStatus : ResponseStatus? = response.body()
                if(changedStatus?.success == true){
                    Toast.makeText(context, changedStatus?.message, Toast.LENGTH_SHORT).show()
                    dismiss()
                } else
                    insertErrors(changedStatus)
            }
            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                println("onFailure was called.")
                t.printStackTrace()
            }
        })
    }
    private fun insertErrors(status : ResponseStatus?){
        if(status?.old_password != null){
            oldPasswordError?.text = status?.old_password
            oldPasswordError?.visibility = View.VISIBLE
        }
        if(status?.password != null) {
            newPasswordError?.text = status?.password
            newPasswordError?.visibility = View.VISIBLE
        }
        if(status?.password2 != null){
            newPasswordCheckError?.text = status?.password2
            newPasswordCheckError?.visibility = View.VISIBLE
        }
    }
}