package com.cuseniordesign909.vpantry.pantries_signed_in_user_interface

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.PantriesSignedInActivity
import com.cuseniordesign909.vpantry.data_representations.EmailListData
import com.cuseniordesign909.vpantry.data_representations.Pantry
import com.cuseniordesign909.vpantry.data_representations.ResponseStatus
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import com.cuseniordesign909.vpantry.user_interface_features.DividerItemDecorator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import pl.kitek.rvswipetodelete.SwipeToDeleteCallback
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreatePantry : Fragment(), View.OnClickListener {
    private var createPantry : View? = null
    private var createPantryName : EditText? = null
    private var pantryNameError : TextView? = null
    private var createPantryButton : Button? = null
    private var createPantryAddAdministrator : Button? = null
    private var createPantryAddUser : Button? = null
    private var createPantryAdmins : RecyclerView? = null
    private var createPantryUsers : RecyclerView? = null
    private var adminEmails : ArrayList<EmailListData>? = null
    private var userEmails : ArrayList<EmailListData>? = null
    private var create : Boolean = true
    val GET_USER : Int = 1
    val GET_ADMIN : Int = 2
    val EDIT_USER : Int = 3
    val EDIT_ADMIN : Int = 4
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        createPantry = inflater.inflate(R.layout.createpantry, container, false)
        createPantryName = createPantry?.findViewById(R.id.createPantryName) as EditText
        pantryNameError = createPantry?.findViewById(R.id.pantryNameError) as TextView

        pantryNameError?.visibility = View.GONE
        createPantryButton = createPantry?.findViewById(R.id.createPantryButton) as Button
        createPantryButton?.setOnClickListener(this)
        createPantryAddAdministrator = createPantry?.findViewById(R.id.createPantryAddAdministrator)
        createPantryAddAdministrator?.setOnClickListener(this)
        createPantryAddUser = createPantry?.findViewById(R.id.createPantryAddUser)
        createPantryAddUser?.setOnClickListener(this)
        adminEmails = ArrayList()
        userEmails = ArrayList()
        if(!create) {
            createPantryButton?.text = "Update Pantry"
            createPantryName?.setText(arguments?.getString("name") as String)
            var users = arguments?.getStringArrayList("users")
            var admins = arguments?.getStringArrayList("administrators")
            for(i in 0 until users?.size as Int)
                userEmails?.add(EmailListData(users[i], ""))
            for(i in 0 until admins?.size as Int)
                adminEmails?.add(EmailListData(admins[i], ""))
        }
        initRecyclerViews()
        return createPantry
    }
    private fun initRecyclerViews(){
        createPantryAdmins = createPantry?.findViewById(R.id.createPantryAdmins)
        createPantryUsers = createPantry?.findViewById(R.id.createPantryUsers)
        var dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(context as Context, R.drawable.divider) as Drawable
            )
        createPantryAdmins?.adapter = EmailListAdapter(adminEmails, "Edit Administrator", this, null)
        createPantryUsers?.adapter = EmailListAdapter(userEmails, "Edit User", this, null)
        createPantryAdmins?.layoutManager = LinearLayoutManager(activity)
        createPantryUsers?.layoutManager = LinearLayoutManager(activity)
        createPantryUsers?.isNestedScrollingEnabled = false
        createPantryAdmins?.isNestedScrollingEnabled = false
        createPantryUsers?.addItemDecoration(dividerItemDecoration)
        createPantryAdmins?.addItemDecoration(dividerItemDecoration)
        val swipeHandler1 = object : SwipeToDeleteCallback(activity?.applicationContext as Context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = createPantryAdmins?.adapter as EmailListAdapter

                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val swipeHandler2 = object : SwipeToDeleteCallback(activity?.applicationContext as Context) {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter = createPantryUsers?.adapter as EmailListAdapter
                adapter.removeAt(viewHolder.adapterPosition)
            }
        }
        val itemTouchHelper1 = ItemTouchHelper(swipeHandler1)
        val itemTouchHelper2 = ItemTouchHelper(swipeHandler2)
        itemTouchHelper1.attachToRecyclerView(createPantryAdmins)
        itemTouchHelper2.attachToRecyclerView(createPantryUsers)
    }
    override fun onResume(){
        super.onResume()
        if(create)
            (activity as AppCompatActivity)?.supportActionBar?.title = "Create Pantry"
        else
            (activity as AppCompatActivity)?.supportActionBar?.title = "Edit Pantry"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        if(arguments != null)
            create = arguments?.getBoolean("create", true) as Boolean
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        if(create) {
            menu?.findItem(R.id.app_bar_create_pantry).isVisible = false
            menu?.findItem(R.id.app_bar_sign_out).isVisible = false
            menu?.findItem(R.id.app_bar_user_info).isVisible = false
        } else {
            menu?.findItem(R.id.app_bar_pantry_info).isVisible = false
            menu?.findItem(R.id.app_bar_add_item).isVisible = false
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(create)
            (activity as AppCompatActivity)?.supportActionBar?.title = "Create Pantry"
        else
            (activity as AppCompatActivity)?.supportActionBar?.title = "Edit Pantry"
    }
    override fun onClick(view: View?) {
        listErrors(adminEmails, null, createPantryAdmins)
        listErrors(userEmails, null, createPantryUsers)
        pantryNameError?.visibility = View.GONE
        when(view?.id){
            R.id.createPantryAddUser -> {
                var b = Bundle()
                b.putString("mode", "Add User")
                b.putString("description", "Users can add, remove items or edit items.")
                var addUserDialog = AddUserorAdmin()
                addUserDialog.arguments = b
                addUserDialog.setTargetFragment(this, GET_USER)
                addUserDialog.show(fragmentManager as FragmentManager, "Add User")
            }
            R.id.createPantryAddAdministrator -> {
                var b = Bundle()
                b.putString("mode", "Add Administrator")
                b.putString("description", "Administrators can add or remove users as well as other administrators and can add, remove items or edit items.")
                var addAdminDialog = AddUserorAdmin()
                addAdminDialog.arguments = b
                addAdminDialog.setTargetFragment(this, GET_ADMIN)
                addAdminDialog.show(fragmentManager as FragmentManager, "Add Administrator")
            }
            R.id.createPantryButton -> {
                var aEmails = ArrayList<String>()
                var uEmails = ArrayList<String>()
                for(admin in adminEmails.orEmpty())
                    aEmails.add(admin?.email as String)
                for(user in userEmails.orEmpty())
                    uEmails.add(user?.email as String)
                var pantry = Pantry(
                    createPantryName?.text.toString(),
                    aEmails,
                    uEmails,
                    null
                )
                CoroutineScope(Dispatchers.IO).async{
                    connect(pantry)
                }
            }
        }
    }

    private fun connect(pantry : Pantry){
        var api = PantriesSignedInActivity.retrofit?.create(NetworkConnection::class.java)
        var call : Call<ResponseStatus>? = null
        if(create)
            call = api?.createPantry(pantry)
        else
            call = api?.updatePantry(pantry)
        call?.enqueue(object : Callback<ResponseStatus> {
            override fun onResponse(call: Call<ResponseStatus>, response: Response<ResponseStatus>) {
                var result = response.body()
                if(result?.success == true) {
                    Toast.makeText(context, result?.message, Toast.LENGTH_SHORT).show()
                    targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, null)
                    fragmentManager?.popBackStack()
                }
                else
                    insertErrors(result)
            }
            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                println("onFailure was called.")
                t.printStackTrace()
            }
        })
    }
    private fun insertErrors(result : ResponseStatus?){
        if(result?.group_name != null){
            pantryNameError?.text = result?.group_name
            pantryNameError?.visibility = View.VISIBLE
        }
        if(result?.admins != null)
            listErrors(adminEmails, result?.admins, createPantryAdmins)
        if(result?.users != null)
            listErrors(userEmails, result?.users, createPantryUsers)
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
                userEmails?.add(EmailListData(data?.getStringExtra("email") as String, ""))
                createPantryUsers?.adapter?.notifyItemInserted(userEmails?.size as Int - 1)
                if(userEmails?.size as Int > 1)
                    createPantryUsers?.adapter?.notifyItemChanged(userEmails?.size as Int - 1)
            }
            GET_ADMIN -> {
                adminEmails?.add(EmailListData(data?.getStringExtra("email") as String, ""))
                createPantryAdmins?.adapter?.notifyItemInserted(adminEmails?.size as Int -1)
            }
            EDIT_USER -> {
                userEmails?.get(index)?.email = data?.getStringExtra("email")
                createPantryUsers?.adapter?.notifyItemChanged(index)
            }
            EDIT_ADMIN -> {
                adminEmails?.get(index)?.email = data?.getStringExtra("email")
                createPantryAdmins?.adapter?.notifyItemChanged(index)
            }
        }
    }
}