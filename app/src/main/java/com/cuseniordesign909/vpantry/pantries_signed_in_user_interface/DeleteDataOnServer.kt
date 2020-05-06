package com.cuseniordesign909.vpantry.pantries_signed_in_user_interface

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.PantriesSignedInActivity
import com.cuseniordesign909.vpantry.data_representations.ResponseStatus
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteDataOnServer : Fragment(), View.OnClickListener{

    private var deleteAllGroups : Button? = null
    private var deleteAllUsers : Button? = null
    private var deleteAllItems : Button? = null
    private var deleteDataOnServer : View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        deleteDataOnServer = inflater.inflate(R.layout.deletedataonserver, container, false)
        deleteAllGroups = deleteDataOnServer?.findViewById(R.id.deleteAllGroups)
        deleteAllGroups?.setOnClickListener(this)
        deleteAllUsers = deleteDataOnServer?.findViewById(R.id.deleteAllUsers)
        deleteAllUsers?.setOnClickListener(this)
        deleteAllItems = deleteDataOnServer?.findViewById(R.id.deleteAllItems)
        deleteAllItems?.setOnClickListener(this)
        (activity as AppCompatActivity)?.supportActionBar?.title = "Delete Data on Server"
        return deleteDataOnServer
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onClick(v: View?) {
        when(v?.id){
            R.id.deleteAllItems -> {
                CoroutineScope(Dispatchers.IO).async{
                    connect(3)
                }
            }
            R.id.deleteAllUsers -> {
                CoroutineScope(Dispatchers.IO).async{
                    connect(1)
                }
            }
            R.id.deleteAllGroups -> {
                CoroutineScope(Dispatchers.IO).async{
                    connect(2)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu?.findItem(R.id.app_bar_create_pantry).isVisible = false
        menu?.findItem(R.id.app_bar_sign_out).isVisible = false
        menu?.findItem(R.id.app_bar_user_info).isVisible = false
        menu?.findItem(R.id.delete_all_data).isVisible = false
    }
    private fun connect(route : Int){
        var api = PantriesSignedInActivity.retrofit?.create(NetworkConnection::class.java)
        var call : Call<ResponseStatus>? = null

        call?.enqueue(object : Callback<ResponseStatus> {
            override fun onResponse(
                call: Call<ResponseStatus>,
                response: Response<ResponseStatus>
            ) {
                var status: ResponseStatus? = response.body()
                Toast.makeText(context, status?.message, Toast.LENGTH_LONG).show()
            }
            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                t.printStackTrace()
                println("onFailure was called")
            }
        })
    }
}