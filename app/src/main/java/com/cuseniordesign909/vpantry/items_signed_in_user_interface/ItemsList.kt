package com.cuseniordesign909.vpantry.items_signed_in_user_interface

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuseniordesign909.vpantry.ItemsSignedInActivity
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.PantriesSignedInActivity
import com.cuseniordesign909.vpantry.data_representations.Item
import com.cuseniordesign909.vpantry.data_representations.ResponseStatus
import com.cuseniordesign909.vpantry.pantries_signed_in_user_interface.EmailListAdapter
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import pl.kitek.rvswipetodelete.SwipeToDeleteCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ItemsList : Fragment(){
    private var itemsList : View? = null
    private  var listOfItems : RecyclerView? = null
    var items : ArrayList<Item>? = null
    var CREATED_ITEM = 0
    var UPDATED_ITEM = 1
    var _id : String? = null
    var name : String? = null
    var users : ArrayList<String>? = null
    var administrators : ArrayList<String>? = null
    var admin : Boolean? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        itemsList = inflater.inflate(R.layout.itemslist, container, false)
        listOfItems = itemsList?.findViewById(R.id.listOfItems) as RecyclerView
        items = ArrayList()
        CoroutineScope(Dispatchers.IO).async {
            connect(null,0)
        }


        var adapter = ItemsListItemAdapter(this, items)
        listOfItems?.adapter = adapter
        var layout = LinearLayoutManager(activity)
        listOfItems?.layoutManager = layout
        val swipeHandler1 = object : SwipeToDeleteCallback(activity?.applicationContext as Context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                var alertDialog = AlertDialog.Builder(context as Context)
                alertDialog.setTitle("Delete item?")
                alertDialog.setMessage("Are you sure you want to delete this item? This action cannot be undone.")
                alertDialog.setCancelable(false)
                alertDialog.setPositiveButton("Delete", object : DialogInterface.OnClickListener{
                    override fun onClick(v: DialogInterface?, p1: Int) {
                        val adapter = listOfItems?.adapter as ItemsListItemAdapter
                        adapter.removeAt(viewHolder.adapterPosition)
                        }

                })
                alertDialog.setNegativeButton("Cancel", object: DialogInterface.OnClickListener{
                    override fun onClick(v: DialogInterface?, p1: Int) {
                        val adapter = listOfItems?.adapter as ItemsListItemAdapter
                        adapter.notifyDataSetChanged()
                    }
                })
                alertDialog.create().show()

            }
        }

        val itemTouchHelper1 = ItemTouchHelper(swipeHandler1)
        itemTouchHelper1.attachToRecyclerView(listOfItems)
        var dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecoration(
                listOfItems?.context, layout.orientation
                )
        listOfItems?.addItemDecoration(dividerItemDecoration)
        if (items?.isEmpty() == true)
            listOfItems?.visibility = View.GONE
        return itemsList
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        CoroutineScope(Dispatchers.IO).async {
                connect(null , 0)
            }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity)?.supportActionBar?.title = name
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        admin = arguments?.getBoolean("admin")
        _id = arguments?.getString("_id")
        name = arguments?.getString("name")
        users = arguments?.getStringArrayList("users")
        administrators = arguments?.getStringArrayList("administrators")
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.items_list_menu, menu)
    }

    override fun onResume(){
        super.onResume()
        (activity as AppCompatActivity)?.supportActionBar?.title = name
        CoroutineScope(Dispatchers.IO).async {
            connect(null, 0)
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.app_bar_add_item -> {
                var addItem = AddItem()
                var bundle = Bundle()
                bundle?.putString("pantryName", name)
                bundle?.putString("_id", _id)
                addItem?.arguments = bundle
                addItem?.setTargetFragment(this, CREATED_ITEM)
                addItem.show(fragmentManager as FragmentManager, "Add Item")
                true
            }
            R.id.app_bar_pantry_info -> {
                var b = Bundle()
                var pantryInfo = PantryInfo()
                b.putString("_id", _id)
                b.putBoolean("admin", admin as Boolean)
                b.putStringArrayList("administrators", arguments?.getStringArrayList("administrators"))
                b.putStringArrayList("users", arguments?.getStringArrayList("users"))
                pantryInfo.arguments = b
                activity?.supportFragmentManager?.beginTransaction()?.add(R.id.signedInFragmentManager, pantryInfo, "PantryInfo")?.addToBackStack("PantryInfo")?.commit()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun connect(item : Item?, pos : Int){
        if(item == null) {
            var api = ItemsSignedInActivity.retrofit?.create(NetworkConnection::class.java)
            var call = api?.getItems(_id as String)
            call?.enqueue(object : Callback<ResponseStatus> {
                override fun onResponse(
                    call: Call<ResponseStatus>,
                    response: Response<ResponseStatus>
                ) {
                    var getItems = response.body()
                    if (getItems?.success == true) {
                        items = getItems?.items
                        println(items?.size)
                        if (items?.isEmpty() == false) {
                            listOfItems?.visibility = View.VISIBLE
                            var adapter = ItemsListItemAdapter(this@ItemsList, items)
                            listOfItems?.adapter = adapter
                            listOfItems?.adapter?.notifyDataSetChanged()
                        }
                    } else
                        Toast.makeText(context, getItems?.message, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    println("onFailure was called.")
                    t.printStackTrace()
                }
            })
        } else {
            var api = ItemsSignedInActivity.retrofit?.create(NetworkConnection::class.java)
            var call = api?.deleteItem(item?._id as String)
            call?.enqueue(object : Callback<ResponseStatus> {
                override fun onResponse(
                    call: Call<ResponseStatus>,
                    response: Response<ResponseStatus>
                ) {
                    var getItems = response.body()
                    if (getItems?.success == true) {
                        Toast.makeText(context, getItems?.message, Toast.LENGTH_SHORT).show()
                        items?.removeAt(pos)
                        listOfItems?.adapter?.notifyItemRemoved(pos)
                    } else
                        Toast.makeText(context, getItems?.message, Toast.LENGTH_SHORT).show()
                }

                override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                    println("onFailure was called.")
                    t.printStackTrace()
                }
            })
        }
    }
    fun deleteItem(item : Item?, pos : Int){
        CoroutineScope(Dispatchers.IO).async {
            connect(item, pos)
        }
    }
}
