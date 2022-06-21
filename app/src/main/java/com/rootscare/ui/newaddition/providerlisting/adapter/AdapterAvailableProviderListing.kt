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
import com.rootscare.ui.newaddition.providerlisting.models.ModelProviderDetails
import java.util.*

interface OnProviderAvailableListingCallback {
    fun onItemClick(pos: Int, id: String?) {}
}

class AdapterAvailableProviderListing(internal var context: Context) :
    ListAdapter<ModelProviderDetails.Result.AvailableProviders, AdapterAvailableProviderListing.ViewHolderInternal>(AdapterAvailableProviderListingDiffUtil()) {


    val updatedArrayList = ArrayList<ModelProviderDetails.Result.AvailableProviders?>()
    internal lateinit var mCallback: OnProviderAvailableListingCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderInternal {
        val binding = DataBindingUtil.inflate<LayoutNewItemRowAvailableProvidersBinding>(
            LayoutInflater.from(context),
            R.layout.layout_new_item_row_available_providers_, parent, false
        )
        return ViewHolderInternal(binding)
    }


    override fun onBindViewHolder(holder: ViewHolderInternal, position: Int) {
         holder.onBindView(getItem(position))
    }

    // dont call this from calling component
    override fun submitList(list: MutableList<ModelProviderDetails.Result.AvailableProviders?>?) {
        super.submitList(if (list.isNullOrEmpty()) null else ArrayList(list))
    }

    fun loadDataIntoList(list: ArrayList<ModelProviderDetails.Result.AvailableProviders?>?) {
        list?.let { updatedArrayList.addAll(it.toMutableList()) }
        submitList(updatedArrayList)
    }

    inner class ViewHolderInternal(val binding: LayoutNewItemRowAvailableProvidersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                crdRoot.setOnClickListener {
                    mCallback.onItemClick(absoluteAdapterPosition,
                        getItem(absoluteAdapterPosition)?.user_id ?: ""
                    )
                }
            }
        }

        fun onBindView(item: ModelProviderDetails.Result.AvailableProviders?) {
            binding.run {
                setVariable(BR.node, item)
                executePendingBindings()
            }
        }
    }

}
    class AdapterAvailableProviderListingDiffUtil : DiffUtil.ItemCallback<ModelProviderDetails.Result.AvailableProviders>() {
        override fun areItemsTheSame(oldItem: ModelProviderDetails.Result.AvailableProviders, newItem: ModelProviderDetails.Result.AvailableProviders): Boolean {
            return oldItem.user_id == newItem.user_id
        }

        override fun areContentsTheSame(oldItem: ModelProviderDetails.Result.AvailableProviders, newItem: ModelProviderDetails.Result.AvailableProviders): Boolean {
            return oldItem == newItem

        }

    }
