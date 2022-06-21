package com.rootscare.ui.newaddition.providerlisting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.databinding.ItemAddPatientBinding
import com.rootscare.ui.newaddition.providerlisting.models.ModelPatientFamilyMembers
import com.rootscare.utilitycommon.setRemoteProfileImage
import java.util.ArrayList

interface OnFamiliyMemberCallback{
    fun onItemClick(resultItem: ModelPatientFamilyMembers.Result?)
//    fun onEditButtonClick(modelOfGetAddPatientList: ResultItem)
    fun onDeleteButtonClick(id: String,name:String)
}

class AdapterFamilyMembers :
    ListAdapter<ModelPatientFamilyMembers.Result, AdapterFamilyMembers.ViewHolder>(FamilyMemberDiffUtil()) {

    var mCallback: OnFamiliyMemberCallback? = null
    val updatedArrayList : MutableList<ModelPatientFamilyMembers.Result?> = ArrayList()
    var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<ItemAddPatientBinding>(LayoutInflater.from(parent.context),
            R.layout.item_add_patient, parent, false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(getItem(position))
    }

    fun updateStatus(pos: Int, checkStatus: Boolean) {
        selectedPosition = pos
     //   updatedArrayList.forEach { it?.isChecked = false }
     //   updatedArrayList[pos]?.isChecked = checkStatus
      //  submitList(ArrayList(updatedArrayList))
        notifyItemRangeChanged(0, itemCount)
    }

    fun updatedData(node: ArrayList<ModelPatientFamilyMembers.Result?>?) {
        node?.let {
            selectedPosition = -1
            updatedArrayList.clear()
            updatedArrayList.addAll(node)
            submitList(ArrayList(updatedArrayList))
        }
    }

    fun loadDataIntoList(list: ArrayList<ModelPatientFamilyMembers.Result?>?) {
        list?.let { updatedArrayList.addAll(it.toMutableList()) }
        submitList(updatedArrayList)
    }

    inner class ViewHolder(val binding: ItemAddPatientBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.llAddPatient.setOnClickListener {
              updateStatus(absoluteAdapterPosition, true)
              mCallback?.onItemClick(getItem(absoluteAdapterPosition))
           }
           binding.imgRemove.setOnClickListener {
           mCallback?.onDeleteButtonClick(getItem(absoluteAdapterPosition)?.id ?: "",
               getItem(absoluteAdapterPosition)?.first_name+" "+ getItem(absoluteAdapterPosition)?.last_name)
          }
     }

        fun onBindView(item: ModelPatientFamilyMembers.Result?) {
            binding.run {
            txtPatientFamilymemberName.text = item?.first_name ?:""

                llAddPatient.setBackgroundResource(if(selectedPosition == absoluteAdapterPosition) {
                    R.drawable.sq_back_patient_selected } else 0)
             imgPatientFamilymemberPhoto.setRemoteProfileImage(item?.image)
          }
       }

    }


}

class FamilyMemberDiffUtil : DiffUtil.ItemCallback<ModelPatientFamilyMembers.Result>() {
    override fun areItemsTheSame(oldItem: ModelPatientFamilyMembers.Result, newItem: ModelPatientFamilyMembers.Result): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ModelPatientFamilyMembers.Result, newItem: ModelPatientFamilyMembers.Result): Boolean {
        return oldItem == newItem
    }

}