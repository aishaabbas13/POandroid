package com.cuseniordesign909.vpantry.items_signed_in_user_interface

import android.content.Context
import android.content.DialogInterface
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuseniordesign909.vpantry.ItemsSignedInActivity
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.data_representations.EmailListData
import com.cuseniordesign909.vpantry.data_representations.Pantry
import com.cuseniordesign909.vpantry.data_representations.ResponseStatus
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

class UpdatePantryInfo : Fragment(), View.OnClickListener {
    private var updatePantryInfo : View? = null
    private var updatePantryName : EditText? = null
    private var updatePantryNameError : TextView? = null
    private var updatePantryNameButton : Button? = null
    private var updatePantryAdmins : RecyclerView? = null
    private var updatePantryUsers : RecyclerView? = null
    private var updatePantryAddUsersButton : Button? = null
    private var mode : Int = 0
    private var pantry : Pantry? = null
    private var adminEmails : ArrayList<EmailListData>? = null
    private var userEmails : ArrayList<EmailListData>? = null
    private var userIds : ArrayList<String>? = null
    private var adminIds : ArrayList<String>? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        updatePantryInfo = inflater.inflate(R.layout.updatepantryinfo, container, false)
        updatePantryName = updatePantryInfo?.findViewById(R.id.updatePantryName)
        updatePantryNameError = updatePantryInfo?.findViewById(R.id.updatePantryNameError)
        updatePantryNameError?.visibility = View.GONE
        updatePantryNameButton = updatePantryInfo?.findViewById(R.id.updatePantryNameButton)
        updatePantryAdmins = updatePantryInfo?.findViewById(R.id.updatePantryAdmins)
        updatePantryUsers = updatePantryInfo?.findViewById(R.id.updatePantryUsers)
        updatePantryAddUsersButton = updatePantryInfo?.findViewById(R.id.updatePantryAddUsersButton)
        updatePantryAddUsersButton?.setOnClickListener(this)
        updatePantryNameButton?.setOnClickListener(this)
        initRecyclerViews()
        userIds = arguments?.getStringArrayList("users")
        adminIds = arguments?.getStringArrayList("administrators")
        pantry = Pantry()
        pantry?._id = arguments?.getString("_id")
        CoroutineScope(Dispatchers.IO).async {
            connect(0)
        }
        return updatePantryInfo
    }
    override fun onClick(view: View?) {
        updatePantryNameError?.visibility = View.GONE
        when(view?.id){
            R.id.updatePantryNameButton-> {
                pantry?.mode = 1
                pantry?.name = updatePantryName?.text.toString()
                CoroutineScope(Dispatchers.IO).async {
                    connect(1)
                }
            }
            R.id.updatePantryAddUsersButton->{
                var addItem = AddUsersAdmins()
                var bundle = Bundle()
                bundle?.putString("_id", pantry?._id)
                addItem.arguments = bundle
                addItem.show((activity?.supportFragmentManager as FragmentManager),"Edit Item")
            }
        }
    }
    private fun connect(m : Int){
        var api = ItemsSignedInActivity.retrofit?.create(NetworkConnection::class.java)
        if(m == 0) {
            var call = api?.getPantry(arguments?.getString("_id") as String)
            call?.enqueue(object : Callback<ResponseStatus> {
                override fun onResponse(
                    call: Call<ResponseStatus>,
                    response: Response<ResponseStatus>
                ) {
                    var status = response.body()
                    if (status?.success == true) {
                        pantry = status.pantry
                        for(admin in pantry?.administrators.orEmpty())
                            adminEmails?.add(EmailListData(admin, ""))
                        for(user in pantry?.users.orEmpty())
                            userEmails?.add(EmailListData(user, ""))
                        updatePantryName?.setText(pantry?.name)
                        var adminAdapter = EmailListAdapter(adminEmails, null, null, null)
                        updatePantryAdmins?.adapter?.notifyDataSetChanged()
                        var userAdapter = EmailListAdapter(userEmails, null, null, null)
                        updatePantryUsers?.adapter = userAdapter
                        updatePantryUsers?.adapter?.notifyDataSetChanged()
                    } else
                        Toast.makeText(context, status?.message, Toast.LENGTH_SHORT).show()
                }
                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    println("onFailure was called.")
                    t.printStackTrace()
                }
            })
        } else if(m == 1){
            var call = api?.updatePantry(pantry as Pantry)
            call?.enqueue(object : Callback<ResponseStatus> {
                override fun onResponse(
                    call: Call<ResponseStatus>,
                    response: Response<ResponseStatus>
                ) {
                    var status = response.body()
                    if(pantry?.mode == 1) {
                        if (status?.success == false) {
                            updatePantryNameError?.text = status?.group_name
                            updatePantryNameError?.visibility = View.VISIBLE
                        } else
                            Toast.makeText(context, status?.message, Toast.LENGTH_SHORT).show()
                    } else if(pantry?.mode == 2){
                        if(status?.success == true)
                            Toast.makeText(context, status?.message, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    println("onFailure was called.")
                    t.printStackTrace()
                }
            })
        }
    }
    private fun initRecyclerViews(){
        adminEmails = ArrayList()
        userEmails = ArrayList()
        updatePantryAdmins =updatePantryInfo?.findViewById(R.id.updatePantryAdmins)
        updatePantryUsers = updatePantryInfo?.findViewById(R.id.updatePantryUsers)
        var dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(context as Context, R.drawable.divider) as Drawable
            )
        updatePantryAdmins?.adapter = EmailListAdapter(adminEmails, "", null, null)
        updatePantryUsers?.adapter = EmailListAdapter(userEmails, "", null, null)
        updatePantryAdmins?.layoutManager = LinearLayoutManager(activity)
        updatePantryUsers?.layoutManager = LinearLayoutManager(activity)
        updatePantryUsers?.isNestedScrollingEnabled = false
        updatePantryAdmins?.isNestedScrollingEnabled = false
        updatePantryUsers?.addItemDecoration(dividerItemDecoration)
        updatePantryAdmins?.addItemDecoration(dividerItemDecoration)
        val swipeHandler1 = object : SwipeToDeleteCallback(activity?.applicationContext as Context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var alertDialog = AlertDialog.Builder(context as Context)
                alertDialog.setTitle("Remove administrator?")
                alertDialog.setMessage("Are you sure you want to remove this user from ? This cannot be undone.")
                alertDialog.setCancelable(false)
                alertDialog.setPositiveButton("Delete", object : DialogInterface.OnClickListener{
                    override fun onClick(v: DialogInterface?, p1: Int) {
                        var pos = viewHolder.adapterPosition
                        println("pos = $pos")
                        val adapter = updatePantryAdmins?.adapter as EmailListAdapter
                        pantry?.mode = 2
                        pantry?.remove_user = adminIds?.get(pos) as String
                        println("id = this new value $id")
                        CoroutineScope(Dispatchers.IO).async {
                            connect(1)
                        }
                        adapter.removeAt(pos)
                        adminIds?.removeAt(pos)
                    }
                })
                alertDialog.setNegativeButton("Cancel", object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        updatePantryAdmins?.adapter?.notifyDataSetChanged()
                    }
                })
                alertDialog.create().show()
            }
        }
        val swipeHandler2 = object : SwipeToDeleteCallback(activity?.applicationContext as Context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var alertDialog = AlertDialog.Builder(context as Context)
                alertDialog.setTitle("Remove user?")
                alertDialog.setMessage("Are you sure you want to remove this user from ? This cannot be undone.")
                alertDialog.setCancelable(false)
                alertDialog.setPositiveButton("Delete", object : DialogInterface.OnClickListener{
                    override fun onClick(v: DialogInterface?, p1: Int) {
                        var pos = viewHolder.adapterPosition
                        println("pos = $pos")
                        val adapter = updatePantryUsers?.adapter as EmailListAdapter
                        pantry?.mode = 2
                        pantry?.remove_user = userIds?.get(pos) as String
                        println("id = this new value {pantry?.$id}")
                        CoroutineScope(Dispatchers.IO).async {
                            connect(1)
                        }
                        adapter.removeAt(pos)
                        userIds?.removeAt(pos)
                    }
                })
                alertDialog.setNegativeButton("Cancel", object : DialogInterface.OnClickListener{
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        updatePantryUsers?.adapter?.notifyDataSetChanged()
                    }
                })
                alertDialog.create().show()
            }
        }
        val itemTouchHelper1 = ItemTouchHelper(swipeHandler1)
        val itemTouchHelper2 = ItemTouchHelper(swipeHandler2)
        itemTouchHelper1.attachToRecyclerView(updatePantryAdmins)
        itemTouchHelper2.attachToRecyclerView(updatePantryUsers)
    }
}