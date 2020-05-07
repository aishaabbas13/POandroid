package com.cuseniordesign909.vpantry.items_signed_in_user_interface

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuseniordesign909.vpantry.PantriesSignedInActivity
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.data_representations.EmailListData
import com.cuseniordesign909.vpantry.data_representations.Pantry
import com.cuseniordesign909.vpantry.data_representations.ResponseStatus
import com.cuseniordesign909.vpantry.pantries_signed_in_user_interface.AddUserorAdmin
import com.cuseniordesign909.vpantry.pantries_signed_in_user_interface.EmailListAdapter
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import com.cuseniordesign909.vpantry.user_interface_features.DividerItemDecorator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import pl.kitek.rvswipetodelete.SwipeToDeleteCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddUsersAdmins : DialogFragment(), View.OnClickListener, DialogInterface.OnShowListener{
    private var updatePantryAddUA: View? = null
    private var updatePantryAddAdminsList: RecyclerView? = null
    private var updatePantryAddAdministrator: Button? = null
    private var updatePantryAddUsersList: RecyclerView? = null
    private var updatePantryAddUser: Button? = null
    private var adminsList: ArrayList <EmailListData>? = null
    private var usersList: ArrayList <EmailListData>? = null
    private var pantry: Pantry? = null
    val GET_USER : Int = 1
    val GET_ADMIN : Int = 2
    val EDIT_USER : Int = 3
    val EDIT_ADMIN : Int = 4

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        updatePantryAddUA = activity?.layoutInflater?.inflate(R.layout.updatepantryaddua, null)
        updatePantryAddAdministrator = updatePantryAddUA?.findViewById(R.id.updatePantryAddAdministrator)
        updatePantryAddUser = updatePantryAddUA?.findViewById(R.id.updatePantryAddUser)
        updatePantryAddAdministrator?.setOnClickListener(this)
        updatePantryAddUser?.setOnClickListener(this)
        initRecyclerViews()
        adminsList = ArrayList()
        usersList = ArrayList()
        var alertDialog = AlertDialog.Builder(activity)
        alertDialog?.setTitle("Add Administrators or Users")
        alertDialog?.setView(updatePantryAddUA)
        alertDialog?.setPositiveButton("Add", null)
        alertDialog?.setNegativeButton("Cancel", null)
        var dialog = alertDialog?.create()
        dialog.setOnShowListener(this)
        dialog.setCanceledOnTouchOutside(false)
        return dialog
    }

    private fun initRecyclerViews(){
        adminsList = ArrayList()
        usersList = ArrayList()
        updatePantryAddAdminsList = updatePantryAddUA?.findViewById(R.id.updatePantryAddAdminsList)
        updatePantryAddUsersList = updatePantryAddUA?.findViewById(R.id.updatePantryAddUsersList)
        var dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(context as Context, R.drawable.divider) as Drawable
            )
        updatePantryAddAdminsList?.adapter = EmailListAdapter(adminsList, "Edit Administrator", null, this)
        updatePantryAddUsersList?.adapter = EmailListAdapter(usersList, "Edit User", null, this)
        updatePantryAddAdminsList?.layoutManager = LinearLayoutManager(activity)
        updatePantryAddUsersList?.layoutManager = LinearLayoutManager(activity)
        updatePantryAddUsersList?.isNestedScrollingEnabled = false
        updatePantryAddAdminsList?.isNestedScrollingEnabled = false
        updatePantryAddUsersList?.addItemDecoration(dividerItemDecoration)
        updatePantryAddAdminsList?.addItemDecoration(dividerItemDecoration)
        val swipeHandler1 = object : SwipeToDeleteCallback(activity?.applicationContext as Context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = updatePantryAddAdminsList?.adapter as EmailListAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val swipeHandler2 = object : SwipeToDeleteCallback(activity?.applicationContext as Context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = updatePantryAddUsersList?.adapter as EmailListAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper1 = ItemTouchHelper(swipeHandler1)
        val itemTouchHelper2 = ItemTouchHelper(swipeHandler2)
        itemTouchHelper1.attachToRecyclerView(updatePantryAddAdminsList)
        itemTouchHelper2.attachToRecyclerView(updatePantryAddUsersList)
    }

    override fun onClick(view : View?) {
        println("view id = " + view?.id)
        println(AlertDialog.BUTTON_POSITIVE)
        println(AlertDialog.BUTTON_NEGATIVE)
        when(view?.id){
            16908313 -> {
                CoroutineScope(Dispatchers.IO).async{
                    connect()
                }
            }
            16908314 -> {
                dismiss()
            }
            R.id.updatePantryAddAdministrator -> {
                var b = Bundle()
                b.putString("mode", "Add Administrator")
                b.putString("description", "Administrators can add or remove users as well as other administrators and can add, remove items or edit items.")
                var addAdminDialog = AddUserorAdmin()
                addAdminDialog.arguments = b
                addAdminDialog.setTargetFragment(this, GET_ADMIN)
                addAdminDialog.show(fragmentManager as FragmentManager, "Add Administrator")
            }
            R.id.updatePantryAddUser -> {
                var b = Bundle()
                b.putString("mode", "Add User")
                b.putString("description", "Users can add, remove items or edit items.")
                var addUserDialog = AddUserorAdmin()
                addUserDialog.arguments = b
                addUserDialog.setTargetFragment(this, GET_USER)
                addUserDialog.show(fragmentManager as FragmentManager, "Add User")
            }
        }
    }

    override fun onShow(dialog: DialogInterface?) {
        var submit = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
        var cancel = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        submit.setOnClickListener(this)
        cancel.setOnClickListener(this)
    }

    private fun connect(){
        var api = PantriesSignedInActivity.retrofit?.create(NetworkConnection::class.java)
        pantry = Pantry()
        pantry?.mode = 3
        var aEmails = ArrayList<String>()
        var uEmails = ArrayList<String>()
        for(admin in adminsList.orEmpty())
            aEmails.add(admin?.email as String)
        for(user in usersList.orEmpty())
            uEmails.add(user?.email as String)
        pantry?.administrators = aEmails
        pantry?.users = uEmails

        var call = api?.updatePantry(pantry as Pantry)
        call?.enqueue(object : Callback<ResponseStatus> {
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

    private fun insertErrors(result : ResponseStatus?){
        if(result?.admins != null)
            listErrors(adminsList, result?.admins, updatePantryAddAdminsList)
        if(result?.users != null)
            listErrors(usersList, result?.users, updatePantryAddAdminsList)
    }

    private fun listErrors(list : ArrayList<EmailListData>?, errors : ArrayList<String>?, recycler : RecyclerView?){
        for(i in 0 until list?.size as Int) {
            if(errors != null)
                list!![i].error = errors!![i]
            else
                list!![i].error = ""
            recycler?.adapter?.notifyItemChanged(i)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var index = data?.getIntExtra("index", 0) as Int
        when(requestCode){
            GET_USER -> {
                usersList?.add(EmailListData(data?.getStringExtra("email") as String, ""))
                updatePantryAddAdminsList?.adapter?.notifyItemInserted(usersList?.size as Int - 1)
                if(usersList?.size as Int > 1)
                    updatePantryAddUsersList?.adapter?.notifyItemChanged(usersList?.size as Int - 1)
            }
            GET_ADMIN -> {
                adminsList?.add(EmailListData(data?.getStringExtra("email") as String, ""))
                updatePantryAddAdminsList?.adapter?.notifyItemInserted(adminsList?.size as Int -1)
            }
            EDIT_USER -> {
                usersList?.get(index)?.email = data?.getStringExtra("email")
                updatePantryAddUsersList?.adapter?.notifyItemChanged(index)
            }
            EDIT_ADMIN -> {
                adminsList?.get(index)?.email = data?.getStringExtra("email")
                updatePantryAddAdminsList?.adapter?.notifyItemChanged(index)
            }
        }
    }
}
