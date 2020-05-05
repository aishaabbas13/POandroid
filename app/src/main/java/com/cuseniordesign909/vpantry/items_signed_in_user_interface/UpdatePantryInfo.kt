package com.cuseniordesign909.vpantry.items_signed_in_user_interface

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.cuseniordesign909.vpantry.ItemsSignedInActivity
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.data_representations.EmailListData
import com.cuseniordesign909.vpantry.data_representations.Pantry
import com.cuseniordesign909.vpantry.data_representations.ResponseStatus
import com.cuseniordesign909.vpantry.pantries_signed_in_user_interface.EmailListAdapter
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
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
                pantry?.mode = 1
                pantry?.name = updatePantryName?.text.toString()
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
                        updatePantryName?.setText(pantry?.name)
                        adminEmails = ArrayList()
                        userEmails = ArrayList()
                        updateList(adminEmails, pantry?.administrators)
                        updateList(userEmails, pantry?.users)
                        var adminAdapter = EmailListAdapter(adminEmails, null, null)
                        updatePantryAdmins?.adapter = adminAdapter
                        updatePantryAdmins?.adapter?.notifyDataSetChanged()
                        var userAdapter = EmailListAdapter(userEmails, null, null)
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
        } else if(mode == 1){
            var call = api?.updatePantry(pantry as Pantry)
            call?.enqueue(object : Callback<ResponseStatus> {
                override fun onResponse(
                    call: Call<ResponseStatus>,
                    response: Response<ResponseStatus>
                ) {
                    var status = response.body()
                    if (status?.success == false) {
                        updatePantryNameError?.text = status?.message
                        updatePantryNameError?.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    println("onFailure was called.")
                    t.printStackTrace()
                }
            })
        }
    }
    private fun updateList(list : ArrayList<EmailListData>?, emails : ArrayList<String>?){
        for(i in 0 until emails?.size as Int)
            list?.add(EmailListData(emails!![i], ""))
    }
}