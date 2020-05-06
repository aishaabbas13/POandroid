package com.cuseniordesign909.vpantry.pantries_signed_in_user_interface

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.data_representations.EmailListData
import com.cuseniordesign909.vpantry.items_signed_in_user_interface.AddUsersAdmins
import kotlinx.android.synthetic.main.emailitem.view.*

class EmailListAdapter(private var emails : ArrayList<EmailListData>?, _mode : String?, _fragment : CreatePantry?, _fragment2:AddUsersAdmins?) : RecyclerView.Adapter<EmailListAdapter.EmailHolder>(){
    var mode = _mode
    var fragment = _fragment
    var fragment2 = _fragment2
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EmailHolder {
        return EmailHolder(LayoutInflater.from(parent.context).inflate(R.layout.emailitem, parent, false), this, mode, fragment, fragment2)
    }
    override fun onBindViewHolder(holder: EmailHolder, position: Int) {
        holder.emailListEmail.text = emails!![position].email
        if(emails!![position].error != "") {
            holder.emailListEmail.setPadding(0, 20, 0, 5)
            holder.userDoesNotExistError.visibility = View.VISIBLE
            holder.userDoesNotExistError.text = emails!![position].error
        }
        else {
            holder.emailListEmail.setPadding(0, 20, 0, 20)
            holder.userDoesNotExistError.visibility = View.GONE
        }
    }
    class EmailHolder(view : View, _adapter:EmailListAdapter, _mode : String?, _fragment : CreatePantry?, _fragment2:AddUsersAdmins?) : RecyclerView.ViewHolder(view), View.OnClickListener {
        var fragment = _fragment
        var fragment2 = _fragment2
        var adapter = _adapter
        var mode = _mode
        var emailListEmail = view.emailListEmail as TextView
        var userDoesNotExistError = view.userDoesNotExistError as TextView
        init{
            if(mode != null)
                view.setOnClickListener(this)
        }
        override fun onClick(v: View?) {
            var b = Bundle()
            b.putString("mode", mode)
            var editEmail = AddUserorAdmin()
            if(mode == "Edit User") {
                b.putString("description", "Users can add, remove items or edit items.")
                if(fragment != null)
                    editEmail.setTargetFragment(fragment, fragment?.EDIT_USER as Int)
                if(fragment2 != null)
                    editEmail.setTargetFragment(fragment2, fragment2?.EDIT_USER as Int)
            } else if(mode == "Edit Administrator"){
                b.putString("description", "Administrators can add or remove users as well as other administrators and can add, remove items or edit items.")
                if(fragment != null)
                    editEmail.setTargetFragment(fragment, fragment?.EDIT_ADMIN as Int)
                if(fragment2 != null)
                    editEmail.setTargetFragment(fragment2, fragment2?.EDIT_ADMIN as Int)
            }
            b.putInt("index", adapterPosition)
            b.putString("email", adapter?.emails?.get(adapterPosition)?.email)
            editEmail.arguments = b
            editEmail.show(fragment?.fragmentManager as FragmentManager, "Add Administrator")
        }
    }
    fun removeAt(pos : Int){
        emails?.removeAt(pos)
        notifyItemRemoved(pos)
    }
    override fun getItemCount() = emails?.size as Int
}