package com.cuseniordesign909.vpantry.user_interface

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cuseniordesign909.vpantry.R
import com.cuseniordesign909.vpantry.data_representations.Pantry

class PantryList() : Fragment(){
    private var pantryList : View? = null
    var listOfPantries : RecyclerView? = null
    var pantries : ArrayList<Pantry>? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        pantryList = inflater.inflate(R.layout.pantrylist, container, false)
        listOfPantries = pantryList?.findViewById(R.id.listOfPantries) as RecyclerView?
        pantries = ArrayList()
        pantries?.add(Pantry("Home kitchen", "aasdfjsdf", "a;sdkfjasdlfj"))
        pantries?.add(Pantry("Summer Home kitchen", "asdlfjasdf", "laksdhf"))
        var adapter =  PantryListItemAdapter(pantries)
        listOfPantries?.adapter = adapter
        listOfPantries?.layoutManager = LinearLayoutManager(activity)
        return pantryList
    }
}
