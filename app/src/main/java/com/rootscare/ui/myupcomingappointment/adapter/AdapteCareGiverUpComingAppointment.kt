package com.rootscare.ui.myupcomingappointment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnCaregiverAppointment
import com.rootscare.R
import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem
import com.rootscare.databinding.ItemMyUpcomingAppointmentListNewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.*
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.*
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.btn_appointment_cancel
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.btn_appointment_reschedule
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.ll_mainlayout
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.tv_status
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_appointment_date
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_appointment_time
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_booking_date
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_doctor_name
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_name_header
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_patient_name
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_upappointment_acceptance
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_upappointment_status
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_upcoming_appointmentphone_no
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_upcomming_appointment
import kotlinx.android.synthetic.main.item_my_upcomingappointment_recyclerview.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AdapteCareGiverUpComingAppointment(
    val caregiverAppointmentList: ArrayList<AppointmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapteCareGiverUpComingAppointment.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    internal lateinit var recyclerViewItemClickWithView: OnCaregiverAppointment
    var fromtime = ""
    var totime = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemMyUpcomingAppointmentListNewBinding>(
                LayoutInflater.from(context),
                R.layout.item_my_upcoming_appointment_list_new, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
//        return trainerList!!.size
        return caregiverAppointmentList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemMyUpcomingAppointmentListNewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.btn_appointment_cancel?.setOnClickListener {
                recyclerViewItemClickWithView.onCancelBtnClick(
                    caregiverAppointmentList?.get(
                        localPosition
                    )!!, localPosition.toString()
                )
            }

            itemView.root.btn_appointment_reschedule?.setOnClickListener {
                recyclerViewItemClickWithView.onRescheduleBtnClick(
                    caregiverAppointmentList?.get(
                        localPosition
                    )!!, localPosition.toString()
                )
            }

            itemView.root.btnViewDetails?.setOnClickListener {
                recyclerViewItemClickWithView.onViewDetailsClick(
                    caregiverAppointmentList?.get(
                        localPosition
                    )!!, localPosition.toString()
                )
            }

        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            when {
                caregiverAppointmentList?.get(pos)?.acceptanceStatus.equals("Pending") -> {
                    itemView.rootView?.ll_mainlayout?.background = null
                    itemView.rootView?.tv_status?.background =
                        ContextCompat.getDrawable(context, R.drawable.accptance_pending_bg)
                    itemView.rootView?.btnViewDetails?.visibility = View.VISIBLE
                    itemView.rootView?.btn_appointment_cancel?.visibility = View.VISIBLE
                    itemView.rootView?.btn_appointment_reschedule?.visibility = View.VISIBLE
                    itemView.rootView?.btn_video?.visibility = View.GONE
                    itemView.rootView?.ll_mainlayout?.background =
                        ContextCompat.getDrawable(context, R.drawable.order_pending_bg)
                }
                caregiverAppointmentList?.get(pos)?.acceptanceStatus.equals("Rejected") -> {
                    itemView.rootView?.ll_mainlayout?.background =
                        ContextCompat.getDrawable(context, R.drawable.appointment_reject_background)
                    itemView.rootView?.tv_status?.background =
                        ContextCompat.getDrawable(context, R.drawable.reject_bg)
                    itemView.rootView?.btnViewDetails?.visibility = View.VISIBLE
                    itemView.rootView?.btn_appointment_cancel?.visibility = View.GONE
                    itemView.rootView?.btn_appointment_reschedule?.visibility = View.GONE
                    itemView.rootView?.btn_video?.visibility = View.GONE
                }
                else -> {
                    itemView.rootView?.ll_mainlayout?.background =
                        ContextCompat.getDrawable(context, R.drawable.background_green_stroke_box)
                    itemView.rootView?.tv_status?.background =
                        ContextCompat.getDrawable(context, R.drawable.approved_bg)
                    itemView.rootView?.ll_mainlayout?.background =
                        ContextCompat.getDrawable(context, R.drawable.order_approved_bg)
                    itemView.rootView?.btnViewDetails?.visibility = View.VISIBLE
                    itemView.rootView?.btn_appointment_cancel?.visibility = View.GONE
                    itemView.rootView?.btn_appointment_reschedule?.visibility = View.GONE
                    itemView.rootView?.btn_video?.visibility = View.GONE
                }
            }

            if (caregiverAppointmentList?.get(pos)?.orderId != null && !caregiverAppointmentList[pos]?.orderId.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_upcomming_appointment?.text =
                    caregiverAppointmentList[pos]?.orderId
            } else {
                itemView.rootView?.txt_upcomming_appointment?.text = ""
            }

            if (caregiverAppointmentList?.get(pos)?.patientName != null && !caregiverAppointmentList[pos]?.patientName.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_patient_name?.text =
                    caregiverAppointmentList[pos]?.patientName
            } else {
                itemView.rootView?.txt_patient_name?.text = ""
            }
            itemView.rootView?.txt_name_header?.text = "Caregiver Name :"
            if (caregiverAppointmentList?.get(pos)?.name != null && !caregiverAppointmentList[pos]?.name.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_doctor_name?.text =
                    caregiverAppointmentList[pos]?.name
            } else {
                itemView.rootView?.txt_doctor_name?.text = ""
            }

            if (caregiverAppointmentList?.get(pos)?.bookingDate != null && !caregiverAppointmentList[pos]?.bookingDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_booking_date?.text = formatDateFromString(
                    "yyyy-MM-dd",
                    "dd MMM yyyy",
                    caregiverAppointmentList[pos]?.bookingDate
                )
            } else {
                itemView.rootView?.txt_booking_date?.text = ""
            }

            if (caregiverAppointmentList?.get(pos)?.appointmentDate != null && !caregiverAppointmentList[pos]?.appointmentDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_appointment_date?.text = formatDateFromString(
                    "yyyy-MM-dd",
                    "dd MMM yyyy",
                    caregiverAppointmentList[pos]?.appointmentDate
                )
            } else {
                itemView.rootView?.txt_appointment_date?.text = ""
            }

            if (caregiverAppointmentList?.get(pos)?.patientContact != null && !caregiverAppointmentList[pos]?.patientContact.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_upcoming_appointmentphone_no?.text =
                    caregiverAppointmentList[pos]?.patientContact
            } else {
                itemView.rootView?.txt_upcoming_appointmentphone_no?.text = ""
            }
            fromtime =
                if (caregiverAppointmentList?.get(pos)?.fromTime != null && !caregiverAppointmentList[pos]?.fromTime.equals(
                        ""
                    )
                ) {
                    caregiverAppointmentList[pos]?.fromTime!!
                } else {
                    ""
                }
            totime =
                if (caregiverAppointmentList?.get(pos)?.toTime != null && !caregiverAppointmentList[pos]?.toTime.equals(
                        ""
                    )
                ) {
                    caregiverAppointmentList[pos]?.toTime!!
                } else {
                    ""
                }
            itemView.rootView?.txt_appointment_time?.text = "$fromtime-$totime"
            itemView.rootView?.txt_upappointment_status?.text =
                caregiverAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.txt_upappointment_acceptance?.text = caregiverAppointmentList?.get(
                pos
            )?.acceptanceStatus
//            itemView.rootView?.tv_status?.text =
//                caregiverAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.tv_status?.text =
                if (caregiverAppointmentList?.get(pos)?.acceptanceStatus == "Pending")
                    caregiverAppointmentList[pos]?.acceptanceStatus
                else caregiverAppointmentList?.get(pos)?.acceptanceStatus

        }

        private fun formatDateFromString(
            inputFormat: String?, outputFormat: String?, inputDate: String?
        ): String {
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