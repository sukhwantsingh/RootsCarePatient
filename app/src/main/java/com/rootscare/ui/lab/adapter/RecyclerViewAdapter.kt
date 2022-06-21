/*
package com.rootscare.ui.lab.Adapter

import android.content.Context
import android.util.SparseBooleanArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.ui.lab.Adapter.RecyclerViewAdapter.RecyclerViewHolder
import com.rootscare.ui.lab.FragmentLabBookingAppointment
import java.util.*

*/
/**
 * Created by sonu on 19/09/16.
 *//*

class RecyclerViewAdapter(
    private val context: Context,
    private val arrayList: ArrayList<String>?,
    adapterCallback: FragmentLabBookingAppointment
) : RecyclerView.Adapter<RecyclerViewHolder?>() {
    class RecyclerViewHolder(view: View) :
        RecyclerView.ViewHolder(view) {
        val label: TextView
        val checkBox: CheckBox

        init {
            label = view.findViewById<View>(R.id.label) as TextView
            checkBox = view.findViewById<View>(R.id.checkbox) as CheckBox
        }
    }

    */
/**
     * Return the selected Checkbox IDs
     *//*

    var selectedIds: SparseBooleanArray

    private val adapterCallback: AdapterCallback
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): RecyclerViewHolder {
        val v: View = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.list_custom_row_layout, viewGroup, false)
        return RecyclerViewHolder(v)
    }

    override fun onBindViewHolder(holder: RecyclerViewHolder, i: Int) {
        holder.label.text = arrayList!![i]
        holder.checkBox.isChecked = selectedIds[i]
        holder.checkBox.setOnClickListener {
            checkCheckBox(i, !selectedIds[i])
            adapterCallback.onMethodCallback()
        }
        holder.label.setOnClickListener { checkCheckBox(i, !selectedIds[i]) }
    }

    private val itemCount: Int = arrayList?.size ?: 0
    override fun getItemCount(): Int {
        return arrayList!!.size
    }

    */
/**
     * Remove all checkbox Selection
     *//*

    fun removeSelection() {
        selectedIds = SparseBooleanArray()
        notifyDataSetChanged()
    }


    */
/**
     * Check the Checkbox if not checked
     *//*

    fun checkCheckBox(position: Int, value: Boolean) {
        if (value) selectedIds.put(position, true) else selectedIds.delete(position)
        notifyDataSetChanged()
    }

    interface AdapterCallback {
        fun onMethodCallback()
    }

    init {
        selectedIds = SparseBooleanArray()
        this.adapterCallback = adapterCallback
    }

    companion object {
        fun getSelectedIds(recyclerViewAdapter: RecyclerViewAdapter): SparseBooleanArray? {
            return recyclerViewAdapter.selectedIds
        }
    }


}*/
