/*
package com.rootscare.ui.lab.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.ui.lab.Adapter.AdapterExtra.GroceryViewHolder
import com.rootscare.ui.lab.FragmentLabBookingAppointment
import java.util.*

class AdapterExtra(
    var horizontalGrocderyList: ArrayList<HashMap<String, String>>?,
    var context: FragmentActivity?,
    var adapterCallback: FragmentLabBookingAppointment
) : RecyclerView.Adapter<GroceryViewHolder?>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GroceryViewHolder {
        //inflate the layout file
        val groceryProductView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return GroceryViewHolder(groceryProductView)
    }

    override fun onBindViewHolder(holder: GroceryViewHolder, position: Int) {
        holder.subtitle.text = horizontalGrocderyList?.get(position)?.get("id")

        holder.cut.setOnClickListener { // adapterCallback.onMethodCallbackNew();
            horizontalGrocderyList!!.removeAt(position)["id"]
            notifyDataSetChanged()
        }


    }

    private val itemCount: Int = horizontalGrocderyList?.size ?: 0

    override fun getItemCount(): Int {
        return horizontalGrocderyList!!.size
    }

    inner class GroceryViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        var cut: TextView
        var subtitle: TextView
        var time: TextView? = null


        init {
            subtitle = view.findViewById(R.id.txt)
            cut = view.findViewById(R.id.cut)
        }
    }


}*/
