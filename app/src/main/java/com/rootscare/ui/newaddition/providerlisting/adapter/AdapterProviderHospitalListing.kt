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

interface OnProviderHospitalListingCallback {
    fun onItemClick(pos: Int, id: String?, usType: String) {}
    fun onFindSpecAndDocs(pos: Int, node: ModelProviderListing.Result?) {}
    fun onDoctorClick(node:ModelProviderListing.Result?){}
    fun onLoadMore(pos: Int, lastuserId: String) {}
}

class AdapterProviderHospitalListing(internal var context: Context, private val mCallback: OnProviderHospitalListingCallback) : ListAdapter<ModelProviderListing.Result, AdapterProviderHospitalListing.ViewHolder>(
        AdapterProviderHospitalListingDiffUtil()) {

    val updatedArrayList = ArrayList<ModelProviderListing.Result?>()
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
            mCallback.onLoadMore(position, getItem(position).user_id ?: "")
        }
    }

    // dont call this from calling component
    override fun submitList(list: MutableList<ModelProviderListing.Result?>?) {
        super.submitList(if (list.isNullOrEmpty()) null else ArrayList(list))
    }

    fun loadDataIntoList(list: ArrayList<ModelProviderListing.Result?>?) {
        list?.let { updatedArrayList.addAll(it.toMutableList()) }
        submitList(updatedArrayList)
    }

    fun updateMarkAcceptReject(pos: Int?) {
        pos?.let {
            updatedArrayList.removeAt(it)
            submitList(updatedArrayList)
        }

    }

    inner class ViewHolder(val binding: RowItemHospitalBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                tvUsername.setOnClickListener { imgProfile.performClick() }
                imgProfile.setOnClickListener {
                    val mNode = getItem(absoluteAdapterPosition)
                    mCallback.onItemClick(absoluteAdapterPosition,mNode?.user_id ?: "",mNode?.user_type ?: "")
                }

                btnFindSpecDocs.setOnClickListener {
                    val mNode = getItem(absoluteAdapterPosition)
                    mCallback.onFindSpecAndDocs(absoluteAdapterPosition, mNode)
                }
            }
        }

        fun onBindView(item: ModelProviderListing.Result?) {
            binding.run {
                mDocsAdapter = AdapterDoctorsUnderHospitals(context, mCallback)
                rvProviders.adapter = mDocsAdapter
                mDocsAdapter?.updatedArrayList?.clear()
                mDocsAdapter?.loadDataIntoList(updatedArrayList)

                setVariable(BR.node, item)
                executePendingBindings()
            }
        }
    }
}

class AdapterProviderHospitalListingDiffUtil : DiffUtil.ItemCallback<ModelProviderListing.Result>() {
    override fun areItemsTheSame(
        oldItem: ModelProviderListing.Result,
        newItem: ModelProviderListing.Result
    ): Boolean {
        return oldItem.user_id == newItem.user_id
    }

    override fun areContentsTheSame(
        oldItem: ModelProviderListing.Result,
        newItem: ModelProviderListing.Result
    ): Boolean {
        return oldItem == newItem

    }

}