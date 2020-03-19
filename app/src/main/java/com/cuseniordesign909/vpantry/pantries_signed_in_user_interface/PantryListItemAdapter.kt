package com.cuseniordesign909.vpantry.pantries_signed_in_user_interface

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.cuseniordesign909.vpantry.ItemsSignedInActivity
import com.cuseniordesign909.vpantry.PantriesSignedInActivity
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.SignedOutActivity
import com.cuseniordesign909.vpantry.data_representations.Pantry
import kotlinx.android.synthetic.main.pantrylistitem.view.*

class PantryListItemAdapter(val pantries: ArrayList<Pantry>?) : RecyclerView.Adapter<PantryListItemAdapter.PantryHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : PantryHolder {
        //Log.d("RecyclerView", "onCreateViewHolder() was called!")
        return PantryHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.pantrylistitem, parent, false),
            this
        )
    }

    override fun getItemCount() = pantries?.size as Int


    override fun onBindViewHolder(holder: PantryHolder, position: Int) {
        //Log.d("RecyclerView", "onBindViewHolder() was called!")
        holder.pantryName.text = pantries?.get(position)?.name
        holder.bind(pantries?.get(position))
    }
    //1
    class PantryHolder(v: View, _adapter : PantryListItemAdapter) : RecyclerView.ViewHolder(v), View.OnClickListener {
        var adapter = _adapter
        val pantryName = v.pantryName
        init {
            v.setOnClickListener(this)
        }
        fun bind(pantry: Pantry?){

        }
        //4
        override fun onClick(v: View) {
            var pantry = adapter?.pantries?.get(adapterPosition)
            println("creationDate = " + pantry?.creationDate.toString())
            var intent = Intent(v?.context, ItemsSignedInActivity::class.java)
            intent.putExtra("_id", pantry?._id)
            intent.putExtra("name", pantry?.name)
            intent.putExtra("users", pantry?.users)
            intent.putExtra("administrators", pantry?.administrators)
            intent.putExtra("admin", pantry?.admin)
            v?.context.startActivity(intent)
        }

    }

}
