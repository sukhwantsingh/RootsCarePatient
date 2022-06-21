package com.rootscare.ui.newaddition.appointments.adapter

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
import com.rootscare.databinding.LayoutItemNewAppointmentsBinding
import com.rootscare.ui.newaddition.appointments.ModelAppointmentsListing
import com.rootscare.ui.newaddition.providerlisting.models.ModelTaskListWithPrice
import com.rootscare.utilitycommon.ProviderTypes
import com.rootscare.utilitycommon.SlotBookingId
import com.rootscare.utilitycommon.TransactionStatus
import com.rootscare.utilitycommon.needToShowVideoCall
import kotlin.collections.ArrayList

interface OnAppointmentListingCallback {
    fun onItemClick(pos: Int,node: ModelAppointmentsListing.Result?) {}
    fun onReschedule(pos: Int, node: ModelAppointmentsListing.Result?) {}
    fun onVideoCall(dataModel: ModelAppointmentsListing.Result?) {}
    fun onLoadMore(pos: Int, lastuserId: String)
}

class AdapterAppointmentListingCommon(internal var context: Context) :
    ListAdapter<ModelAppointmentsListing.Result, AdapterAppointmentListingCommon.ViewHolder>(AppointmentListingDiffUtil()) {

    companion object {
        val TAG: String = AdapterAppointmentListingCommon::class.java.simpleName
    }

    val updatedArrayList = ArrayList<ModelAppointmentsListing.Result?>()
    internal lateinit var mCallback: OnAppointmentListingCallback

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<LayoutItemNewAppointmentsBinding>(
            LayoutInflater.from(context),
            R.layout.layout_item_new_appointments, parent, false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(getItem(position))
        if(position == itemCount - 1) {
            mCallback.onLoadMore(position,getItem(position).id ?: "")
        }
    }
    // dont call this from calling component
    override fun submitList(list: MutableList<ModelAppointmentsListing.Result?>?) {
        super.submitList(if (list.isNullOrEmpty()) null else ArrayList(list))
    }

    fun loadDataIntoList(list: ArrayList<ModelAppointmentsListing.Result?>?) {
        list?.let { updatedArrayList.addAll(it.toMutableList()) }
        submitList(updatedArrayList)
    }

    fun updateAfterFilterList(list: ArrayList<ModelAppointmentsListing.Result?>?) {
        list?.let {
            submitList(java.util.ArrayList(list))
        }
    }

    fun updateReschedule(pos: Int?, bkDate:String, bkTime:String) {
        pos?.let {
            updatedArrayList[it]?.app_date = bkDate
            updatedArrayList[it]?.app_time = bkTime
            submitList(ArrayList(updatedArrayList))
            notifyItemChanged(pos)
        }

    }

    inner class ViewHolder(val binding: LayoutItemNewAppointmentsBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.apply {
                btnViewDetails.setOnClickListener {
                    mCallback.onItemClick(absoluteAdapterPosition,getItem(absoluteAdapterPosition)) }

              btnRescheduleCall.setOnClickListener {
                    mCallback.onReschedule(absoluteAdapterPosition, getItem(absoluteAdapterPosition))
                }
           btnVideoCall.setOnClickListener {
              mCallback.onVideoCall(getItem(absoluteAdapterPosition)) }
            }
        }

        fun onBindView(item: ModelAppointmentsListing.Result?) {
            binding.run {
                setVariable(BR.node, item)
                when {
                    item?.acceptance_status?.equals(TransactionStatus.PENDING.get(), ignoreCase = true) == true -> {
                       btnRescheduleCall.visibility = View.VISIBLE
                       btnVideoCall.visibility = View.GONE
                    }
                    item?.acceptance_status?.equals(TransactionStatus.ACCEPTED.get(), ignoreCase = true) == true -> {
                        btnRescheduleCall.visibility = View.GONE

                        if((item.provider_type.equals(ProviderTypes.HOSPITAL_DOCTOR.getType(), ignoreCase = true) || item.provider_type.equals(ProviderTypes.DOCTOR.getType(), ignoreCase = true) )&&
                           item.slot_booking_id.equals(SlotBookingId.ONLINE_BOOKING.get(), ignoreCase = true)) {
                            needToShowVideoCall(item.app_date, item.app_time, btnVideoCall)
                        //   btnVideoCall.visibility = View.VISIBLE
                        } else btnVideoCall.visibility = View.GONE

                    }
                    else -> {
                        btnVideoCall.visibility = View.GONE
                        btnRescheduleCall.visibility = View.GONE
                    }
                }
               executePendingBindings()
           }
        }
    }
}

class AppointmentListingDiffUtil : DiffUtil.ItemCallback<ModelAppointmentsListing.Result>() {
    override fun areItemsTheSame(oldItem: ModelAppointmentsListing.Result, newItem: ModelAppointmentsListing.Result): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ModelAppointmentsListing.Result, newItem: ModelAppointmentsListing.Result): Boolean {
        return oldItem == newItem

    }

}