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

class AdapterBabySitterCancelMyUpcomingAppiontment(
    val babysitterAppointmentList: ArrayList<AppointmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterBabySitterCancelMyUpcomingAppiontment.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick

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
        return babysitterAppointmentList?.size!!
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

            if (babysitterAppointmentList?.get(pos)?.orderId != null && !babysitterAppointmentList[pos]?.orderId.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_appointment_name?.text =
                    babysitterAppointmentList[pos]?.orderId
            } else {
                itemView.rootView?.txt_appointment_name?.text = ""
            }

            if (babysitterAppointmentList?.get(pos)?.patientName != null && !babysitterAppointmentList[pos]?.patientName.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_patient_name?.text =
                    babysitterAppointmentList[pos]?.patientName
            } else {
                itemView.rootView?.txt_cancelappointment_patient_name?.text = ""
            }
            itemView.rootView?.txt_header?.text = "Babysitter Name :"
            if (babysitterAppointmentList?.get(pos)?.name != null && !babysitterAppointmentList[pos]?.name.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_doctor_name?.text =
                    babysitterAppointmentList[pos]?.name
            } else {
                itemView.rootView?.txt_cancelappointment_doctor_name?.text = ""
            }

            if (babysitterAppointmentList?.get(pos)?.bookingDate != null && !babysitterAppointmentList[pos]?.bookingDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_booking_date?.text =
                    formatDateFromString(
                        "yyyy-MM-dd", "dd MMM yyyy",
                        babysitterAppointmentList[pos]?.bookingDate
                    )
            } else {
                itemView.rootView?.txt_cancelappointment_booking_date?.text = ""
            }
            if (babysitterAppointmentList?.get(pos)?.appointmentDate != null && !babysitterAppointmentList[pos]?.appointmentDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_appointment_date?.text =
                    formatDateFromString(
                        "yyyy-MM-dd", "dd MMM yyyy",
                        babysitterAppointmentList[pos]?.appointmentDate
                    )
            } else {
                itemView.rootView?.txt_cancelappointment_appointment_date?.text = ""
            }
            fromtime =
                if (babysitterAppointmentList?.get(pos)?.fromTime != null && !babysitterAppointmentList[pos]?.fromTime.equals(
                        ""
                    )
                ) {
                    babysitterAppointmentList[pos]?.fromTime!!
                } else {
                    ""
                }
            totime =
                if (babysitterAppointmentList?.get(pos)?.toTime != null && !babysitterAppointmentList[pos]?.toTime.equals(
                        ""
                    )
                ) {
                    babysitterAppointmentList[pos]?.toTime!!
                } else {
                    ""
                }
            itemView.rootView?.txt_appointmentcancel_time?.text = "$fromtime-$totime"

            if (babysitterAppointmentList?.get(pos)?.patientContact != null && !babysitterAppointmentList[pos]?.patientContact.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_phone_no?.text =
                    babysitterAppointmentList[pos]?.patientContact
            } else {
                itemView.rootView?.txt_cancelappointment_phone_no?.text = ""
            }

//            itemView.rootView?.txt_appointment_status?.text =
//                babysitterAppointmentList?.get(pos)?.appointmentStatus
//            itemView.rootView?.txt_appointment_acceptance?.text =
//                babysitterAppointmentList?.get(pos)?.acceptanceStatus

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