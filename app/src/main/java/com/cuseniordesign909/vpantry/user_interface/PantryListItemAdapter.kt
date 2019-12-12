package com.cuseniordesign909.vpantry.user_interface

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.data_representations.Pantry
import kotlinx.android.synthetic.main.pantrylistitem.view.*

class PantryListItemAdapter(private val pantries: ArrayList<Pantry>?) : RecyclerView.Adapter<PantryListItemAdapter.PantryHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : PantryHolder {
        //Log.d("RecyclerView", "onCreateViewHolder() was called!")
        return PantryHolder(LayoutInflater.from(parent.context).inflate(R.layout.pantrylistitem, parent, false))
    }

    override fun getItemCount() = pantries!!.size


    override fun onBindViewHolder(holder: PantryHolder, position: Int) {
        //Log.d("RecyclerView", "onBindViewHolder() was called!")
        holder.pantryName.text = pantries?.get(position)?.name
    }
    //1
    class PantryHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        val pantryName = v.pantryName
        init {
            v.setOnClickListener(this)
        }

        //4
        override fun onClick(v: View) {
            //Log.d("RecyclerView", "CLICK!")
        }
    }

}
