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
import com.rootscare.data.model.api.response.appointmenthistoryresponse.PathologyAppointmentItem
import com.rootscare.databinding.ItemAppointmentlistRecyclerviewBinding
import com.interfaces.OnItemClikWithIdListener
import com.interfaces.OnUpcomingAppointmentButtonClickListener
import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem
import com.rootscare.data.model.api.response.appointmenthistoryresponse.DoctorAppointmentItem
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

class AdapterHospitalAppointmentlistRecyclerview(
    val pathologyAppointmentList: ArrayList<AppointmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterHospitalAppointmentlistRecyclerview.ViewHolder>() {
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
                recyclerViewItemClickWithView.onFirstItemClick(
                    localPosition,
                    pathologyAppointmentList?.get(localPosition)?.id?.toInt()!!
                )
            }
            itemView.root.btn_appointment_add_review?.setOnClickListener {
                recyclerViewItemClickWithView.onSecondItemClick(
                    pathologyAppointmentList?.get(
                        localPosition
                    )?.userId?.toInt()!!
                )
            }

        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            itemView.rootView?.txt_appointment_id?.text =
                pathologyAppointmentList?.get(pos)?.orderId
            itemView.rootView?.txt_patient_name?.text =
                pathologyAppointmentList?.get(pos)?.patientName
            itemView.rootView?.txt_appoitment_header?.text = "Hospital Name : "
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
            itemView.rootView?.ll_mainlayout?.background =
                ContextCompat.getDrawable(context, R.drawable.order_approved_bg)
//            itemView.rootView?.tv_status?.text =
//                pathologyAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.tv_status?.text =
                if (pathologyAppointmentList?.get(pos)?.acceptanceStatus == "Pending")
                    pathologyAppointmentList[pos]?.acceptanceStatus
                else pathologyAppointmentList?.get(pos)?.acceptanceStatus

        }

        private fun formatDateFromString(
            inputFormat: String?,
            outputFormat: String?,
            inputDate: String?
        ): String? {
            var parsed: Date?
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