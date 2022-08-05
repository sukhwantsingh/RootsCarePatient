package com.rootscare.ui.newaddition.providerlisting.packages.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.LayoutNewItemPackagesBinding
import com.rootscare.ui.newaddition.providerlisting.packages.models.ModelPackages

interface OnLabPackageCallback {
    fun onLabPackage(packId: String?)
}

class AdapterLabPackages(internal var context: Context) :
    ListAdapter<ModelPackages.Result.PackageBaseTask, AdapterLabPackages.ViewHolder>(LabPackagesListingDiffUtil()) {

    var mCallback: OnLabPackageCallback? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<LayoutNewItemPackagesBinding>(
            LayoutInflater.from(context),
            R.layout.layout_new_item_packages, parent, false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(getItem(position))
    }

    override fun submitList(list: MutableList<ModelPackages.Result.PackageBaseTask?>?) {
        super.submitList(if (list.isNullOrEmpty()) null else ArrayList(list))
    }

    inner class ViewHolder(val binding: LayoutNewItemPackagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.crdMain.setOnClickListener {
                mCallback?.onLabPackage(getItem(absoluteAdapterPosition).id)
            }
        }

        fun onBindView(item: ModelPackages.Result.PackageBaseTask?) {
            binding.run {
                setVariable(BR.node, item)
                executePendingBindings()
            }
        }
    }
}


class LabPackagesListingDiffUtil : DiffUtil.ItemCallback<ModelPackages.Result.PackageBaseTask>() {
    override fun areItemsTheSame(
        oldItem: ModelPackages.Result.PackageBaseTask,
        newItem: ModelPackages.Result.PackageBaseTask
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ModelPackages.Result.PackageBaseTask,
        newItem: ModelPackages.Result.PackageBaseTask
    ): Boolean {
        return oldItem.id == newItem.id && oldItem.name == newItem.name
    }

}