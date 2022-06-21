package com.rootscare.ui.myupcomingappointment.adapter

import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnUpcomingAppointmentButtonClickListener
import com.rootscare.R
import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem
import com.rootscare.databinding.ItemMyUpcomingAppointmentListNewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import com.rootscare.utils.AppConstants
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.*
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.btn_appointment_cancel
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.btn_appointment_reschedule
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.ll_mainlayout
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_appointment_date
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_appointment_time
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_booking_date
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_doctor_name
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_name_header
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_patient_name
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_upappointment_acceptance
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_upappointment_status
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_upcomming_appointment
import kotlinx.android.synthetic.main.item_my_upcomingappointment_recyclerview.view.*
import java.text.DateFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class AdapterAllAppointment(
    val doctorAppointmentList: ArrayList<AppointmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterAllAppointment.ViewHolder>() {
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    internal lateinit var recyclerViewItemClickWithView: OnUpcomingAppointmentButtonClickListener
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
        return doctorAppointmentList?.size!!
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
                    doctorAppointmentList?.get(
                        localPosition
                    )!!, localPosition.toString()

                )
            }

            itemView.root.btn_appointment_reschedule?.setOnClickListener {
                recyclerViewItemClickWithView.onRescheduleBtnClick(
                    doctorAppointmentList?.get(
                        localPosition
                    )!!, localPosition.toString()
                )
            }
            itemView.root.btnViewDetails?.setOnClickListener {
                recyclerViewItemClickWithView.onViewDetailsClick(
                    doctorAppointmentList?.get(
                        localPosition
                    )!!, localPosition.toString()
                )
            }

            itemView.root.btn_video?.setOnClickListener {
                recyclerViewItemClickWithView.onVideoCallClick(
                    doctorAppointmentList?.get(
                        localPosition
                    )!!, localPosition.toString()
                )
            }

        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            if (doctorAppointmentList?.get(pos)?.orderId != null && !doctorAppointmentList[pos]?.orderId.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_upcomming_appointment?.text =
                    doctorAppointmentList[pos]?.orderId
            } else {
                itemView.rootView?.txt_upcomming_appointment?.text = ""
            }

            if (doctorAppointmentList?.get(pos)?.patientName != null && !doctorAppointmentList[pos]?.patientName.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_patient_name?.text =
                    doctorAppointmentList[pos]?.patientName
            } else {
                itemView.rootView?.txt_patient_name?.text = ""
            }

            if (doctorAppointmentList?.get(pos)?.name != null && !doctorAppointmentList[pos]?.name.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_doctor_name?.text =
                    doctorAppointmentList[pos]?.name
            } else {
                itemView.rootView?.txt_doctor_name?.text = ""
            }

            if (doctorAppointmentList?.get(pos)?.bookingDate != null && !doctorAppointmentList[pos]?.bookingDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_booking_date?.text =
                    doctorAppointmentList[pos]?.bookingDate?.let {
                        formatDateFromString(
                            "yyyy-MM-dd",
                            "dd MMM yyyy",
                            it
                        )
                    }
            } else {
                itemView.rootView?.txt_booking_date?.text = ""
            }

            if (doctorAppointmentList?.get(pos)?.appointmentDate != null && !doctorAppointmentList[pos]?.appointmentDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_appointment_date?.text =
                    doctorAppointmentList[pos]?.appointmentDate?.let {
                        formatDateFromString(
                            "yyyy-MM-dd",
                            "dd MMM yyyy",
                            it
                        )
                    }
            } else {
                itemView.rootView?.txt_appointment_date?.text = ""
            }

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

            if (doctorAppointmentList?.get(pos)?.userType == "doctor") {
                itemView.rootView?.txt_name_header?.text = "Doctor Name : "
                when {
                    doctorAppointmentList[pos]?.acceptanceStatus.equals("Pending") -> {
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
                    doctorAppointmentList[pos]?.acceptanceStatus.equals("Rejected") -> {
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.appointment_reject_background
                            )
                        itemView.rootView?.tv_status?.background =
                            ContextCompat.getDrawable(context, R.drawable.reject_bg)
                        itemView.rootView?.btnViewDetails?.visibility = View.VISIBLE
                        itemView.rootView?.btn_appointment_cancel?.visibility = View.GONE
                        itemView.rootView?.btn_appointment_reschedule?.visibility = View.GONE
                        itemView.rootView?.btn_video?.visibility = View.GONE
                    }
                    else -> {
                        println("xcxcxcxc is here")
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.background_green_stroke_box
                            )
                        itemView.rootView?.tv_status?.background =
                            ContextCompat.getDrawable(context, R.drawable.approved_bg)
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(context, R.drawable.order_approved_bg)
                        itemView.rootView?.btn_appointment_cancel?.visibility = View.GONE
                        itemView.rootView?.btn_appointment_reschedule?.visibility = View.GONE
                        itemView.rootView?.btnViewDetails?.visibility = View.VISIBLE
                        val timeString =
                            if (doctorAppointmentList[pos]?.fromTime?.isNotEmpty() == true && doctorAppointmentList[pos]?.fromTime?.split(
                                    " "
                                )?.get(0)?.length == 4
                            ) "0" + doctorAppointmentList[pos]?.fromTime?.split(" ")
                                ?.get(0) else doctorAppointmentList[pos]?.fromTime?.split(" ")
                                ?.get(0)
                        val input = formatDateFromString(
                            "yyyy-MM-dd",
                            "dd-MM-yyyy", doctorAppointmentList[pos]?.appointmentDate!!
                        ) + " " + timeString + ":00" + " " + (if (doctorAppointmentList[pos]?.fromTime?.isNotEmpty() == true) doctorAppointmentList[pos]?.fromTime?.split(
                            " "
                        )?.get(1) else "")
//            val input = "20-12-2020 10:30:00 am"
//            println("input 1234567 : $input")
                        val df: DateFormat =
                            SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.ENGLISH)
                        //Desired format: 24 hour format: Change the pattern as per the need
//            val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
                        val date: Date?
//            val output: String?
                        try {
                            //Converting the input String to Date
                            date = df.parse(input)
                            //Changing the format of date and storing it in String
                            val calendar = Calendar.getInstance()
                            //Setting the Calendar date and time to the given date and time
                            calendar.time = date

                            if ((calendar.timeInMillis - System.currentTimeMillis()) <= TimeUnit.MINUTES.toMillis(
                                    2
                                )
                            ) {
                                itemView.rootView?.btn_video?.visibility = View.VISIBLE
                            } else {
                                itemView.rootView?.btn_video?.visibility = View.GONE
                            }
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
//                        itemView.rootView?.btn_video?.visibility = View.VISIBLE
                    }
                }

                itemView.rootView?.txt_upappointment_status?.text =
                    doctorAppointmentList[pos]?.appointmentStatus
                itemView.rootView?.txt_upappointment_acceptance?.text =
                    this@AdapterAllAppointment.doctorAppointmentList[pos]?.acceptanceStatus
                itemView.rootView?.tv_status?.text =
                    if (doctorAppointmentList[pos]?.acceptanceStatus == "Pending")
                        doctorAppointmentList[pos]?.acceptanceStatus
                    else doctorAppointmentList[pos]?.acceptanceStatus

            } else if (doctorAppointmentList?.get(pos)?.userType == "nurse") {
                itemView.rootView?.txt_name_header?.text = "Nurse Name : "

                when {
                    doctorAppointmentList[pos]?.acceptanceStatus.equals("Pending") -> {
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
                    doctorAppointmentList[pos]?.acceptanceStatus.equals("Rejected") -> {
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.appointment_reject_background
                            )
                        itemView.rootView?.tv_status?.background =
                            ContextCompat.getDrawable(context, R.drawable.reject_bg)
                        itemView.rootView?.btnViewDetails?.visibility = View.VISIBLE
                        itemView.rootView?.btn_appointment_cancel?.visibility = View.GONE
                        itemView.rootView?.btn_appointment_reschedule?.visibility = View.GONE
                        itemView.rootView?.btn_video?.visibility = View.GONE
                    }
                    else -> {
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.background_green_stroke_box
                            )
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
                itemView.rootView?.txt_upappointment_status?.text =
                    doctorAppointmentList[pos]?.appointmentStatus
                itemView.rootView?.txt_upappointment_acceptance?.text =
                    doctorAppointmentList[pos]?.acceptanceStatus
//            itemView.rootView?.tv_status?.text =
//                pathologyAppointmentList?.get(pos)?.appointmentStatus
                itemView.rootView?.tv_status?.text =
                    if (doctorAppointmentList[pos]?.acceptanceStatus == "Pending")
                        doctorAppointmentList[pos]?.acceptanceStatus
                    else doctorAppointmentList[pos]!!.acceptanceStatus

                if (doctorAppointmentList[pos]?.taskDetails?.testName != null && !doctorAppointmentList[pos]?.taskDetails?.testName.equals(
                        ""
                    )
                ) {
                    itemView.tv_appointment.text = "Test Name : "
                    itemView.txt_appointment_time.text =
                        doctorAppointmentList[pos]!!.taskDetails?.testName
                }
            } else if (doctorAppointmentList?.get(pos)?.userType == "caregiver") {
                itemView.rootView?.txt_name_header?.text = "Caregiver Name : "
                when {
                    doctorAppointmentList[pos]?.acceptanceStatus.equals("Pending") -> {
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
                    doctorAppointmentList[pos]?.acceptanceStatus.equals("Rejected") -> {
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.appointment_reject_background
                            )
                        itemView.rootView?.tv_status?.background =
                            ContextCompat.getDrawable(context, R.drawable.reject_bg)
                        itemView.rootView?.btnViewDetails?.visibility = View.VISIBLE
                        itemView.rootView?.btn_appointment_cancel?.visibility = View.GONE
                        itemView.rootView?.btn_appointment_reschedule?.visibility = View.GONE
                        itemView.rootView?.btn_video?.visibility = View.GONE
                    }
                    else -> {
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.background_green_stroke_box
                            )
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

                itemView.rootView?.txt_upappointment_status?.text =
                    doctorAppointmentList[pos]?.appointmentStatus
                itemView.rootView?.txt_upappointment_acceptance?.text =
                    doctorAppointmentList[pos]?.acceptanceStatus
                itemView.rootView?.tv_status?.text =
                    if (doctorAppointmentList[pos]?.acceptanceStatus == "Pending")
                        doctorAppointmentList[pos]?.acceptanceStatus
                    else doctorAppointmentList.get(pos)!!.acceptanceStatus
            } else if (doctorAppointmentList?.get(pos)?.userType == "babysitter") {
                itemView.rootView?.txt_name_header?.text = "BabySitter Name : "

                when {
                    doctorAppointmentList[pos]?.acceptanceStatus.equals("Pending") -> {
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
                    doctorAppointmentList[pos]?.acceptanceStatus.equals("Rejected") -> {
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.appointment_reject_background
                            )
                        itemView.rootView?.tv_status?.background =
                            ContextCompat.getDrawable(context, R.drawable.reject_bg)
                        itemView.rootView?.btnViewDetails?.visibility = View.VISIBLE
                        itemView.rootView?.btn_appointment_cancel?.visibility = View.GONE
                        itemView.rootView?.btn_appointment_reschedule?.visibility = View.GONE
                        itemView.rootView?.btn_video?.visibility = View.GONE
                    }
                    else -> {
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.background_green_stroke_box
                            )
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
                itemView.rootView?.txt_upappointment_status?.text =
                    doctorAppointmentList[pos]?.appointmentStatus
                itemView.rootView?.txt_upappointment_acceptance?.text =
                    doctorAppointmentList[pos]?.acceptanceStatus
                itemView.rootView?.tv_status?.text =
                    if (doctorAppointmentList[pos]?.acceptanceStatus == "Pending")
                        doctorAppointmentList[pos]?.acceptanceStatus
                    else doctorAppointmentList[pos]!!.acceptanceStatus


            } else if (doctorAppointmentList?.get(pos)?.userType == "hospital_doctor") {
                itemView.rootView?.txt_name_header?.text = "Hospital Name : "
                if (doctorAppointmentList[pos]?.hospitalName != null && !doctorAppointmentList[pos]?.hospitalName.equals(
                        ""
                    )
                ) {
                    itemView.rootView?.txt_doctor_name?.text =
                        doctorAppointmentList[pos]?.hospitalName
                } else {
                    itemView.rootView?.txt_doctor_name?.text = ""
                }

                when {
                    doctorAppointmentList[pos]?.acceptanceStatus.equals("Pending") -> {
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
                    doctorAppointmentList[pos]?.acceptanceStatus.equals("Rejected") -> {
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.appointment_reject_background
                            )
                        itemView.rootView?.tv_status?.background =
                            ContextCompat.getDrawable(context, R.drawable.reject_bg)
                        itemView.rootView?.btnViewDetails?.visibility = View.VISIBLE
                        itemView.rootView?.btn_appointment_cancel?.visibility = View.GONE
                        itemView.rootView?.btn_appointment_reschedule?.visibility = View.GONE
                        itemView.rootView?.btn_video?.visibility = View.GONE
                    }

                    else -> {
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.background_green_stroke_box
                            )
                        itemView.rootView?.tv_status?.background =
                            ContextCompat.getDrawable(context, R.drawable.approved_bg)
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(context, R.drawable.order_approved_bg)
                        itemView.rootView?.btn_appointment_cancel?.visibility = View.GONE
                        itemView.rootView?.btn_appointment_reschedule?.visibility = View.GONE
                        itemView.rootView?.btnViewDetails?.visibility = View.VISIBLE
//                        itemView.rootView?.btn_video?.visibility = View.VISIBLE

                        val timeString = if (doctorAppointmentList[pos]?.fromTime?.split(" ")
                                ?.get(0)?.length == 4
                        ) "0" + doctorAppointmentList[pos]?.fromTime?.split(" ")
                            ?.get(0) else doctorAppointmentList[pos]?.fromTime?.split(" ")
                            ?.get(0)
                        val input = formatDateFromString(
                            "yyyy-MM-dd",
                            "dd-MM-yyyy", doctorAppointmentList[pos]?.appointmentDate!!
                        ) + " " + timeString + ":00" + " " + (if (doctorAppointmentList[pos]?.fromTime?.isNotEmpty() == true) doctorAppointmentList[pos]?.fromTime?.split(
                            " "
                        )?.get(1) else "")
//            val input = "20-12-2020 10:30:00 am"
//            println("input 1234567 : $input")
                        val df: DateFormat =
                            SimpleDateFormat("dd-MM-yyyy hh:mm:ss aa", Locale.ENGLISH)
                        //Desired format: 24 hour format: Change the pattern as per the need
//            val outputFormat: DateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
                        val date: Date?
//            val output: String?
                        try {
                            //Converting the input String to Date
                            date = df.parse(input)
                            //Changing the format of date and storing it in String
//                output = outputFormat.format(date)
                            val calendar = Calendar.getInstance()
                            //Setting the Calendar date and time to the given date and time
                            calendar.time = date

                            if ((calendar.timeInMillis - System.currentTimeMillis()) <= TimeUnit.MINUTES.toMillis(
                                    2
                                )
                            ) {
                                itemView.rootView?.btn_video?.visibility = View.VISIBLE
                            } else {
                                itemView.rootView?.btn_video?.visibility = View.GONE
                            }
                        } catch (e: ParseException) {
                            e.printStackTrace()
                        }
                    }
                }

                itemView.rootView?.txt_upappointment_status?.text =
                    doctorAppointmentList[pos]?.appointmentStatus
                itemView.rootView?.txt_upappointment_acceptance?.text =
                    doctorAppointmentList[pos]?.acceptanceStatus
                itemView.rootView?.tv_status?.text =
                    if (doctorAppointmentList[pos]?.acceptanceStatus == "Pending")
                        doctorAppointmentList[pos]?.acceptanceStatus
                    else doctorAppointmentList[pos]?.acceptanceStatus


            } else if (doctorAppointmentList?.get(pos)?.userType == "pathology") {
                when {
                    doctorAppointmentList[pos]?.acceptanceStatus.equals("Pending") -> {
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
                    doctorAppointmentList[pos]?.acceptanceStatus.equals("Rejected") -> {
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.appointment_reject_background
                            )
                        itemView.rootView?.tv_status?.background =
                            ContextCompat.getDrawable(context, R.drawable.reject_bg)
                        itemView.rootView?.btnViewDetails?.visibility = View.VISIBLE
                        itemView.rootView?.btn_appointment_cancel?.visibility = View.GONE
                        itemView.rootView?.btn_appointment_reschedule?.visibility = View.GONE
                        itemView.rootView?.btn_video?.visibility = View.GONE
                    }
                    else -> {
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.background_green_stroke_box
                            )
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
                if (pos == AppConstants.DoctorRescheduleClickPosition) {
                    if (AppConstants.IS_DOCTOR_RESCHEDULE) {
                        val handler = Handler()
                        handler.postDelayed({
                            AppConstants.IS_DOCTOR_RESCHEDULE = false
                            itemView.rootView?.txt_appointment_date?.setBackgroundColor(
                                Color.parseColor("#ffffff")
                            )
                            // Execute a come thode in the interval 3sec
                        }, 3000)
                    }
                }
                itemView.rootView?.txt_name_header?.text = "Hospital Name : "
                if (doctorAppointmentList[pos]?.hospitalName != null && !doctorAppointmentList[pos]?.hospitalName.equals(
                        ""
                    )
                ) {
                    itemView.rootView?.txt_doctor_name?.text =
                        doctorAppointmentList[pos]?.hospitalName
                } else {
                    itemView.rootView?.txt_doctor_name?.text = ""
                }
                itemView.rootView?.txt_upappointment_status?.text =
                    doctorAppointmentList[pos]?.appointmentStatus
                itemView.rootView?.txt_upappointment_acceptance?.text =
                    doctorAppointmentList[pos]?.acceptanceStatus
                itemView.rootView?.tv_status?.text =
                    if (doctorAppointmentList[pos]?.acceptanceStatus == "Pending")
                        doctorAppointmentList[pos]?.acceptanceStatus
                    else
                        doctorAppointmentList[pos]?.acceptanceStatus

                itemView.rootView?.btn_video?.visibility = View.GONE

                if (doctorAppointmentList[pos]?.pathologyName != null && !doctorAppointmentList[pos]?.pathologyName.equals(
                        ""
                    )
                ) {
                    itemView.tv_appointment.text = "Test Name : "
                    itemView.txt_appointment_time.text =
                        doctorAppointmentList[pos]!!.pathologyName
                }
            } else if (doctorAppointmentList?.get(pos)?.userType == "physiotherapy") {
//                itemView.rootView?.txt_name_header?.text = "Physiotherapist Name : "
                when {
                    doctorAppointmentList[pos]?.acceptanceStatus.equals("Pending") -> {
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
                    doctorAppointmentList[pos]?.acceptanceStatus.equals("Rejected") -> {
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.appointment_reject_background
                            )
                        itemView.rootView?.tv_status?.background =
                            ContextCompat.getDrawable(context, R.drawable.reject_bg)
                        itemView.rootView?.btnViewDetails?.visibility = View.VISIBLE
                        itemView.rootView?.btn_appointment_cancel?.visibility = View.GONE
                        itemView.rootView?.btn_appointment_reschedule?.visibility = View.GONE
                        itemView.rootView?.btn_video?.visibility = View.GONE
                    }
                    else -> {
                        itemView.rootView?.ll_mainlayout?.background =
                            ContextCompat.getDrawable(
                                context,
                                R.drawable.background_green_stroke_box
                            )
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
                if (pos == AppConstants.DoctorRescheduleClickPosition) {
                    if (AppConstants.IS_DOCTOR_RESCHEDULE) {
                        val handler = Handler()
                        handler.postDelayed({
                            AppConstants.IS_DOCTOR_RESCHEDULE = false
                            itemView.rootView?.txt_appointment_date?.setBackgroundColor(
                                Color.parseColor("#ffffff")
                            )
                            // Execute a come thode in the interval 3sec
                        }, 3000)
                    }
                }
                itemView.rootView?.txt_name_header?.text = "Physiotherapist Name : "
                if (doctorAppointmentList[pos]?.name != null && !doctorAppointmentList[pos]?.name.equals(
                        ""
                    )
                ) {
                    itemView.rootView?.txt_doctor_name?.text =
                        doctorAppointmentList[pos]?.name
                } else {
                    itemView.rootView?.txt_doctor_name?.text = ""
                }
                itemView.rootView?.txt_upappointment_status?.text =
                    doctorAppointmentList[pos]?.appointmentStatus
                itemView.rootView?.txt_upappointment_acceptance?.text =
                    doctorAppointmentList[pos]?.acceptanceStatus
                itemView.rootView?.tv_status?.text =
                    if (doctorAppointmentList[pos]?.acceptanceStatus == "Pending")
                        doctorAppointmentList[pos]?.acceptanceStatus
                    else
                        doctorAppointmentList[pos]?.acceptanceStatus

                itemView.rootView?.btn_video?.visibility = View.GONE

                if (doctorAppointmentList[pos]?.taskDetails != null && !doctorAppointmentList[pos]?.taskDetails?.testName.equals(
                        ""
                    )
                ) {
                    itemView.tv_appointment.text = "Task Name : "
                    itemView.txt_appointment_time.text =
                        doctorAppointmentList[pos]!!.taskDetails?.testName
                }


            }

        }

        private fun formatDateFromString(
            inputFormat: String,
            outputFormat: String,
            inputDate: String
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