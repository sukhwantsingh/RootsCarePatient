package com.rootscare.ui.newaddition.providerlisting.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.LayoutItemNewProvidersBinding
import com.rootscare.ui.newaddition.providerlisting.models.ModelProviderListing
import com.rootscare.utilitycommon.ProviderTypes

interface OnProviderListingCallback {
    fun onItemClick(pos: Int, id: String?, usType: String) {}
    fun onBookAppointment(pos: Int, node: ModelProviderListing.Result?) {}
    fun onLoadMore(pos: Int, lastuserId: String) {}
}

class AdapterProviderListing(internal var context: Context) :
    ListAdapter<ModelProviderListing.Result, AdapterProviderListing.ViewHolder>(
        AdapterProviderListingDiffUtil()
    ) {


    val updatedArrayList = ArrayList<ModelProviderListing.Result?>()
    internal lateinit var mCallback: OnProviderListingCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<LayoutItemNewProvidersBinding>(
            LayoutInflater.from(context),
            R.layout.layout_item_new_providers_, parent, false
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

    inner class ViewHolder(val binding: LayoutItemNewProvidersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                tvUsername.setOnClickListener { imgProfile.performClick() }
                imgProfile.setOnClickListener {
                    val mNode = getItem(absoluteAdapterPosition)
                    mCallback.onItemClick(absoluteAdapterPosition,mNode?.user_id ?: "",mNode?.user_type ?: "")
                }

                btnBookAppointment.setOnClickListener {
                    val mNode = getItem(absoluteAdapterPosition)
                    mCallback.onBookAppointment(absoluteAdapterPosition, mNode)
                }
            }
        }

        fun onBindView(item: ModelProviderListing.Result?) {
            binding.run {
                setVariable(BR.node, item)
                binding.btnBookAppointment.text = if(item?.user_type.equals(ProviderTypes.LAB.getType(), ignoreCase = true)){
                context.getString(R.string.book_tests) } else context.getString(R.string.book_appointment)
                executePendingBindings()
            }
        }
    }
}

class AdapterProviderListingDiffUtil : DiffUtil.ItemCallback<ModelProviderListing.Result>() {
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