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

class AdapterCaregiverCancelMyUpcomingAppiontment(
    val caregiverAppointmentList: ArrayList<AppointmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterCaregiverCancelMyUpcomingAppiontment.ViewHolder>() {
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
        return caregiverAppointmentList?.size!!
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

            if (caregiverAppointmentList?.get(pos)?.orderId != null && !caregiverAppointmentList[pos]?.orderId.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_appointment_name?.text =
                    caregiverAppointmentList[pos]?.orderId
            } else {
                itemView.rootView?.txt_appointment_name?.text = ""
            }

            if (caregiverAppointmentList?.get(pos)?.patientName != null && !caregiverAppointmentList[pos]?.patientName.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_patient_name?.text =
                    caregiverAppointmentList[pos]?.patientName
            } else {
                itemView.rootView?.txt_cancelappointment_patient_name?.text = ""
            }
            itemView.rootView?.txt_header?.text = "Caregiver Name :"
            if (caregiverAppointmentList?.get(pos)?.name != null && !caregiverAppointmentList[pos]?.name.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_doctor_name?.text =
                    caregiverAppointmentList[pos]?.name
            } else {
                itemView.rootView?.txt_cancelappointment_doctor_name?.text = ""
            }

            if (caregiverAppointmentList?.get(pos)?.bookingDate != null && !caregiverAppointmentList[pos]?.bookingDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_booking_date?.text =
                    formatDateFromString(
                        "yyyy-MM-dd", "dd MMM yyyy",
                        caregiverAppointmentList[pos]?.bookingDate
                    )
            } else {
                itemView.rootView?.txt_cancelappointment_booking_date?.text = ""
            }
            if (caregiverAppointmentList?.get(pos)?.appointmentDate != null && !caregiverAppointmentList[pos]?.appointmentDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_appointment_date?.text =
                    formatDateFromString(
                        "yyyy-MM-dd", "dd MMM yyyy",
                        caregiverAppointmentList[pos]?.appointmentDate
                    )
            } else {
                itemView.rootView?.txt_cancelappointment_appointment_date?.text = ""
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
            itemView.rootView?.txt_appointmentcancel_time?.text = "$fromtime-$totime"
            if (caregiverAppointmentList?.get(pos)?.patientContact != null && !caregiverAppointmentList[pos]?.patientContact.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_phone_no?.text =
                    caregiverAppointmentList[pos]?.patientContact
            } else {
                itemView.rootView?.txt_cancelappointment_phone_no?.text = ""
            }
//            itemView.rootView?.txt_appointment_status?.text =
//                caregiverAppointmentList?.get(pos)?.appointmentStatus
//            itemView.rootView?.txt_appointment_acceptance?.text =
//                caregiverAppointmentList?.get(pos)?.acceptanceStatus

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