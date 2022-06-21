package com.rootscare.ui.newaddition.providerlisting.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.databinding.LayoutNewItemRowDateSlotBinding
import com.rootscare.databinding.LayoutNewItemRowTimesBinding
import com.rootscare.ui.newaddition.providerlisting.models.ModelDateSlots
import com.rootscare.ui.newaddition.providerlisting.models.ModelTaskListWithPrice
import java.util.*
import kotlin.collections.ArrayList

interface OnDateSlotCallback{
    fun onDateSlotClicked(node: ModelDateSlots?)
}
class AdapterDateSlotList : ListAdapter<ModelDateSlots, AdapterDateSlotList.ViewHolder>(AdapterDateSlotDiffUtil()) {

    var updatedArrayList: MutableList<ModelDateSlots?> = ArrayList()

    var mCallback: OnDateSlotCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<LayoutNewItemRowDateSlotBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_new_item_row_date_slot, parent, false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(updatedArrayList[position])
    }

    fun updateStatus(pos: Int, checkStatus: Boolean) {
        updatedArrayList.forEach { it?.isSelected_ = false }
        updatedArrayList[pos]?.isSelected_ = checkStatus
        //  submitList(updatedArrayList)
        notifyItemRangeChanged(0, itemCount)
    }

    fun markedSelectedSlot(timeSlot: String) {
        updatedArrayList.forEach {
           if(it?.dayNdate?.trim().equals(timeSlot,ignoreCase = true)){
               it?.isSelected_ = true
           }
        }
        notifyItemRangeChanged(0, itemCount)
        //  submitList(updatedArrayList)

    }

    fun loadDataIntoList(list: ArrayList<ModelDateSlots?>?) {
        list?.let { updatedArrayList.addAll(it.toMutableList()) }
        submitList(ArrayList(updatedArrayList))
    }

    fun updateData(list: ArrayList<ModelDateSlots?>?) {
        list?.let {
            updatedArrayList = list
        }
        submitList(ArrayList(updatedArrayList))
    }

    inner class ViewHolder(val binding: LayoutNewItemRowDateSlotBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.txtDate.setOnClickListener {
                updateStatus(absoluteAdapterPosition, true)
                mCallback?.onDateSlotClicked(getItem(absoluteAdapterPosition))
            }
        }

        fun onBindView(item: ModelDateSlots?) {
            binding.run {
                txtDate.text =item?.dayNdate ?: ""
                txtDate.setBackgroundResource(
                    if (item?.isSelected_ == true) {
                        txtDate.setTextColor(Color.parseColor("#ffffff"))
                        R.color.indicator_color
                    } else {
                        txtDate.setTextColor(Color.parseColor("#354052"))
                        R.color.price_bg
                    }
                )
            }
        }

    }


}

class AdapterDateSlotDiffUtil : DiffUtil.ItemCallback<ModelDateSlots>() {
    override fun areItemsTheSame(oldItem: ModelDateSlots,newItem: ModelDateSlots): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ModelDateSlots,  newItem: ModelDateSlots): Boolean {
        return oldItem == newItem
    }

}