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
import com.rootscare.databinding.LnRowItemHealthPacksAvailableBinding
import com.rootscare.ui.newaddition.providerlisting.models.ModelProviderDetails
import java.util.*

interface OnAvailablePackageListingCallback {
    fun onItemClick(pos: Int, id: String?) {}
    fun onBookClick(pos: Int, id: String?)
}

class AdapterAvailablepackageListing(internal var context: Context) :
    ListAdapter<ModelProviderDetails.Result.AvailablePackages, AdapterAvailablepackageListing.ViewHolderInternal>(AdapterAvailablePackageListingDiffUtil()) {


    val updatedArrayList = ArrayList<ModelProviderDetails.Result.AvailablePackages?>()
    internal lateinit var mCallback: OnAvailablePackageListingCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolderInternal {
        val binding = DataBindingUtil.inflate<LnRowItemHealthPacksAvailableBinding>(
            LayoutInflater.from(context),
            R.layout.ln_row_item_health_packs_available, parent, false
        )
        return ViewHolderInternal(binding)
    }


    override fun onBindViewHolder(holder: ViewHolderInternal, position: Int) {
         holder.onBindView(getItem(position))
    }

    // dont call this from calling component
    override fun submitList(list: MutableList<ModelProviderDetails.Result.AvailablePackages?>?) {
        super.submitList(if (list.isNullOrEmpty()) null else ArrayList(list))
    }

    fun loadDataIntoList(list: ArrayList<ModelProviderDetails.Result.AvailablePackages?>?) {
        list?.let { updatedArrayList.addAll(it.toMutableList()) }
        submitList(updatedArrayList)
    }

    inner class ViewHolderInternal(val binding: LnRowItemHealthPacksAvailableBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                crdRoot.setOnClickListener {
                   mCallback.onItemClick(absoluteAdapterPosition, getItem(absoluteAdapterPosition)?.pid.orEmpty())
                }
                tvhBook.setOnClickListener {
                 mCallback.onBookClick(absoluteAdapterPosition, getItem(absoluteAdapterPosition)?.pid.orEmpty())
                }
            }
        }

        fun onBindView(item: ModelProviderDetails.Result.AvailablePackages?) {
            binding.run {
                setVariable(BR.node, item)
                executePendingBindings()
            }
        }
    }

}
    class AdapterAvailablePackageListingDiffUtil : DiffUtil.ItemCallback<ModelProviderDetails.Result.AvailablePackages>() {
        override fun areItemsTheSame(oldItem: ModelProviderDetails.Result.AvailablePackages, newItem: ModelProviderDetails.Result.AvailablePackages): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(oldItem: ModelProviderDetails.Result.AvailablePackages, newItem: ModelProviderDetails.Result.AvailablePackages): Boolean {
            return oldItem == newItem

        }

    }
