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

class AdapterAllCancelAppointment(
    val doctorAppointmentList: ArrayList<AppointmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterAllCancelAppointment.ViewHolder>() {
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
        return doctorAppointmentList?.size!!
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

            if (doctorAppointmentList?.get(pos)?.name != null && !doctorAppointmentList[pos]?.name.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_doctor_name?.text =
                    doctorAppointmentList[pos]?.name
            } else {
                itemView.rootView?.txt_cancelappointment_doctor_name?.text = ""
            }

            if (doctorAppointmentList?.get(pos)?.userType == "doctor") {
                itemView.rootView?.txt_header?.text = "Doctor Name : "
            } else if (doctorAppointmentList?.get(pos)?.userType == "nurse") {
                itemView.rootView?.txt_header?.text = "Nurse Name : "
            } else if (doctorAppointmentList?.get(pos)?.userType == "hospital_doctor") {
                itemView.rootView?.txt_header?.text = "Hospital Name : "
                if (doctorAppointmentList[pos]?.hospitalName != null && !doctorAppointmentList[pos]?.hospitalName.equals(
                        ""
                    )
                ) {
                    itemView.rootView?.txt_cancelappointment_doctor_name?.text =
                        doctorAppointmentList[pos]?.hospitalName
                } else {
                    itemView.rootView?.txt_cancelappointment_doctor_name?.text = ""
                }
            } else if (doctorAppointmentList?.get(pos)?.userType == "caregiver") {
                itemView.rootView?.txt_header?.text = "Caregiver Name : "
            } else if (doctorAppointmentList?.get(pos)?.userType == "babysitter") {
                itemView.rootView?.txt_header?.text = "Babysitter Name : "
            } else if (doctorAppointmentList?.get(pos)?.userType == "pathology") {
                itemView.rootView?.txt_header?.text = "Hospital Name : "
                if (doctorAppointmentList[pos]?.hospitalName != null && !doctorAppointmentList[pos]?.hospitalName.equals(
                        ""
                    )
                ) {
                    itemView.rootView?.txt_cancelappointment_doctor_name?.text =
                        doctorAppointmentList[pos]?.hospitalName
                } else {
                    itemView.rootView?.txt_cancelappointment_doctor_name?.text = ""
                }

                if (doctorAppointmentList[pos]?.pathologyName != null && !doctorAppointmentList[pos]?.pathologyName.equals(
                        ""
                    )
                ) {
                    itemView.tv_appointment.text = "Test Name : "
                    itemView.txt_appointmentcancel_time.text = doctorAppointmentList[pos]!!.pathologyName
                }
            } else if (doctorAppointmentList?.get(pos)?.userType == "physiotherapy") {
                itemView.rootView?.txt_header?.text = "Physiotherapy Name : "
            }

            if (doctorAppointmentList?.get(pos)?.orderId != null && !doctorAppointmentList[pos]?.orderId.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_appointment_name?.text =
                    doctorAppointmentList[pos]?.orderId
            } else {
                itemView.rootView?.txt_appointment_name?.text = ""
            }

            if (doctorAppointmentList?.get(pos)?.patientName != null && !doctorAppointmentList[pos]?.patientName.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_patient_name?.text =
                    doctorAppointmentList[pos]?.patientName
            } else {
                itemView.rootView?.txt_cancelappointment_patient_name?.text = ""
            }

//            if(doctorAppointmentList?.get(pos)?.doctorName!=null && !doctorAppointmentList?.get(pos)?.doctorName.equals("")){
//                itemView?.rootView?.txt_cancelappointment_booking_date?.setText(doctorAppointmentList?.get(pos)?.doctorName)
//            }else{
//                itemView?.rootView?.txt_cancelappointment_booking_date?.setText("")
//            }
            if (doctorAppointmentList?.get(pos)?.bookingDate != null && !doctorAppointmentList[pos]?.bookingDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_booking_date?.text =
                    formatDateFromString(
                        "yyyy-MM-dd", "dd MMM yyyy",
                        doctorAppointmentList[pos]?.bookingDate
                    )
            } else {
                itemView.rootView?.txt_cancelappointment_booking_date?.text = ""
            }
            if (doctorAppointmentList?.get(pos)?.appointmentDate != null && !doctorAppointmentList[pos]?.appointmentDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_appointment_date?.text =
                    formatDateFromString(
                        "yyyy-MM-dd", "dd MMM yyyy",
                        doctorAppointmentList[pos]?.appointmentDate
                    )
            } else {
                itemView.rootView?.txt_cancelappointment_appointment_date?.text = ""
            }

            if (doctorAppointmentList?.get(pos)?.patientContact != null && !doctorAppointmentList[pos]?.patientContact.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_phone_no?.text =
                    doctorAppointmentList[pos]?.patientContact
            } else {
                itemView.rootView?.txt_cancelappointment_phone_no?.text = ""
            }
//            itemView.rootView?.txt_appointment_status?.text =
//                doctorAppointmentList?.get(pos)?.appointmentStatus
//            itemView.rootView?.txt_appointment_acceptance?.text =
//                doctorAppointmentList?.get(pos)?.acceptanceStatus
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
            itemView.rootView?.txt_appointmentcancel_time?.text = "$fromtime-$totime"

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