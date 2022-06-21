package com.rootscare.ui.newaddition.providerlisting.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.databinding.LayoutNewItemRowTimesBinding
import com.rootscare.ui.newaddition.providerlisting.models.ModelTaskListWithPrice
import java.util.*
import kotlin.collections.ArrayList

class AdapterTimeSlotList :
    ListAdapter<ModelTaskListWithPrice.Result, AdapterTimeSlotList.ViewHolder>(
        ProviderTimeSlotListDiffUtil()
    ) {

    var updatedArrayList: MutableList<ModelTaskListWithPrice.Result?> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<LayoutNewItemRowTimesBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_new_item_row_times, parent, false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(updatedArrayList[position])
    }

    fun updateStatus(pos: Int, checkStatus: Boolean) {
        updatedArrayList.forEach { it?.isChecked = false }
        updatedArrayList[pos]?.isChecked = checkStatus
        //  submitList(updatedArrayList)
        notifyItemRangeChanged(0, itemCount)
    }

    fun markedSelectedSlot(timeSlot: String) {
        updatedArrayList.forEach {
           if(it?.name?.trim().equals(timeSlot,ignoreCase = true)){
               it?.isChecked = true
           }
        }
        notifyItemRangeChanged(0, itemCount)
        //  submitList(updatedArrayList)

    }

    fun loadDataIntoList(list: ArrayList<ModelTaskListWithPrice.Result?>?) {
        list?.let { updatedArrayList.addAll(it.toMutableList()) }
        submitList(ArrayList(updatedArrayList))
    }

    fun updateData(list: ArrayList<ModelTaskListWithPrice.Result?>?) {
        list?.let {
            updatedArrayList = list
        }
        submitList(ArrayList(updatedArrayList))
    }

    inner class ViewHolder(val binding: LayoutNewItemRowTimesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.txtTime.setOnClickListener {
                updateStatus(absoluteAdapterPosition, true)
            }
        }

        fun onBindView(item: ModelTaskListWithPrice.Result?) {
            binding.run {
                txtTime.text = (item?.name ?: "").uppercase()
                txtTime.setBackgroundResource(
                    if (item?.isChecked == true) {
                        txtTime.setTextColor(Color.parseColor("#ffffff"))
                        R.color.indicator_color
                    } else {
                        txtTime.setTextColor(Color.parseColor("#354052"))
                        R.color.price_bg
                    }
                )
            }
        }

    }


}

class ProviderTimeSlotListDiffUtil : DiffUtil.ItemCallback<ModelTaskListWithPrice.Result>() {
    override fun areItemsTheSame(
        oldItem: ModelTaskListWithPrice.Result,
        newItem: ModelTaskListWithPrice.Result
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ModelTaskListWithPrice.Result,
        newItem: ModelTaskListWithPrice.Result
    ): Boolean {
        return oldItem == newItem
    }

}