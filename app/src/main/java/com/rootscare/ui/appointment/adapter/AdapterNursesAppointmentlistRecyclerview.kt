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
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.tv_appointment
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.tv_status
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.txt_appointment_date
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.txt_appointment_time
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.txt_booking_date
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.txt_doctor_name
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.txt_patient_name
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.*
import kotlinx.android.synthetic.main.item_my_upcomingappointment_recyclerview.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AdapterNursesAppointmentlistRecyclerview(
    val nurseAppointmentList: ArrayList<AppointmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterNursesAppointmentlistRecyclerview.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    internal lateinit var recyclerViewItemClick: OnClickWithTwoButton
    var fromtime = ""
    var totime = ""
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemAppointmentlistRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_appointmentlist_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return nurseAppointmentList!!.size
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
                    nurseAppointmentList?.get(localPosition)?.id?.toInt()!!
                )
            }
            itemView.root.btn_appointment_add_review?.setOnClickListener {
                recyclerViewItemClick.onSecondItemClick(nurseAppointmentList?.get(localPosition)?.userId?.toInt()!!)
            }
//


        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos
            itemView.rootView?.txt_appointment_id?.text = nurseAppointmentList?.get(pos)?.orderId
            itemView.rootView?.txt_patient_name?.text = nurseAppointmentList?.get(pos)?.patientName
            itemView.rootView?.txt_appoitment_header?.text = "Nurse Name : "
            itemView.rootView?.txt_doctor_name?.text = nurseAppointmentList?.get(pos)?.name
            itemView.rootView?.txt_phone_no?.text = nurseAppointmentList?.get(pos)?.patientContact
            itemView.rootView?.txt_booking_date?.text = formatDateFromString(
                "yyyy-MM-dd",
                "dd MMM yyyy",
                nurseAppointmentList?.get(pos)?.bookingDate
            )
//            itemView.rootView?.txt_appointment_date?.text = formatDateFromString(
//                "yyyy-MM-dd",
//                "dd MMM yyyy",
//                nurseAppointmentList?.get(pos)?.fromDate
//            )
//            +" "+"-"+" "+formateDateFromstring("yyyy-MM-dd","dd MMM yyyy",nurseAppointmentList?.get(pos)?.toDate)
            itemView.rootView?.txt_appointment_status?.text =
                nurseAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.txt_appointment_acceptance?.text =
                nurseAppointmentList?.get(pos)?.acceptanceStatus

            itemView.rootView?.tv_status?.background =
                ContextCompat.getDrawable(context, R.drawable.approved_bg)
            itemView.rootView?.ll_mainlayout_appointment_history?.background =
                ContextCompat.getDrawable(context, R.drawable.order_approved_bg)
//            itemView.rootView?.tv_status?.text =
//                nurseAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.tv_status?.text =
                if (nurseAppointmentList?.get(pos)?.acceptanceStatus == "Pending")
                    nurseAppointmentList[pos]?.acceptanceStatus
                else nurseAppointmentList?.get(pos)?.acceptanceStatus

            fromtime =
                if (nurseAppointmentList?.get(pos)?.fromTime != null && !nurseAppointmentList[pos]?.fromTime.equals(
                        ""
                    )
                ) {
                    nurseAppointmentList[pos]?.fromTime!!
                } else {
                    ""
                }
            totime =
                if (nurseAppointmentList?.get(pos)?.toTime != null && !nurseAppointmentList.get(pos)?.toTime.equals(
                        ""
                    )
                ) {
                    nurseAppointmentList[pos]?.toTime!!
                } else {
                    ""
                }
            itemView.rootView?.txt_appointment_time?.text = "$fromtime-$totime"

//            if (nurseAppointmentList?.get(pos)?.taskDetails?.testName != null && !nurseAppointmentList[pos]?.taskDetails?.testName.equals(
//                    ""
//                )
//            ) {
//                itemView.tv_appointment.text = "Test Name : "
//                itemView.txt_appointment_time.text =
//                    nurseAppointmentList[pos]!!.taskDetails?.testName
//            }
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