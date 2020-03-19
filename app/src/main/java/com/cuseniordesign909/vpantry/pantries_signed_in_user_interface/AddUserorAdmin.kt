package com.cuseniordesign909.vpantry.pantries_signed_in_user_interface

import android.app.Activity.RESULT_OK
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.cuseniordesign909.vpantry.R

class AddUserorAdmin : DialogFragment(), View.OnClickListener, DialogInterface.OnShowListener {
    private var addUserDescription : TextView? = null
    private var addUserorAdmin : View? = null
    private var userEmail : EditText? = null
    private var emailNotExistsError : TextView? = null
    private var index : Int? = null
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        addUserorAdmin = activity?.layoutInflater?.inflate(R.layout.addadminoruser, null)
        initViews()

        var alertDialog = AlertDialog.Builder(activity)
        alertDialog?.setTitle(arguments?.get("mode") as String)
        addUserDescription?.text = arguments?.get("description") as String
        alertDialog?.setView(addUserorAdmin)
        alertDialog?.setPositiveButton(arguments?.get("mode") as String, null)
        alertDialog?.setNegativeButton("Cancel", null)
        var dialog = alertDialog?.create()
        dialog.setOnShowListener(this)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
    private fun initViews(){
        addUserDescription = addUserorAdmin?.findViewById(R.id.addUserDescription)
        userEmail = addUserorAdmin?.findViewById(R.id.userEmail)
        emailNotExistsError = addUserorAdmin?.findViewById(R.id.emailNotExistsError)
        emailNotExistsError?.visibility = View.GONE
        if(arguments?.getString("mode") == "Edit User" || arguments?.getString("mode") == "Edit Administrator") {
            userEmail?.setText(arguments?.getString("email"))
            index = arguments?.getInt("index")
        }
    }

    override fun onShow(dialog: DialogInterface?) {
        var submit = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        var cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        submit.setOnClickListener(this)
        cancel.setOnClickListener(this)
    }
    override fun onClick(view : View?) {
        emailNotExistsError?.visibility = View.GONE
        when(view?.id){
            16908313 -> {
                var intent = Intent()
                if(userEmail?.text.toString() != ""){
                    intent.putExtra("email", userEmail?.text.toString())
                    intent.putExtra("index", index)
                    targetFragment?.onActivityResult(targetRequestCode, RESULT_OK, intent)
                    dismiss()
                } else
                    insertError("An email is required.")
            }
            16908314 -> {
                dismiss()
            }
        }
    }
    private fun insertError(status : String){
        emailNotExistsError?.text = status
        emailNotExistsError?.visibility = View.VISIBLE
    }
}