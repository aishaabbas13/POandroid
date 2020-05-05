package com.cuseniordesign909.vpantry.pantries_signed_in_user_interface

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.PantriesSignedInActivity
import com.cuseniordesign909.vpantry.SignedOutActivity
import com.cuseniordesign909.vpantry.data_representations.Pantry
import com.cuseniordesign909.vpantry.data_representations.ResponseStatus
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import com.cuseniordesign909.vpantry.user_interface_features.DividerItemDecorator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class PantriesList : Fragment(){
    private var pantryList : View? = null
    private  var listOfPantries : RecyclerView? = null
    var pantries : ArrayList<Pantry>? = null
    var CREATED_PANTRY = 0
    var user_id : String? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        pantryList = inflater.inflate(R.layout.pantrylist, container, false)
        listOfPantries = pantryList?.findViewById(R.id.listOfPantries) as RecyclerView
        pantries = ArrayList()
        /*CoroutineScope(Dispatchers.IO).async {
            connect()
        }*/

        var dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(context as Context, R.drawable.divider) as Drawable
            )
        var adapter = PantryListItemAdapter(pantries)
        listOfPantries?.adapter = adapter
        listOfPantries?.layoutManager = LinearLayoutManager(activity)
        listOfPantries?.addItemDecoration(dividerItemDecoration)
        if (pantries?.isEmpty() == true)
            listOfPantries?.visibility = View.GONE
        return pantryList
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CREATED_PANTRY){
            CoroutineScope(Dispatchers.IO).async {
                connect()
            }
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity)?.supportActionBar?.title = "Pantries"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.pantries_list_menu, menu)
    }

    override fun onResume(){
        super.onResume()
        (activity as AppCompatActivity)?.supportActionBar?.title = "Pantries"
        CoroutineScope(Dispatchers.IO).async {
            connect()
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.app_bar_create_pantry -> {
                var createPantry = CreatePantry()
                createPantry?.setTargetFragment(this, CREATED_PANTRY)
                activity!!.supportFragmentManager.beginTransaction().add(R.id.signedInFragmentManager, createPantry, "CreatePantry").addToBackStack("CreatePantry").commit()
                true
            }
            R.id.app_bar_user_info -> {
                activity!!.supportFragmentManager.beginTransaction().add(R.id.signedInFragmentManager, UserInfo(), "UserInfo").addToBackStack("UserInfo").commit()
                true
            }
            R.id.app_bar_sign_out -> {
                val editor = PantriesSignedInActivity.preferences!!.edit()
                editor.putString(getString(R.string.token), null)
                editor.apply()
                Toast.makeText(context, getString(R.string.signed_out), Toast.LENGTH_SHORT).show()
                var intent = Intent(activity?.applicationContext, SignedOutActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)
                activity?.finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun connect(){
        var api = PantriesSignedInActivity.retrofit?.create(NetworkConnection::class.java)
        var call = api?.getPantries()
        call?.enqueue(object : Callback<ResponseStatus> {
            override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                var getPantries = response.body()
                if(getPantries?.success == true) {
                    pantries = getPantries?.pantries
                    user_id = getPantries?.user_id
                    println(pantries?.size)
                    if(pantries?.isEmpty() == false) {
                        listOfPantries?.visibility = View.VISIBLE
                        var adapter = PantryListItemAdapter(pantries)
                        listOfPantries?.adapter = adapter
                        listOfPantries?.adapter?.notifyDataSetChanged()
                    }
                }
                else
                    Toast.makeText(context, getPantries?.message, Toast.LENGTH_SHORT).show()
            }
            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                println("onFailure was called.")
                t.printStackTrace()
            }
        })
    }
}
