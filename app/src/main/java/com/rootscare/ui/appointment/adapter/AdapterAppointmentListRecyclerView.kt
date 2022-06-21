package com.rootscare.ui.appointment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnClickWithTwoButton
import com.rootscare.R
import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem
import com.rootscare.databinding.ItemAppointmentlistRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AdapterAppointmentListRecyclerView(
    val doctorAppointmentList: ArrayList<AppointmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterAppointmentListRecyclerView.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    var fromtime = ""
    var totime = ""
    internal lateinit var recyclerViewItemClick: OnClickWithTwoButton
//
//    internal lateinit var recyclerViewItemClickWithView: OnItemClickWithIdListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemAppointmentlistRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_appointmentlist_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return doctorAppointmentList!!.size
//        return 3
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemAppointmentlistRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.btn_appointment_view_details?.setOnClickListener {
                recyclerViewItemClick.onFirstItemClick(
                    localPosition,
                    doctorAppointmentList?.get(localPosition)?.id?.toInt()!!
                )
            }
            itemView.root.btn_appointment_add_review?.setOnClickListener {
                recyclerViewItemClick.onSecondItemClick(doctorAppointmentList?.get(localPosition)?.userId?.toInt()!!)
            }

        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            itemView.rootView?.txt_appointment_id?.text = doctorAppointmentList?.get(pos)?.orderId
            itemView.rootView?.txt_patient_name?.text = doctorAppointmentList?.get(pos)?.patientName
            itemView.rootView?.txt_doctor_name?.text = doctorAppointmentList?.get(pos)?.name
            itemView.rootView?.txt_phone_no?.text = doctorAppointmentList?.get(pos)?.patientContact
            itemView.rootView?.txt_booking_date?.text = formatDateFromString(
                "yyyy-MM-dd",
                "dd MMM yyyy",
                doctorAppointmentList?.get(pos)?.bookingDate
            )
            itemView.rootView?.txt_appointment_date?.text = formatDateFromString(
                "yyyy-MM-dd",
                "dd MMM yyyy",
                doctorAppointmentList?.get(pos)?.appointmentDate
            )
            itemView.rootView?.txt_appointment_status?.text =
                doctorAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.txt_appointment_acceptance?.text =
                doctorAppointmentList?.get(pos)?.acceptanceStatus

            itemView.rootView?.tv_status?.background =
                ContextCompat.getDrawable(context, R.drawable.approved_bg)
            itemView.rootView?.ll_mainlayout_appointment_history?.background =
                ContextCompat.getDrawable(context, R.drawable.order_approved_bg)
//            itemView.rootView?.tv_status?.text =
//                doctorAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.tv_status?.text =
                if (doctorAppointmentList?.get(pos)?.acceptanceStatus == "Pending")
                    doctorAppointmentList[pos]?.acceptanceStatus
                else doctorAppointmentList?.get(pos)?.acceptanceStatus

            fromtime =
                if (doctorAppointmentList?.get(pos)?.fromTime != null && !doctorAppointmentList[pos]?.fromTime.equals(
                        ""
                    )
                ) {
                    doctorAppointmentList[pos]?.fromTime!!
                } else {
                    ""
                }
            totime =
                if (doctorAppointmentList?.get(pos)?.toTime != null && !doctorAppointmentList[pos]?.toTime.equals(
                        ""
                    )
                ) {
                    doctorAppointmentList[pos]?.toTime!!
                } else {
                    ""
                }
            itemView.rootView?.txt_appointment_time?.text = "$fromtime-$totime"
        }

        private fun formatDateFromString(
            inputFormat: String?,
            outputFormat: String?,
            inputDate: String?
        ): String? {
            val parsed: Date?
            var outputDate = ""
            val dfInput =
                SimpleDateFormat(inputFormat, Locale.ENGLISH)
            val dfOutput =
                SimpleDateFormat(outputFormat, Locale.ENGLISH)
            try {
                parsed = dfInput.parse(inputDate)
                outputDate = dfOutput.format(parsed)
            } catch (e: ParseException) {
            }
            return outputDate
        }
    }

}