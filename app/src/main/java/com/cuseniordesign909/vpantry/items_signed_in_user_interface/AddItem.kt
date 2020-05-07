package com.cuseniordesign909.vpantry.items_signed_in_user_interface

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.cuseniordesign909.vpantry.ItemsSignedInActivity
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.data_representations.Item
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat

class AddItem : DialogFragment(), DialogInterface.OnShowListener, View.OnClickListener, DatePicker.OnDateChangedListener{
    private var addItem : View? = null
    private var addItemName : EditText? = null
    private var addItemNameError : TextView? = null
    private var addItemType : EditText? = null
    private var addItemTypeError : TextView? = null
    private var addItemQuantity : EditText? = null
    private var addItemNote : EditText? = null
    private var addItemExpirationDate : DatePicker? = null
    private var pantryName : String? = null
    private var _id : String? = null
    private var expirationDate : Long = 0
    var update = false
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        addItem = activity?.layoutInflater?.inflate(R.layout.additem, null)
        pantryName = arguments?.getString("pantryName")
        _id = arguments?.getString("_id")
        initViews()
        var alertDialog = AlertDialog.Builder(activity)
        if(arguments?.getString("title")!=null) {
            update = true
            alertDialog?.setTitle("Edit Item")
            alertDialog?.setPositiveButton("Update Item", null)
            addItemName?.setText(arguments?.getString("name"))
            addItemType?.setText(arguments?.getString("type"))
            addItemQuantity?.setText(arguments?.getString("quantity"))
            addItemNote?.setText(arguments?.getString("note"))
        } else {
            alertDialog?.setTitle("Add Item")
            alertDialog?.setPositiveButton("Add Item", null)
        }
        alertDialog?.setView(addItem)
        alertDialog?.setNegativeButton("Cancel", null)
        var dialog = alertDialog?.create()
        dialog.setOnShowListener(this)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }
    private fun initViews(){
        addItemName = addItem?.findViewById(R.id.addItemName)
        addItemNameError = addItem?.findViewById(R.id.addItemNameError)
        addItemNameError?.visibility = View.GONE
        addItemType = addItem?.findViewById(R.id.addItemType)
        addItemTypeError = addItem?.findViewById(R.id.addItemTypeError)
        addItemTypeError?.visibility = View.GONE
        addItemQuantity = addItem?.findViewById(R.id.addItemQuantity)
        addItemNote = addItem?.findViewById(R.id.addItemNote)
        addItemExpirationDate = addItem?.findViewById(R.id.addItemExpirationDate)
        addItemExpirationDate?.setOnDateChangedListener(this)
        //expirationDate = addItemExpirationDate?. as Long
    }

    /*override fun onSelectedDayChange(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        var mon = month+1
        var date = "$dayOfMonth/$mon/$year"
        Toast.makeText(context, "date = $date", Toast.LENGTH_LONG).show()
        var dateFormat = SimpleDateFormat("d/m/yyyy")
        var d = dateFormat.parse(date)
        expirationDate = d.time
    }*/
    override fun onDateChanged(view: DatePicker, year: Int, month: Int, dayOfMonth: Int) {
        var mon = month+1
        var date = "$dayOfMonth/$mon/$year"
        Toast.makeText(context, "date = $date", Toast.LENGTH_LONG).show()
        var dateFormat = SimpleDateFormat("d/m/yyyy")
        var d = dateFormat.parse(date)
        expirationDate = d.time
    }

    override fun onShow(dialog: DialogInterface?) {
        var submit = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        var cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        submit.setOnClickListener(this)
        cancel.setOnClickListener(this)
    }
    override fun onClick(view : View?) {
        addItemNameError?.visibility = View.GONE
        addItemTypeError?.visibility = View.GONE
        println("view id = " + view?.id)
        println(AlertDialog.BUTTON_POSITIVE)
        println(AlertDialog.BUTTON_NEGATIVE)
        when(view?.id){
            16908313 -> {
                var newItem = Item(
                    addItemName?.text.toString(),
                    pantryName as String,
                    addItemType?.text.toString(),
                    addItemQuantity?.text.toString(),
                    expirationDate,
                    addItemNote?.text.toString(),
                    _id as String
                )
                if(update)
                    newItem._id = arguments?.getString("item_id")
                //Toast.makeText(context, SimpleDateFormat("MM/dd/yyyy").format(addItemExpirationDate?.date), Toast.LENGTH_LONG).show()
                CoroutineScope(Dispatchers.IO).async{
                    connect(newItem)
                }
            }
            16908314 -> {
                dismiss()
            }
        }
    }
    private fun connect(item: Item?){
        var api = ItemsSignedInActivity.retrofit?.create(NetworkConnection::class.java)
        var call : Call<Item>?
        if(update)
            call = api?.updateItem(item as Item)
        else
            call = api?.addItem(item as Item)
        call?.enqueue(object : Callback<Item> {
            override fun onResponse(call: Call<Item>, response: Response<Item>) {
                var status : Item? = response.body()
                if(status?.success == true){
                    Toast.makeText(context, status?.message, Toast.LENGTH_SHORT).show()
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                    dismiss()
                } else
                    insertErrors(status)
            }

            override fun onFailure(call: Call<Item>, t: Throwable) {
                println("onFailure was called.")
                t.printStackTrace()
            }
        })
    }
    private fun insertErrors(status : Item?){
        if(status?.name != null){
            addItemNameError?.text = status?.name
            addItemNameError?.visibility = View.VISIBLE
        }
        if(status?.type != null) {
            addItemTypeError?.text = status?.type
            addItemTypeError?.visibility = View.VISIBLE
        }
    }
}