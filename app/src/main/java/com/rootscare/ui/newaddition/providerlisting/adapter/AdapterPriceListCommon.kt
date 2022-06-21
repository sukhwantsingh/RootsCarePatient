package com.rootscare.ui.newaddition.providerlisting.adapter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.databinding.LayoutNewItemPriceWithTasksBinding

import com.rootscare.ui.newaddition.providerlisting.models.ModelTaskListWithPrice
import java.util.ArrayList
import com.google.android.gms.common.data.DataHolder
import com.rootscare.utilitycommon.setAmount
import com.rootscare.utilitycommon.setAmountWithCurrency


interface OnTaskPriceCallback{
    fun onTaskClick(node: ModelTaskListWithPrice.Result?)
}

class AdapterPriceListCommon :
    ListAdapter<ModelTaskListWithPrice.Result, AdapterPriceListCommon.ViewHolder>(PriceListDiffUtil()) {


    var mCallback: OnTaskPriceCallback? = null
    val updatedArrayList : MutableList<ModelTaskListWithPrice.Result?> = ArrayList()
    var mCurrency: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<LayoutNewItemPriceWithTasksBinding>(LayoutInflater.from(parent.context),
            R.layout.layout_new_item_price_with_tasks, parent, false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(getItem(position))
    }

    override fun submitList(list: MutableList<ModelTaskListWithPrice.Result?>?) {
        super.submitList(if(list.isNullOrEmpty()) null else ArrayList(list))
    }
    fun updateStatus(node: ModelTaskListWithPrice.Result?, checkStatus: Boolean) {
        node?.let { nod ->
            updatedArrayList.forEach { inLst ->
                if (nod.id.equals(inLst?.id, ignoreCase = true)) {
                    inLst?.isChecked = checkStatus
                }
            }
            submitList(updatedArrayList)
        }
    }
   fun unCheckTaskById(node: ModelTaskListWithPrice.Result?) {
       node?.let { nod ->
              updatedArrayList.forEach { inLst->
              if(nod.id.equals(inLst?.id, ignoreCase = true)) { inLst?.isChecked = false }
          }
           submitList(updatedArrayList)
           notifyItemRangeChanged(0,itemCount)
       }

    }

    fun loadDataIntoList(list: ArrayList<ModelTaskListWithPrice.Result?>?) {
        list?.let { updatedArrayList.addAll(it.toMutableList()) }
        submitList(updatedArrayList)
    }

    fun updateList(list: ArrayList<ModelTaskListWithPrice.Result?>?) {
        list?.let {
         submitList(ArrayList(list))
        }
    }

    inner class ViewHolder(val binding: LayoutNewItemPriceWithTasksBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.cbTask.setOnClickListener {
                updateStatus(getItem(absoluteAdapterPosition), binding.cbTask.isChecked)
                mCallback?.onTaskClick(getItem(absoluteAdapterPosition))
            }
        }

        fun onBindView(item: ModelTaskListWithPrice.Result?) {
            binding.run {
                cbTask.isChecked = item?.isChecked ?: false
                cbTask.text = item?.name ?:""

                if(mCurrency.isNullOrBlank().not()){
                    labelRate.setAmountWithCurrency(item?.price ?: "0", mCurrency)
                } else labelRate.setAmount(item?.price ?: "0" )
            }
        }

    }


}

class PriceListDiffUtil : DiffUtil.ItemCallback<ModelTaskListWithPrice.Result>() {
    override fun areItemsTheSame(oldItem: ModelTaskListWithPrice.Result, newItem: ModelTaskListWithPrice.Result): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: ModelTaskListWithPrice.Result, newItem: ModelTaskListWithPrice.Result): Boolean {
        return oldItem == newItem
    }

}