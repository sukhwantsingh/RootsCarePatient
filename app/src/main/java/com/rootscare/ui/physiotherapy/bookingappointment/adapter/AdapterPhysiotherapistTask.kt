package com.rootscare.ui.physiotherapy.bookingappointment.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.data.model.api.response.nurses.taskresponse.ResultItem
import com.rootscare.ui.physiotherapy.bookingappointment.FragmentPhysiotherapyBookingAppointment
import java.util.*
import kotlin.collections.ArrayList


class AdapterPhysiotherapistTask(
    val nurseTimingList: ArrayList<ResultItem?>?,
    var context: FragmentActivity?,
    var adapterCallback: FragmentPhysiotherapyBookingAppointment

) : RecyclerView.Adapter<AdapterPhysiotherapistTask.ViewHolder?>(), Filterable {
    private var savedListFiltered: ArrayList<ResultItem?>? =
        null

    init {
        savedListFiltered = nurseTimingList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //inflate the layout file
        val groceryProductView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.singlenursetask, parent, false)
        return ViewHolder(groceryProductView)
    }


    private val itemCount: Int = nurseTimingList?.size ?: 0

    override fun getItemCount(): Int {
        // return nurseTimingList!!.size
        return savedListFiltered?.size ?: 0
    }

    inner class ViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {

        // var cut: TextView
        var subtitle: TextView = view.findViewById(R.id.label)
        var time: TextView? = null
        var rate: TextView = view.findViewById(R.id.label_rate)
        var check: CheckBox? = null


        init {
            //    cut = view.findViewById(R.id.cut)
            check = view.findViewById(R.id.checkbox)
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.subtitle.text = savedListFiltered?.get(position)?.name
        holder.rate.text = savedListFiltered?.get(position)?.price.toString()
        holder.check?.isChecked = savedListFiltered?.get(position)?.isCheck ?: false
        holder.check?.setOnClickListener {
            savedListFiltered?.get(position)?.isCheck = holder.check?.isChecked ?: false
            nurseTimingList?.filter { it?.id == savedListFiltered?.get(position)?.id }
                ?.map { it?.isCheck = holder.check?.isChecked ?: false }
            if (holder.check?.isChecked == true) {
                Log.d("TAG", "onBindViewHolder: " + savedListFiltered?.get(position)?.id)
                adapterCallback.onMethodCallback(
                    position, savedListFiltered?.get(position)?.id,
                    savedListFiltered?.get(position)?.price, isCheck = true
                )
            } else {
                Log.d("TAG", "uncheck: ")
                adapterCallback.onMethodCallback(
                    position, savedListFiltered?.get(position)?.id,
                    savedListFiltered?.get(position)?.price, isCheck = false
                )
            }
        }
        /* holder.check?.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
             nurseTimingList?.get(position)?.isCheck=isChecked
             if (isChecked) {
                 Log.d("TAG", "onBindViewHolder: "+nurseTimingList?.get(position)?.id)
                 adapterCallback.onMethodCallback(nurseTimingList?.get(position)?.id,nurseTimingList?.get(position)?.price)
             } else {
                 Log.d("TAG", "uncheck: ")
                 adapterCallback.onMethodCallbackUncheck(nurseTimingList?.get(position)?.id,nurseTimingList?.get(position)?.price)
             }
         })*/
    }

    interface AdapterCallback {
        fun onMethodCallback(position: Int?, id: Int?, price: Int?, isCheck: Boolean?)
//        fun onMethodCallbackUncheck(id: Int?, price: Int?)
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    savedListFiltered = nurseTimingList
                } else {
                    val filteredList: ArrayList<ResultItem?> =
                        ArrayList()
                    for (row in nurseTimingList!!) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row?.name?.lowercase(Locale.ENGLISH)?.contains(
                                charString.lowercase(
                                    Locale.ENGLISH
                                )
                            ) == true
                        ) {
                            filteredList.add(row)
                        }
                    }
                    savedListFiltered = filteredList
                }
                val filterResults = FilterResults()
                filterResults.values = savedListFiltered
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
                savedListFiltered =
                    filterResults.values as ArrayList<ResultItem?>
                notifyDataSetChanged()
            }
        }
    }


}