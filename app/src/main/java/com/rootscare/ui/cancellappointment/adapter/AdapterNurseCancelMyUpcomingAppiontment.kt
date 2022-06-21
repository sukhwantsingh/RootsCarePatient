package com.rootscare.ui.cancellappointment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.R
import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem
import com.rootscare.databinding.ItemCancellAppointmentBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_cancell_appointment.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AdapterNurseCancelMyUpcomingAppiontment(
    val nurseAppointmentList: ArrayList<AppointmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterNurseCancelMyUpcomingAppiontment.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    internal lateinit var recyclerViewItemClickWithView: OnItemClikWithIdListener
    var fromtime = ""
    var totime = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemCancellAppointmentBinding>(
                LayoutInflater.from(context),
                R.layout.item_cancell_appointment, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
//        return trainerList!!.size
        return nurseAppointmentList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemCancellAppointmentBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            if (nurseAppointmentList?.get(pos)?.orderId != null && !nurseAppointmentList[pos]?.orderId.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_appointment_name?.text =
                    nurseAppointmentList[pos]?.orderId
            } else {
                itemView.rootView?.txt_appointment_name?.text = ""
            }

            if (nurseAppointmentList?.get(pos)?.patientName != null && !nurseAppointmentList[pos]?.patientName.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_patient_name?.text =
                    nurseAppointmentList[pos]?.patientName
            } else {
                itemView.rootView?.txt_cancelappointment_patient_name?.text = ""
            }
            itemView.rootView?.txt_header?.text = "Nurse Name :"
            if (nurseAppointmentList?.get(pos)?.name != null && !nurseAppointmentList[pos]?.name.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_doctor_name?.text =
                    nurseAppointmentList[pos]?.name
            } else {
                itemView.rootView?.txt_cancelappointment_doctor_name?.text = ""
            }

            if (nurseAppointmentList?.get(pos)?.bookingDate != null && !nurseAppointmentList[pos]?.bookingDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_booking_date?.text = formatDateFromString(
                    "yyyy-MM-dd",
                    "dd MMM yyyy",
                    nurseAppointmentList[pos]?.bookingDate
                )
            } else {
                itemView.rootView?.txt_cancelappointment_booking_date?.text = ""
            }
            if (nurseAppointmentList?.get(pos)?.appointmentDate != null && !nurseAppointmentList[pos]?.appointmentDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_appointment_date?.text =
                    formatDateFromString(
                        "yyyy-MM-dd",
                        "dd MMM yyyy",
                        nurseAppointmentList[pos]?.appointmentDate
                    )
            } else {
                itemView.rootView?.txt_cancelappointment_appointment_date?.text = ""
            }

            if (nurseAppointmentList?.get(pos)?.patientContact != null && !nurseAppointmentList[pos]?.patientContact.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_phone_no?.text =
                    nurseAppointmentList[pos]?.patientContact
            } else {
                itemView.rootView?.txt_cancelappointment_phone_no?.text = ""
            }
//            itemView.rootView?.txt_appointment_status?.text =
//                nurseAppointmentList?.get(pos)?.appointmentStatus
//            itemView.rootView?.txt_appointment_acceptance?.text =
//                nurseAppointmentList?.get(pos)?.acceptanceStatus
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
                if (nurseAppointmentList?.get(pos)?.toTime != null && !nurseAppointmentList[pos]?.toTime.equals(
                        ""
                    )
                ) {
                    nurseAppointmentList[pos]?.toTime!!
                } else {
                    ""
                }
            itemView.rootView?.txt_appointmentcancel_time?.text = "$fromtime-$totime"
        }

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

