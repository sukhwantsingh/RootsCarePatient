package com.rootscare.ui.newaddition.providerlisting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.LayoutNewItemRowAvailableProvidersBinding
import com.rootscare.databinding.RowItemDoctorsUnderHospitalsBinding
import com.rootscare.ui.newaddition.providerlisting.models.ModelProviderDetails
import com.rootscare.ui.newaddition.providerlisting.models.ModelProviderListing
import com.rootscare.ui.newaddition.providerlisting.models.NetworkHospitalListing
import java.util.*


class AdapterDoctorsUnderHospitals(internal var context: Context,private val mCallback: OnProviderHospitalListingCallback) :
    ListAdapter<NetworkHospitalListing.Result.AvailableProvider, AdapterDoctorsUnderHospitals.ViewHolderInternal>(AdapterDoctorsUnderHospitalsDiffUtil()) {


    val updatedArrayList = ArrayList<NetworkHospitalListing.Result.AvailableProvider?>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderInternal {
        val binding = DataBindingUtil.inflate<RowItemDoctorsUnderHospitalsBinding>(LayoutInflater.from(context), R.layout.row_item_doctors_under_hospitals, parent, false)
        return ViewHolderInternal(binding)
    }


    override fun onBindViewHolder(holder: ViewHolderInternal, position: Int) {
         holder.onBindView(getItem(position))
    }

    // dont call this from calling component
    override fun submitList(list: MutableList<NetworkHospitalListing.Result.AvailableProvider?>?) {
        super.submitList(if (list.isNullOrEmpty()) null else ArrayList(list))
    }

    fun loadDataIntoList(list: ArrayList<NetworkHospitalListing.Result.AvailableProvider?>?) {
        list?.let { updatedArrayList.addAll(it.toMutableList()) }
        submitList(updatedArrayList)
    }

    inner class ViewHolderInternal(val binding: RowItemDoctorsUnderHospitalsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                crdRoot.setOnClickListener {
                 mCallback.onDoctorClick(getItem(absoluteAdapterPosition))
                }
            }
        }

        fun onBindView(item: NetworkHospitalListing.Result.AvailableProvider?) {
            binding.run {
                setVariable(BR.node, item)
                executePendingBindings()
            }
        }
    }

}
    class AdapterDoctorsUnderHospitalsDiffUtil : DiffUtil.ItemCallback<NetworkHospitalListing.Result.AvailableProvider>() {
        override fun areItemsTheSame(oldItem: NetworkHospitalListing.Result.AvailableProvider, newItem: NetworkHospitalListing.Result.AvailableProvider): Boolean {
            return oldItem.user_id == newItem.user_id
        }

        override fun areContentsTheSame(oldItem: NetworkHospitalListing.Result.AvailableProvider, newItem: NetworkHospitalListing.Result.AvailableProvider): Boolean {
            return oldItem == newItem

        }

    }
