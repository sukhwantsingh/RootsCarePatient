package com.rootscare.ui.bookingcart.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnClickOfCartItem
import com.rootscare.R
import com.rootscare.databinding.LayoutNewRowItemCartBinding
import com.rootscare.ui.bookingcart.models.ModelPatientCartNew
import com.rootscare.ui.newaddition.appointments.ModelAppointmentDetails
import com.rootscare.ui.newaddition.appointments.adapter.AdapterPaymentSplitting
import com.rootscare.utilitycommon.setAmount


class AdapterBookingCartRecyclerview(val cartItemList: ArrayList<ModelPatientCartNew.Result?>?,
    internal var context: Context) : RecyclerView.Adapter<AdapterBookingCartRecyclerview.ViewHolder>() {

    internal lateinit var recyclerViewItemClickWithView: OnClickOfCartItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<LayoutNewRowItemCartBinding>(
                LayoutInflater.from(parent.context), R.layout.layout_new_row_item_cart_, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cartItemList?.size ?: 0

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(val binding: LayoutNewRowItemCartBinding) : RecyclerView.ViewHolder(binding.root) {
      init {
          binding.ivCross.setOnClickListener {
                recyclerViewItemClickWithView.onFirstItemClick(cartItemList?.get(absoluteAdapterPosition)?.id?.toInt()!!)
            }
        }

        fun onBind(pos: Int) {
            binding.run {
                cartItemList?.get(pos)?.let{
                tvhtop.text = it.provider_details?.provider_name
                tvOrderid.text = it.service_display_type
                tvBankName.text = it.display_app_date?.uppercase()  //"date"
                tvAcName.text = "${it.from_time} - ${it.to_time}".uppercase() //"Time"
                tvScheduleType.text = it.display_task_type //"Task type"
                tvBookingPref.text = it.display_task_time // task time

                // payment recyclerview
                setupTaksListing(binding, it.task_details)
                tvhDisFare.text = it.distance_fare_text
                tvDisFare.setAmount(it.distance_fare)
                tvhVat.text = it.vat_text
                tvVat.setAmount(it.vat_price)
                tvTotalPrice.setAmount(it.total_price)
               executePendingBindings()
                }
            }

        }
    }

    fun setupTaksListing(binding: LayoutNewRowItemCartBinding, taskDetails: List<ModelPatientCartNew.Result.TaskDetail?>?
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

    @JvmName("getCartItemList1")
    fun getCartItemList(): ArrayList<ModelPatientCartNew.Result?>? {
        return cartItemList!!
    }

}

/*

// Incase multiple item at time take approach from it
class AdapterBookingCartRecyclerview(val cartItemList: ArrayList<ModelPatientCartNew.Result?>?,
    internal var context: Context) : RecyclerView.Adapter<AdapterBookingCartRecyclerview.ViewHolder>() {

    internal lateinit var recyclerViewItemClickWithView: OnClickOfCartItem

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding = DataBindingUtil.inflate<ItemBookingCartRecyclerviewBinding>(
                LayoutInflater.from(context), R.layout.item_booking_cart_recyclerview, parent, false)
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return cartItemList?.size ?: 0

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemBookingCartRecyclerviewBinding) : RecyclerView.ViewHolder(itemView.root) {
      init {
            itemView.root.img_remove?.setOnClickListener {
                recyclerViewItemClickWithView.onFirstItemClick(cartItemList?.get(absoluteAdapterPosition)?.id?.toInt()!!)
            }
            itemView.root.checkbox_cartitem?.setOnClickListener {
                //   recyclerViewItemClickWithView?.onFirstItemClick(cartItemList?.get(local_position)?.id?.toInt()!!)
            }
            itemView.root.checkbox_cartitem?.setOnCheckedChangeListener { _, isChecked -> //set your object's last status
                cartItemList?.get(absoluteAdapterPosition)?.isselectitem = isChecked
                recyclerViewItemClickWithView.onSecondItemClick(cartItemList)
            }
        }

        fun onBind(pos: Int) {
            itemView.rootView?.checkbox_cartitem?.isChecked = cartItemList?.get(pos)?.isselectitem ?: true
            itemView.rootView?.txt_booked_doctor_name?.text = cartItemList?.get(pos)?.provider_details?.provider_name
            itemView.rootView?.txt_booked_doctor_qualification?.text = cartItemList?.get(pos)?.provider_details?.qualification
            itemView.rootView?.txt_booked_doctor_description?.text = cartItemList?.get(pos)?.provider_details?.speciality
            itemView.rootView?.txt_booked_doctor_appointmentdate?.text =
             "${cartItemList?.get(pos)?.from_date}\n${cartItemList?.get(pos)?.from_time} - ${cartItemList?.get(pos)?.to_time}"
            itemView.rootView?.txt_booked_doctor_fees?.setAmount(cartItemList?.get(pos)?.total_price)
            itemView.rootView?.img_doctor_photo?.setCircularRemoteImage(null)


//            itemView?.rootView?.txt_teacher_name?.text= trainerList?.get(pos)?.name
//            itemView?.rootView?.txt_teacher_qualification?.text= "Qualification : "+" "+trainerList?.get(pos)?.qualification
//            if(trainerList?.get(pos)?.avgRating!=null && !trainerList?.get(pos)?.avgRating.equals("")){
//                itemView?.rootView?.ratingBarteacher?.rating= trainerList?.get(pos)?.avgRating?.toFloat()!!
//            }


//            itemView?.rootView?.txt_rating_count?.text="("+contactListItem?.get(pos)?.contactRating+")"
//            (contactListItem?.get(pos)?.contactRating)?.toFloat()?.let { itemView?.rootView?.ratingBar?.setRating(it) }
//            itemView?.rootView?.ratingBar?.rating=1.5f


        }
    }

    @JvmName("getCartItemList1")
    fun getCartItemList(): ArrayList<ModelPatientCartNew.Result?>? {
        return cartItemList!!
    }

}
*/