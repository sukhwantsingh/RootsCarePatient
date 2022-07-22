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
import com.rootscare.databinding.LayoutItemNewProvidersBinding
import com.rootscare.databinding.RowItemHospitalBinding
import com.rootscare.ui.newaddition.providerlisting.models.ModelProviderListing
import com.rootscare.ui.newaddition.providerlisting.models.NetworkHospitalListing

interface OnProviderHospitalListingCallback {
    fun onItemClick(pos: Int, node: NetworkHospitalListing.Result?) {}
    fun onFindSpecAndDocs(pos: Int, node: NetworkHospitalListing.Result?) {}
    fun onDoctorClick(node:NetworkHospitalListing.Result.AvailableProvider?){}
    fun onLoadMore(pos: Int, lastuserId: String) {}
}

class AdapterProviderHospitalListing(internal var context: Context, private val mCallback: OnProviderHospitalListingCallback) : ListAdapter<NetworkHospitalListing.Result, AdapterProviderHospitalListing.ViewHolder>(
        AdapterProviderHospitalListingDiffUtil()) {

    val updatedArrayList = ArrayList<NetworkHospitalListing.Result?>()
    private var mDocsAdapter: AdapterDoctorsUnderHospitals? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RowItemHospitalBinding>(
            LayoutInflater.from(context),
            R.layout.row_item_hospital, parent, false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(getItem(position))

        if (position == itemCount - 1) {
            mCallback.onLoadMore(position, getItem(position).hospital_id ?: "")
        }
    }

    // dont call this from calling component
    override fun submitList(list: MutableList<NetworkHospitalListing.Result?>?) {
        super.submitList(if (list.isNullOrEmpty()) null else ArrayList(list))
    }

    fun loadDataIntoList(list: ArrayList<NetworkHospitalListing.Result?>?) {
        list?.let { updatedArrayList.addAll(it.toMutableList()) }
        submitList(updatedArrayList)
    }

    inner class ViewHolder(val binding: RowItemHospitalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                tvUsername.setOnClickListener { imgProfile.performClick() }
                imgProfile.setOnClickListener {
                    mCallback.onItemClick(absoluteAdapterPosition,getItem(absoluteAdapterPosition))
                }

                btnFindSpecDocs.setOnClickListener {
                    mCallback.onFindSpecAndDocs(absoluteAdapterPosition, getItem(absoluteAdapterPosition))
                }
            }
        }

        fun onBindView(item: NetworkHospitalListing.Result?) {
            binding.run {
              mDocsAdapter = AdapterDoctorsUnderHospitals(context, mCallback)
              rvProviders.adapter = mDocsAdapter
              mDocsAdapter?.updatedArrayList?.clear()
              mDocsAdapter?.loadDataIntoList(item?.available_provider)

                setVariable(BR.node, item)
                executePendingBindings()
            }
        }
    }
}

class AdapterProviderHospitalListingDiffUtil : DiffUtil.ItemCallback<NetworkHospitalListing.Result>() {
    override fun areItemsTheSame(
        oldItem: NetworkHospitalListing.Result,
        newItem: NetworkHospitalListing.Result
    ): Boolean {
        return oldItem.hospital_id == newItem.hospital_id
    }

    override fun areContentsTheSame(
        oldItem: NetworkHospitalListing.Result,
        newItem: NetworkHospitalListing.Result
    ): Boolean {
        return oldItem == newItem

    }

}