package com.rootscare.ui.newaddition.providerlisting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.databinding.LayoutNewItemPriceWithTasksBinding
import com.rootscare.databinding.LayoutNewItemRowHourSlotsBinding
import com.rootscare.ui.newaddition.providerlisting.models.ModelTaskListWithPrice
import com.rootscare.utilitycommon.setAmount
import com.rootscare.utilitycommon.setAmountWithCurrency
import java.util.ArrayList

interface OnHourlyPriceCallback{
    fun onTaskClick(node: ModelTaskListWithPrice.Result?)
}

class AdapterHourlyPriceList :
    ListAdapter<ModelTaskListWithPrice.Result, AdapterHourlyPriceList.ViewHolder>(HourlyPriceListDiffUtil()) {

    var mCallback: OnHourlyPriceCallback? = null
    val updatedArrayList : MutableList<ModelTaskListWithPrice.Result?> = ArrayList()
    var mCurrency: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<LayoutNewItemRowHourSlotsBinding>(LayoutInflater.from(parent.context),
            R.layout.layout_new_item_row_hour_slots, parent, false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(getItem(position))
    }

    fun updateStatus(pos: Int, checkStatus: Boolean) {
        updatedArrayList.forEach { it?.isChecked = false }
        updatedArrayList[pos]?.isChecked = checkStatus
      //  submitList(ArrayList(updatedArrayList))
        notifyItemRangeChanged(0, itemCount)
    }

    fun loadDataIntoList(list: ArrayList<ModelTaskListWithPrice.Result?>?) {
        list?.let { updatedArrayList.addAll(it.toMutableList()) }
        submitList(updatedArrayList)
    }

    inner class ViewHolder(val binding: LayoutNewItemRowHourSlotsBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.crdRoot.setOnClickListener {
                updateStatus(absoluteAdapterPosition, true)
                mCallback?.onTaskClick(getItem(absoluteAdapterPosition))
           }
        }

        fun onBindView(item: ModelTaskListWithPrice.Result?) {
            binding.run {
            txtHour.text = item?.name ?:""

            if(mCurrency.isNullOrBlank().not()){
                txtPrice.setAmountWithCurrency(item?.price ?: "0", mCurrency)
            } else txtPrice.setAmount(item?.price ?: "0" )

           txtPrice.setBackgroundResource(if(item?.isChecked == true) {
               R.drawable.sq_top_round_bttom_border_blue } else 0)
          }
       }

    }


}

class HourlyPriceListDiffUtil : DiffUtil.ItemCallback<ModelTaskListWithPrice.Result>() {
    override fun areItemsTheSame(oldItem: ModelTaskListWithPrice.Result, newItem: ModelTaskListWithPrice.Result): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ModelTaskListWithPrice.Result, newItem: ModelTaskListWithPrice.Result): Boolean {
        return oldItem == newItem
    }

}