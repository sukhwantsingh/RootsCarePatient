package com.rootscare.ui.myupcomingappointment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnPhysiotherapyAppointment
import com.rootscare.R
import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem
import com.rootscare.databinding.ItemMyUpcomingAppointmentListNewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AdapterPhysiotherapyUpComingAppointment(
    val pathologyAppointmentList: ArrayList<AppointmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterPhysiotherapyUpComingAppointment.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    internal lateinit var recyclerViewItemClickWithView: OnPhysiotherapyAppointment

    var fromTime = ""
    var toTime = ""

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
        return pathologyAppointmentList?.size!!
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
                    pathologyAppointmentList?.get(
                        localPosition
                    )!!, localPosition.toString()
                )
            }

            itemView.root.btn_appointment_reschedule?.setOnClickListener {
                recyclerViewItemClickWithView.onRescheduleBtnClick(
                    pathologyAppointmentList?.get(
                        localPosition
                    )!!, localPosition.toString()
                )
            }

            itemView.root.btnViewDetails?.setOnClickListener {
                recyclerViewItemClickWithView.onViewDetailsClick(
                    pathologyAppointmentList?.get(
                        localPosition
                    )!!, localPosition.toString()
                )
            }

        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            when {
                pathologyAppointmentList?.get(pos)?.acceptanceStatus.equals("Pending") -> {
                    itemView.rootView?.ll_mainlayout?.background = null
//                    itemView.rootView?.setBackgroundColor(Color.parseColor("#ffffff"))
                    itemView.rootView?.tv_status?.background =
                        ContextCompat.getDrawable(context, R.drawable.accptance_pending_bg)
                    itemView.rootView?.btnViewDetails?.visibility = View.VISIBLE
                    itemView.rootView?.btn_appointment_cancel?.visibility = View.VISIBLE
                    itemView.rootView?.btn_appointment_reschedule?.visibility = View.VISIBLE
                    itemView.rootView?.btn_video?.visibility = View.GONE
                    itemView.rootView?.ll_mainlayout?.background =
                        ContextCompat.getDrawable(context, R.drawable.order_pending_bg)
//                    itemView.rootView?.btn_accepted?.visibility = View.GONE
                }
                pathologyAppointmentList?.get(pos)?.acceptanceStatus.equals("Rejected") -> {
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
                    itemView.rootView?.btn_appointment_cancel?.visibility = View.GONE
                    itemView.rootView?.btn_appointment_reschedule?.visibility = View.GONE
                    itemView.rootView?.btnViewDetails?.visibility = View.VISIBLE
                    itemView.rootView?.btn_video?.visibility = View.VISIBLE
                }
            }
            if (pathologyAppointmentList?.get(pos)?.orderId != null && !pathologyAppointmentList[pos]?.orderId.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_upcomming_appointment?.text =
                    pathologyAppointmentList[pos]?.orderId
            } else {
                itemView.rootView?.txt_upcomming_appointment?.text = ""
            }
//            if (pos == AppConstants.DoctorRescheduleClickPosition) {
//                if (AppConstants.IS_DOCTOR_RESCHEDULE) {
//                    val handler = Handler()
//                    handler.postDelayed({
//                        AppConstants.IS_DOCTOR_RESCHEDULE = false
//                        itemView.rootView?.txt_appointment_date?.setBackgroundColor(
//                            Color.parseColor("#ffffff")
//                        )
//                        // Execute a come thode in the interval 3sec
//                    }, 3000)
//
//                }
//            }

            if (pathologyAppointmentList?.get(pos)?.patientName != null && !pathologyAppointmentList[pos]?.patientName.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_patient_name?.text =
                    pathologyAppointmentList[pos]?.patientName
            } else {
                itemView.rootView?.txt_patient_name?.text = ""
            }

            if (pathologyAppointmentList?.get(pos)?.bookingDate != null && !pathologyAppointmentList[pos]?.bookingDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_booking_date?.text =
                    pathologyAppointmentList[pos]?.bookingDate?.let {
                        formatDateFromString(
                            "yyyy-MM-dd",
                            "dd MMM yyyy",
                            it
                        )
                    }
            } else {
                itemView.rootView?.txt_booking_date?.text = ""
            }
            itemView.rootView?.txt_name_header?.text = "Physiotherapist Name :"
            if (pathologyAppointmentList?.get(pos)?.name != null && !pathologyAppointmentList[pos]?.name.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_doctor_name?.text =
                    pathologyAppointmentList[pos]?.name
            } else {
                itemView.rootView?.txt_doctor_name?.text = ""
            }
            if (pathologyAppointmentList?.get(pos)?.appointmentDate != null && !pathologyAppointmentList[pos]?.appointmentDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_appointment_date?.text =
                    pathologyAppointmentList[pos]?.appointmentDate?.let {
                        formatDateFromString(
                            "yyyy-MM-dd",
                            "dd MMM yyyy",
                            it
                        )
                    }
            } else {
                itemView.rootView?.txt_appointment_date?.text = ""
            }

            fromTime =
                if (pathologyAppointmentList?.get(pos)?.fromTime != null && !pathologyAppointmentList[pos]?.fromTime.equals(
                        ""
                    )
                ) {
                    pathologyAppointmentList[pos]?.fromTime!!
                } else {
                    ""
                }
            toTime =
                if (pathologyAppointmentList?.get(pos)?.toTime != null && !pathologyAppointmentList[pos]?.toTime.equals(
                        ""
                    )
                ) {
                    pathologyAppointmentList[pos]?.toTime!!
                } else {
                    ""
                }
            itemView.rootView?.txt_appointment_time?.text = "$fromTime-$toTime"
            if (pathologyAppointmentList?.get(pos)?.patientContact != null && !pathologyAppointmentList[pos]?.patientContact.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_upcoming_appointmentphone_no?.text =
                    pathologyAppointmentList[pos]?.patientContact
            } else {
                itemView.rootView?.txt_upcoming_appointmentphone_no?.text = ""
            }
            itemView.rootView?.txt_upappointment_status?.text =
                pathologyAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.txt_upappointment_acceptance?.text =
                pathologyAppointmentList?.get(pos)?.acceptanceStatus
//            itemView.rootView?.tv_status?.text =
//                pathologyAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.tv_status?.text =
                if (pathologyAppointmentList?.get(pos)?.acceptanceStatus == "Pending")
                    pathologyAppointmentList[pos]?.acceptanceStatus
                else pathologyAppointmentList?.get(pos)?.acceptanceStatus
            itemView.rootView?.btn_video?.visibility = View.GONE

            if (pathologyAppointmentList?.get(pos)?.taskDetails != null && !pathologyAppointmentList[pos]?.taskDetails?.testName.equals(
                    ""
                )
            ) {
                itemView.tv_appointment.text = "Task Name : "
                itemView.txt_appointment_time.text =
                    pathologyAppointmentList[pos]!!.taskDetails?.testName
            }

        }

        private fun formatDateFromString(
            inputFormat: String?,
            outputFormat: String?,
            inputDate: String?
        ): String {
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