package com.rootscare.ui.newaddition.providerlisting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R

import com.rootscare.databinding.RowItemBookingPackagesBinding
import com.rootscare.ui.newaddition.providerlisting.models.ModelBookingInitialLabData
import com.rootscare.utilitycommon.setAmount
import com.rootscare.utilitycommon.setAmountWithCurrency

interface OnPacakgePriceCallback {
    fun onTaskClick(node: ModelBookingInitialLabData.Result.PackageBaseTask?)
}

class AdapterBookingPackages :
    ListAdapter<ModelBookingInitialLabData.Result.PackageBaseTask, AdapterBookingPackages.ViewHolder>(
        BookingPackageListDiffUtil()
    ) {

    var mCallback: OnPacakgePriceCallback? = null
    val updatedArrayList: MutableList<ModelBookingInitialLabData.Result.PackageBaseTask?> =
        ArrayList()
    var mCurrency: String? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<RowItemBookingPackagesBinding>(
            LayoutInflater.from(parent.context),
            R.layout.row_item_booking_packages, parent, false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(getItem(position))
    }

    fun updateStatus(pos: Int, checkStatus: Boolean) {
        updatedArrayList.forEach { it?.isChecked = false }
        updatedArrayList[pos]?.isChecked = checkStatus
        //  submitList(ArrayList(updatedArrayList))
        notifyItemRangeChanged(0, itemCount)
    }

    fun loadDataIntoList(list: ArrayList<ModelBookingInitialLabData.Result.PackageBaseTask?>?) {
        list?.let { updatedArrayList.addAll(it.toMutableList()) }
        submitList(updatedArrayList)
    }

    inner class ViewHolder(val binding: RowItemBookingPackagesBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.crdRoot.setOnClickListener {
                updateStatus(absoluteAdapterPosition, true)
                mCallback?.onTaskClick(getItem(absoluteAdapterPosition))
            }
        }

        fun onBindView(item: ModelBookingInitialLabData.Result.PackageBaseTask?) {
            binding.run {
                tvhname.text = item?.name.orEmpty()
                tvhTestIncludes.text = item?.test_count.orEmpty()

                if (mCurrency.isNullOrBlank().not()) {
                    tvPrice.setAmountWithCurrency(item?.price ?: "0", mCurrency)
                } else tvPrice.setAmount(item?.price ?: "0")

                rlRoot.setBackgroundResource(if (item?.isChecked == true) { R.drawable.round_border_blue_white_bg } else R.drawable.round_gray_white_bg
                )
            }
        }
    }
}

class BookingPackageListDiffUtil :
    DiffUtil.ItemCallback<ModelBookingInitialLabData.Result.PackageBaseTask>() {
    override fun areItemsTheSame(
        oldItem: ModelBookingInitialLabData.Result.PackageBaseTask,
        newItem: ModelBookingInitialLabData.Result.PackageBaseTask
    ): Boolean {
        return oldItem.pid == newItem.pid
    }

    override fun areContentsTheSame(
        oldItem: ModelBookingInitialLabData.Result.PackageBaseTask,
        newItem: ModelBookingInitialLabData.Result.PackageBaseTask
    ): Boolean {
        return oldItem == newItem
    }

}