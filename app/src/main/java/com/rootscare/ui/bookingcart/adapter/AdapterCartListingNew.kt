package com.rootscare.ui.bookingcart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnClickOfCartItem
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.response.nurses.nurselist.GetNurseListResponse
import com.rootscare.data.model.api.response.nurses.nurselist.ResultItem
import com.rootscare.databinding.LayoutItemNewAppointmentsBinding
import com.rootscare.databinding.LayoutItemNewCartitemsBinding
import com.rootscare.databinding.LayoutItemNewProvidersBinding
import com.rootscare.databinding.LayoutNewRowItemCartBinding
import com.rootscare.ui.bookingcart.models.ModelPatientCartNew
import com.rootscare.ui.newaddition.appointments.ModelAppointmentDetails
import com.rootscare.ui.newaddition.appointments.ModelAppointmentsListing
import com.rootscare.ui.newaddition.appointments.adapter.AdapterPaymentSplitting
import com.rootscare.ui.newaddition.providerlisting.models.ModelProviderListing
import com.rootscare.utilitycommon.*
import java.util.*

class AdapterCartListingNew(internal var context: Context) :
    ListAdapter<ModelPatientCartNew.Result, AdapterCartListingNew.ViewHolder>(AdaptercartListingDiffUtil()) {

    val updatedArrayList = ArrayList<ModelPatientCartNew.Result?>()

    internal lateinit var mCallback: OnClickOfCartItem

    var mCurrency: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<LayoutItemNewCartitemsBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_item_new_cartitems, parent, false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(getItem(position))
    }

    // dont call this from calling component
    override fun submitList(list: MutableList<ModelPatientCartNew.Result?>?) {
        super.submitList(if (list.isNullOrEmpty()) null else ArrayList(list))
    }

    fun loadDataIntoList(list: ArrayList<ModelPatientCartNew.Result?>?) {
        list?.let { updatedArrayList.addAll(it.toMutableList()) }
        submitList(updatedArrayList)
    }

  inner class ViewHolder(val binding: LayoutItemNewCartitemsBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.ivCross.setOnClickListener {
                mCallback.onFirstItemClick(updatedArrayList.get(absoluteAdapterPosition)?.id?.toInt()!!)
            }
        }

        fun onBindView(item: ModelPatientCartNew.Result?) {
            binding.run {
                item?.let{
                    if(it.booking_type.equals(BookingTypes.ONLINE_CONS.getApiType(), ignoreCase = true)){
                        tvhDisFare.visibility = View.GONE
                        tvDisFare.visibility = View.GONE
                        tvhDisFare.text = ""
                        tvDisFare.text = ""
                    } else {
                        tvhDisFare.visibility = View.VISIBLE
                        tvDisFare.visibility = View.VISIBLE

                        tvhDisFare.text = it.distance_fare_text
                        tvDisFare.setAmount(it.distance_fare)
                    }

                    tvhtop.text = it.provider_details?.provider_name
                    tvOrderid.text = it.service_display_type
                    tvBankName.text = it.display_app_date?.uppercase()  //"date"
                    tvAcName.text = "${it.from_time} - ${it.to_time}".uppercase() //"Time"
                    tvScheduleType.text = it.display_task_type //"Task type"
                    tvBookingPref.text = it.display_task_time // task time

                    // payment recyclerview
                    setupTaksListing(binding, it.task_details)
                    tvhVat.text = it.vat_text
                    tvVat.setAmount(it.vat_price)
                    if(mCurrency.isNullOrBlank()) {
                        tvTotalPrice.setAmount(it.total_price)
                    } else tvTotalPrice.setAmountWithCurrency(it.total_price, mCurrency)
                    executePendingBindings()
                }
            }
        }


  }
    fun setupTaksListing(binding: LayoutItemNewCartitemsBinding, taskDetails: List<ModelPatientCartNew.Result.TaskDetail?>?
    ) {
        val mAdapPayment = AdapterPaymentSplitting()
        val mArrList = ArrayList<ModelAppointmentDetails.Result.TaskDetail>()
        binding.rvPayments.adapter = mAdapPayment
        taskDetails?.forEach { node ->
            val mNode = ModelAppointmentDetails.Result.TaskDetail(
                node?.id ?: "",node?.name ?: "", node?.price ?: "0")
            mArrList.add(mNode)
        }
        mAdapPayment.submitList(mArrList)
    }
}

class AdaptercartListingDiffUtil : DiffUtil.ItemCallback<ModelPatientCartNew.Result>() {
    override fun areItemsTheSame(oldItem: ModelPatientCartNew.Result, newItem: ModelPatientCartNew.Result): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: ModelPatientCartNew.Result, newItem: ModelPatientCartNew.Result): Boolean {
        return oldItem == newItem

    }

}