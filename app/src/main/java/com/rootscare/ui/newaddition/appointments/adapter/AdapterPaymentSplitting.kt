package com.rootscare.ui.newaddition.appointments.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.databinding.LayoutNewItemPaymentSplittingBinding
import com.rootscare.ui.newaddition.appointments.ModelAppointmentDetails
import com.rootscare.utilitycommon.setAmount
import com.rootscare.utilitycommon.setAmountWithCurrency
import java.util.ArrayList


class AdapterPaymentSplitting :
    ListAdapter<ModelAppointmentDetails.Result.TaskDetail, AdapterPaymentSplitting.ViewHolder>(PaymentSplittingDiffUtil()) {

    val updatedArrayList = ArrayList<ModelAppointmentDetails.Result.TaskDetail?>()
    var mCurrency: String? = null

   override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<LayoutNewItemPaymentSplittingBinding>(LayoutInflater.from(parent.context),
            R.layout.layout_new_item_payment_splitting, parent, false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
         holder.onBindView(getItem(position))
    }

    fun loadDataIntoList(node: ArrayList<ModelAppointmentDetails.Result.TaskDetail?>?) {
        node?.let {
            updatedArrayList.addAll(node)
            submitList(updatedArrayList)
        }
    }

    fun updatedData(node: ArrayList<ModelAppointmentDetails.Result.TaskDetail?>?) {
        node?.let {
            updatedArrayList.clear()
            updatedArrayList.addAll(node)
            submitList(ArrayList(updatedArrayList))
        }
    }

    fun updatedNode(node: ModelAppointmentDetails.Result.TaskDetail?) {
        node?.let {
            updatedArrayList.clear()
            updatedArrayList.add(node)
            submitList(ArrayList(updatedArrayList))
        }
    }

    fun addNode(node: ModelAppointmentDetails.Result.TaskDetail?) {
        val mFind = updatedArrayList.find { node?.id.equals(it?.id, ignoreCase = true)  }
          if(mFind == null) {
              updatedArrayList.add(node)
              submitList(ArrayList(updatedArrayList))
          }
    }

    fun removeById(mIde: String) {
        val mFind = updatedArrayList.find { mIde.equals(it?.id, ignoreCase = true)  }
        mFind?.let {
           updatedArrayList.remove(it)
           submitList(ArrayList(updatedArrayList))
       }

    }

    inner class ViewHolder(val binding: LayoutNewItemPaymentSplittingBinding) : RecyclerView.ViewHolder(binding.root) {

        fun onBindView(item: ModelAppointmentDetails.Result.TaskDetail?) {
            binding.tvHeading.text = item?.name ?:"Unknown"

            if(mCurrency.isNullOrBlank().not()){
                binding.tvPrice.setAmountWithCurrency(item?.price ?: "0", mCurrency)
            }else binding.tvPrice.setAmount(item?.price ?: "0" )
        }

    }

}

class PaymentSplittingDiffUtil : DiffUtil.ItemCallback<ModelAppointmentDetails.Result.TaskDetail>() {
    override fun areItemsTheSame(oldItem: ModelAppointmentDetails.Result.TaskDetail, newItem: ModelAppointmentDetails.Result.TaskDetail): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ModelAppointmentDetails.Result.TaskDetail, newItem: ModelAppointmentDetails.Result.TaskDetail): Boolean {
        return oldItem == newItem

    }

}