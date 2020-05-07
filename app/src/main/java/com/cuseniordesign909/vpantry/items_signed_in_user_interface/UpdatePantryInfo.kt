package com.cuseniordesign909.vpantry.items_signed_in_user_interface

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
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
        CoroutineScope(Dispatchers.IO).async {
            connect(0)
        }
        return updatePantryInfo
    }
    override fun onClick(view: View?) {
        updatePantryNameError?.visibility = View.GONE
        when(view?.id){
            R.id.updatePantryNameButton-> {
                pantry = Pantry()
                pantry?._id = arguments?.getString("_id")
                pantry?.mode = 1
                pantry?.name = updatePantryName?.text.toString()
                CoroutineScope(Dispatchers.IO).async {
                    connect(1)
                }
            }
            R.id.updatePantryAddUsersButton->{

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
                        updatePantryAdmins?.adapter = adminAdapter
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
                    if (status?.success == false) {
                        updatePantryNameError?.text = status?.group_name
                        updatePantryNameError?.visibility = View.VISIBLE
                    } else
                        Toast.makeText(context, status?.message, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    println("onFailure was called.")
                    t.printStackTrace()
                }
            })
        }
    }
    private fun initRecyclerViews(){
        updatePantryAdmins =updatePantryInfo?.findViewById(R.id.createPantryAdmins)
        updatePantryUsers = updatePantryInfo?.findViewById(R.id.createPantryUsers)
        var dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(context as Context, R.drawable.divider) as Drawable
            )
        updatePantryAdmins?.adapter = EmailListAdapter(adminEmails, "Edit Administrator", null, null)
        updatePantryUsers?.adapter = EmailListAdapter(userEmails, "Edit User", null, null)
        updatePantryAdmins?.layoutManager = LinearLayoutManager(activity)
        updatePantryUsers?.layoutManager = LinearLayoutManager(activity)
        updatePantryUsers?.isNestedScrollingEnabled = false
        updatePantryAdmins?.isNestedScrollingEnabled = false
        updatePantryUsers?.addItemDecoration(dividerItemDecoration)
        updatePantryAdmins?.addItemDecoration(dividerItemDecoration)
        val swipeHandler1 = object : SwipeToDeleteCallback(activity?.applicationContext as Context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = updatePantryAdmins?.adapter as EmailListAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val swipeHandler2 = object : SwipeToDeleteCallback(activity?.applicationContext as Context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = updatePantryUsers?.adapter as EmailListAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper1 = ItemTouchHelper(swipeHandler1)
        val itemTouchHelper2 = ItemTouchHelper(swipeHandler2)
        itemTouchHelper1.attachToRecyclerView(updatePantryAdmins)
        itemTouchHelper2.attachToRecyclerView(updatePantryUsers)
    }
    private fun updateList(list : ArrayList<EmailListData>?, emails : ArrayList<String>?){
        for(i in 0 until emails?.size as Int)
            list?.add(EmailListData(emails!![i], ""))
    }
}