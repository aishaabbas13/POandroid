package com.cuseniordesign909.vpantry.items_signed_in_user_interface
import com.cuseniordesign909.vpantry.pantries_signed_in_user_interface.EmailListAdapter
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuseniordesign909.vpantry.ItemsSignedInActivity
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.data_representations.EmailListData
import com.cuseniordesign909.vpantry.data_representations.Pantry
import com.cuseniordesign909.vpantry.data_representations.ResponseStatus
import com.cuseniordesign909.vpantry.pantries_signed_in_user_interface.CreatePantry
import com.cuseniordesign909.vpantry.primary_operations.NetworkConnection
import com.cuseniordesign909.vpantry.user_interface_features.DividerItemDecorator
import kotlinx.android.synthetic.main.signed_in.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PantryInfo : Fragment(), View.OnClickListener {
    private var pantryInfo : View? = null
    private var pantryInfoName : TextView? = null
    private var pantryInfoEdit : Button? = null
    private var pantryInfoAdmins : RecyclerView? = null
    private var pantryInfoUsers : RecyclerView? = null
    private var adminEmails : ArrayList<EmailListData>? = null
    private var userEmails : ArrayList<EmailListData>? = null
    private var _id : String? = null
    private var pantry : Pantry? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        pantryInfo = inflater.inflate(R.layout.pantryinfo, container, false)
        pantryInfoName = pantryInfo?.findViewById(R.id.pantryInfoName)
        pantryInfoEdit = pantryInfo?.findViewById(R.id.pantryInfoEdit)
        if(arguments?.getBoolean("admin") as Boolean) {
            pantryInfoEdit?.setOnClickListener(this)
        } else {
            pantryInfoEdit?.visibility = View.GONE
        }
        initRecyclerViews()
        CoroutineScope(Dispatchers.IO).async {
            connect()
        }
        return pantryInfo
    }
    private fun initRecyclerViews(){
        pantryInfoAdmins = pantryInfo?.findViewById(R.id.pantryInfoAdmins)
        pantryInfoUsers = pantryInfo?.findViewById(R.id.pantryInfoUsers)
        adminEmails = ArrayList()
        userEmails = ArrayList()
        var dividerItemDecoration: RecyclerView.ItemDecoration =
            DividerItemDecorator(
                ContextCompat.getDrawable(context as Context, R.drawable.divider) as Drawable
            )
        pantryInfoAdmins?.adapter = EmailListAdapter(adminEmails, "", null, null)
        pantryInfoUsers?.adapter = EmailListAdapter(userEmails, "", null, null)
        pantryInfoAdmins?.layoutManager = LinearLayoutManager(activity)
        pantryInfoUsers?.layoutManager = LinearLayoutManager(activity)
        pantryInfoUsers?.isNestedScrollingEnabled = false
        pantryInfoAdmins?.isNestedScrollingEnabled = false
        pantryInfoUsers?.addItemDecoration(dividerItemDecoration)
        pantryInfoAdmins?.addItemDecoration(dividerItemDecoration)
    }
    override fun onResume(){
        super.onResume()
        CoroutineScope(Dispatchers.IO).async {
            connect()
        }
        (activity as AppCompatActivity)?.supportActionBar?.title = "Pantry Info"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        _id = arguments?.getString("_id")
    }
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu?.findItem(R.id.app_bar_pantry_info).isVisible = false
        menu?.findItem(R.id.app_bar_add_item).isVisible = false
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity)?.supportActionBar?.title = "Pantry Info"
    }
    override fun onClick(view: View?) {
        when(view?.id) {
            R.id.pantryInfoEdit -> {
                var b = Bundle()
                b?.putString("_id", pantry?._id)
                b?.putStringArrayList("users", arguments?.getStringArrayList("users"))
                b?.putStringArrayList("administrators", arguments?.getStringArrayList("administrators"))
                var editPantry = UpdatePantryInfo()
                editPantry?.arguments = b
                activity?.supportFragmentManager?.beginTransaction()
                    ?.add(R.id.signedInFragmentManager, editPantry, "Edit Pantry")
                    ?.addToBackStack("Edit Pantry")?.commit()
            }
        }
    }

    private fun connect(){
        var api = ItemsSignedInActivity.retrofit?.create(NetworkConnection::class.java)
        var call = api?.getPantry(_id as String)
        call?.enqueue(object : Callback<ResponseStatus> {
            override fun onResponse(
                call: Call<ResponseStatus>,
                response: Response<ResponseStatus>
            ) {
                var status = response.body()
                if(status?.success == true) {
                    pantry = status.pantry
                    pantryInfoName?.text = pantry?.name
                    adminEmails = ArrayList()
                    userEmails = ArrayList()
                    updateList(adminEmails, pantry?.administrators)
                    updateList(userEmails, pantry?.users)
                    var adminAdapter = EmailListAdapter(adminEmails, null, null, null)
                    pantryInfoAdmins?.adapter = adminAdapter
                    pantryInfoAdmins?.adapter?.notifyDataSetChanged()
                    var userAdapter = EmailListAdapter(userEmails, null, null, null)
                    pantryInfoUsers?.adapter = userAdapter
                    pantryInfoUsers?.adapter?.notifyDataSetChanged()
                } else
                    Toast.makeText(context, status?.message, Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(call: Call<ResponseStatus>, t: Throwable) {
                println("onFailure was called.")
                t.printStackTrace()
            }
        })
    }
    private fun updateList(list : ArrayList<EmailListData>?, emails : ArrayList<String>?){
        for(i in 0 until emails?.size as Int)
            list?.add(EmailListData(emails!![i], ""))
    }
}