package com.cuseniordesign909.vpantry.items_signed_in_user_interface

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
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

class ItemDetails : DialogFragment(), DialogInterface.OnShowListener, View.OnClickListener {
    private var itemDetails: View? = null
    private var itemDetailsName: TextView? = null
    private var itemDetailsType: TextView? = null
    private var itemDetailsQuantity: TextView? = null
    private var itemDetailsNote: TextView? = null
    private var itemDetailsExpirationDate: TextView? = null
    private var pantryName: String? = null
    private var _id: String? = null
    private var expirationDate: TextView? = null
    private var UPDATED_ITEM = 1
    private var item: Item? = null
    private var itemId: String? = null
    private var itemName: String? = null
    private var itemNote: String? = null
    private var itemType: String? = null
    private var itemQuantity: String? = null
    private var itemExpDate: Long? = 0

    var update = false
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        itemDetails = activity?.layoutInflater?.inflate(R.layout.itemdetails, null)
        item?._id = arguments?.getString("_id")
        itemId = item?._id
        item?.name = arguments?.getString("name") as String
        itemName = item?.name
        item?.note = arguments?.getString("note") as String
        itemNote = item?.note
        item?.type = arguments?.getString("type") as String
        itemType = item?.type
        item?.quantity = arguments?.getString("quantity") as String
        itemQuantity = item?.quantity
        item?.expirationDate = arguments?.getLong("expirationDate") as Long
        itemExpDate = item?.expirationDate
        pantryName = arguments?.getString("pantryName") as String
        _id = arguments?.getString("_id")
        initViews()
        var alertDialog = AlertDialog.Builder(activity)
        if (arguments?.getString("title") != null) {
            update = true
            alertDialog?.setTitle("Item Details")
            alertDialog?.setPositiveButton("Update Item", null)
            itemDetailsName?.setText(arguments?.getString("name"))
            itemDetailsType?.setText(arguments?.getString("type"))
            itemDetailsQuantity?.setText(arguments?.getString("quantity"))
            itemDetailsNote?.setText(arguments?.getString("note"))
            itemDetailsExpirationDate?.setText(SimpleDateFormat("MM/dd/yyyy").format(arguments?.getLong("expirationDate")))
        } else {
            alertDialog?.setTitle("Add Item")
            alertDialog?.setPositiveButton("Add Item", null)
        }
        alertDialog?.setView(itemDetails)
        alertDialog?.setNegativeButton("Cancel", null)
        var dialog = alertDialog?.create()
        dialog.setOnShowListener(this)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private fun initViews() {
        itemDetailsName = itemDetails?.findViewById(R.id.itemDetailsName)
        itemDetailsType = itemDetails?.findViewById(R.id.itemDetailsType)
        itemDetailsQuantity = itemDetails?.findViewById(R.id.itemDetailsQuantity)
        itemDetailsNote = itemDetails?.findViewById(R.id.itemDetailsNote)
        itemDetailsExpirationDate = itemDetails?.findViewById(R.id.itemDetailsExpirationDate)
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

    override fun onShow(dialog: DialogInterface?) {
        var submit = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        var cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        submit.setOnClickListener(this)
        cancel.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        println("view id = " + view?.id)
        println(AlertDialog.BUTTON_POSITIVE)
        println(AlertDialog.BUTTON_NEGATIVE)
        when (view?.id) {
            16908313 -> {
                //this calls the update view
                var addItem = AddItem()
                addItem.arguments = arguments
                addItem.setTargetFragment(this, UPDATED_ITEM)
                addItem.show((activity?.supportFragmentManager as FragmentManager),"Edit Item")
                //dismiss()
            }
            16908314 -> {
                dismiss()
            }
        }
    }
}