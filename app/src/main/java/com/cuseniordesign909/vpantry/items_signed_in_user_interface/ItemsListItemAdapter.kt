package com.cuseniordesign909.vpantry.items_signed_in_user_interface

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.data_representations.Item
import kotlinx.android.synthetic.main.itemslistitem.view.*
import java.text.SimpleDateFormat

class ItemsListItemAdapter(_fragment : ItemsList, val items: ArrayList<Item>?) : RecyclerView.Adapter<ItemsListItemAdapter.ItemHolder>() {
    var fragment = _fragment
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) : ItemHolder {
        //Log.d("RecyclerView", "onCreateViewHolder() was called!")
        return ItemHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.itemslistitem, parent, false),
            this, fragment
        )
    }

    override fun getItemCount() = items?.size as Int


    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        //Log.d("RecyclerView", "onBindViewHolder() was called!")
        holder.bind(items?.get(position))
    }
    fun removeAt(pos : Int){
        fragment?.deleteItem(items?.get(pos), pos)
    }
    //1
    class ItemHolder(v: View, _adapter : ItemsListItemAdapter, _fragment:ItemsList) : RecyclerView.ViewHolder(v), View.OnClickListener {
        var adapter = _adapter
        var fragment = _fragment
        var itemOwner = v.itemOwner
        val itemName = v.itemName
        var itemType = v.itemType
        var itemQuantity = v.itemQuantity
        var itemExpirationDate = v.itemExpirationDate
        var itemNote = v.itemNote
        init {
            v.setOnClickListener(this)
        }
        fun bind(item: Item?){
            itemOwner.text = item?.owner
            itemName.text = item?.name
            itemType.text = item?.type
            itemQuantity.text = item?.quantity
            itemExpirationDate.text = SimpleDateFormat("MM/dd/yyyy").format(item?.expirationDate)
            itemNote.text = item?.note
        }
        //4
        override fun onClick(v: View) {
            var item = adapter?.items?.get(adapterPosition)
            var addItem = AddItem()
            var bundle = Bundle()
            bundle?.putString("pantryName", fragment.name)
            bundle?.putString("_id", fragment._id)
            bundle.putString("item_id", item?._id)
            bundle.putString("name", item?.name)
            bundle.putString("note", item?.note)
            bundle.putString("type", item?.type)
            bundle.putString("quantity", item?.quantity)
            bundle.putLong("expirationDate", item?.expirationDate as Long)
            bundle.putString("title", "Edit Item")
            addItem.arguments = bundle
            addItem.setTargetFragment(fragment, fragment.UPDATED_ITEM)
            addItem.show((fragment?.activity?.supportFragmentManager as FragmentManager),"Edit Item")
        }

    }

}