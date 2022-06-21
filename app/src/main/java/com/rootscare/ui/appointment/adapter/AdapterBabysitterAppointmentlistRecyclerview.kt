package com.rootscare.ui.appointment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.data.model.api.response.appointmenthistoryresponse.BabysitterAppointmentItem
import com.rootscare.databinding.ItemAppointmentlistRecyclerviewBinding
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.*
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.tv_status
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.txt_appointment_date
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.txt_booking_date
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.txt_doctor_name
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.txt_patient_name
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class AdapterBabysitterAppointmentlistRecyclerview(
    val babysitterAppointmentList: ArrayList<AppointmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterBabysitterAppointmentlistRecyclerview.ViewHolder>() {
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //
    internal lateinit var recyclerViewItemClickWithView: OnItemClikWithIdListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemAppointmentlistRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_appointmentlist_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return babysitterAppointmentList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemAppointmentlistRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.crdview_appoitment_list?.setOnClickListener {
                recyclerViewItemClickWithView.onItemClick(1)
            }

        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            itemView.rootView?.txt_appointment_id?.text =
                babysitterAppointmentList?.get(pos)?.orderId
            itemView.rootView?.txt_patient_name?.text =
                babysitterAppointmentList?.get(pos)?.patientName
            itemView.rootView?.txt_appoitment_header?.text = "Babysitter Name : "
            itemView.rootView?.txt_doctor_name?.text =
                babysitterAppointmentList?.get(pos)?.name
            itemView.rootView?.txt_booking_date?.text =
                formatDateFromString(
                    "yyyy-MM-dd",
                    "dd MMM yyyy",
                    babysitterAppointmentList?.get(pos)?.bookingDate
                )
            itemView.rootView?.txt_phone_no?.text =
                babysitterAppointmentList?.get(pos)?.patientContact
//            itemView.rootView?.txt_appointment_date?.text =
//                formatDateFromString(
//                    "yyyy-MM-dd",
//                    "dd MMM yyyy",
//                    babysitterAppointmentList?.get(pos)?.fromDate
//                ) + " " + "-" + " " + formatDateFromString(
//                    "yyyy-MM-dd",
//                    "dd MMM yyyy",
//                    babysitterAppointmentList?.get(pos)?.toDate
//                )
            itemView.rootView?.txt_appointment_status?.text =
                babysitterAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.txt_appointment_acceptance?.text =
                babysitterAppointmentList?.get(pos)?.acceptanceStatus

            itemView.rootView?.tv_status?.background =
                ContextCompat.getDrawable(context, R.drawable.approved_bg)
            itemView.rootView?.ll_mainlayout?.background =
                ContextCompat.getDrawable(context, R.drawable.order_approved_bg)
//            itemView.rootView?.tv_status?.text =
//                babysitterAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.tv_status?.text =
                if (babysitterAppointmentList?.get(pos)?.acceptanceStatus == "Pending")
                    babysitterAppointmentList[pos]?.acceptanceStatus
                else babysitterAppointmentList?.get(pos)?.acceptanceStatus

        }

        private fun formatDateFromString(
            inputFormat: String?,
            outputFormat: String?,
            inputDate: String?
        ): String? {
            var parsed: Date? = null
            var outputDate = ""
            val df_input =
                SimpleDateFormat(inputFormat, Locale.ENGLISH)
            val df_output =
                SimpleDateFormat(outputFormat, Locale.ENGLISH)
            try {
                parsed = df_input.parse(inputDate)
                outputDate = df_output.format(parsed)
            } catch (e: ParseException) {
            }
            return outputDate
        }
    }

}