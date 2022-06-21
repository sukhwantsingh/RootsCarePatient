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

class AdapterPhysiotherapyAppointmentListRecyclerView(
    val pathologyAppointmentList: ArrayList<AppointmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterPhysiotherapyAppointmentListRecyclerView.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    internal lateinit var recyclerViewItemClickWithView: OnClickWithTwoButton

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemAppointmentlistRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_appointmentlist_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return pathologyAppointmentList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemAppointmentlistRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.btn_appointment_view_details?.setOnClickListener {
                recyclerViewItemClickWithView.onFirstItemClick(
                    localPosition,
                    pathologyAppointmentList?.get(localPosition)?.id?.toInt()!!
                )
            }
            itemView.root.btn_appointment_add_review?.setOnClickListener {
                recyclerViewItemClickWithView.onSecondItemClick(
                    pathologyAppointmentList?.get(
                        localPosition
                    )?.id?.toInt()!!
                )
            }
        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

//            itemView.rootView?.txt_appointment_id?.text =
//                pathologyAppointmentList?.get(pos)?.orderId
//            itemView.rootView?.txt_patient_name?.text =
//                pathologyAppointmentList?.get(pos)?.patientName
//            itemView.rootView?.txt_appoitment_header?.text = "Physiotherapist Name : "
//            itemView.rootView?.txt_doctor_name?.text =
//                pathologyAppointmentList?.get(pos)?.name
//            itemView.rootView?.txt_phone_no?.text =
//                pathologyAppointmentList?.get(pos)?.patientContact
//            itemView.rootView?.txt_booking_date?.text =
//                formatDateFromString(
//                    "yyyy-MM-dd",
//                    "dd MMM yyyy",
//                    pathologyAppointmentList?.get(pos)?.bookingDate
//                )
////            itemView.rootView?.txt_appointment_date?.text =
////                formatDateFromString(
////                    "yyyy-MM-dd",
////                    "dd MMM yyyy",
////                    pathologyAppointmentList?.get(pos)?.fromDate
////                ) + " " + "-" + " " + formatDateFromString(
////                    "yyyy-MM-dd",
////                    "dd MMM yyyy",
////                    pathologyAppointmentList?.get(pos)?.toDate
////                )
//            itemView.rootView?.txt_appointment_status?.text =
//                pathologyAppointmentList?.get(pos)?.appointmentStatus
//            itemView.rootView?.txt_appointment_acceptance?.text =
//                pathologyAppointmentList?.get(pos)?.acceptanceStatus
//
//            itemView.rootView?.tv_status?.background =
//                ContextCompat.getDrawable(context, R.drawable.approved_bg)
//            itemView.rootView?.ll_mainlayout?.background =
//                ContextCompat.getDrawable(context, R.drawable.order_approved_bg)
//            itemView.rootView?.tv_status?.text =
//                if (pathologyAppointmentList?.get(pos)?.acceptanceStatus == "Pending") pathologyAppointmentList[pos]?.acceptanceStatus else pathologyAppointmentList?.get(
//                    pos
//                )?.appointmentStatus


            itemView.rootView?.txt_appointment_id?.text =
                pathologyAppointmentList?.get(pos)?.orderId
            itemView.rootView?.txt_patient_name?.text =
                pathologyAppointmentList?.get(pos)?.patientName
            itemView.rootView?.txt_appoitment_header?.text = "Physiotherapist Name : "
            itemView.rootView?.txt_doctor_name?.text =
                pathologyAppointmentList?.get(pos)?.hospitalName
            itemView.rootView?.txt_phone_no?.text =
                pathologyAppointmentList?.get(pos)?.patientContact
            itemView.rootView?.txt_booking_date?.text =
                formatDateFromString(
                    "yyyy-MM-dd",
                    "dd MMM yyyy",
                    pathologyAppointmentList?.get(pos)?.bookingDate
                )
            itemView.rootView?.txt_appointment_date?.text =
                formatDateFromString(
                    "yyyy-MM-dd",
                    "dd MMM yyyy",
                    pathologyAppointmentList?.get(pos)?.appointmentDate
                )
            itemView.rootView?.txt_appointment_status?.text =
                pathologyAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.txt_appointment_acceptance?.text =
                pathologyAppointmentList?.get(pos)?.acceptanceStatus

            itemView.rootView?.tv_status?.background =
                ContextCompat.getDrawable(context, R.drawable.approved_bg)
            itemView.rootView?.ll_mainlayout_appointment_history?.background =
                ContextCompat.getDrawable(context, R.drawable.order_approved_bg)
//            itemView.rootView?.tv_status?.text =
//                pathologyAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.tv_status?.text =
                if (pathologyAppointmentList?.get(pos)?.acceptanceStatus == "Pending")
                    pathologyAppointmentList[pos]?.acceptanceStatus
                else pathologyAppointmentList?.get(pos)?.acceptanceStatus
//            println("pathologyAppointmentList[pos]!!.pathologyName ${pathologyAppointmentList?.get(pos)!!.pathologyName}")
            if (pathologyAppointmentList?.get(pos)?.taskDetails != null && !pathologyAppointmentList[pos]?.taskDetails?.testName.equals(
                    ""
                )
            ) {
                itemView.txt_appoitment_header.text = "Task Name : "
                itemView.txt_doctor_name.text =
                    pathologyAppointmentList[pos]!!.taskDetails?.testName
            }

        }

        private fun formatDateFromString(
            inputFormat: String?,
            outputFormat: String?,
            inputDate: String?
        ): String? {
            val parsed: Date?
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