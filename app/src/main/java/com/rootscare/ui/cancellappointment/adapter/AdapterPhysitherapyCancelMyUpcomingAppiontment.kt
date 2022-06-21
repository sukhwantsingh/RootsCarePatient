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
import com.rootscare.data.model.api.response.appointmenthistoryresponse.PhysiotherapyAppointmentItem
import com.rootscare.databinding.ItemCancellAppointmentBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_appointmentlist_recyclerview.view.*
import kotlinx.android.synthetic.main.item_cancell_appointment.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AdapterPhysitherapyCancelMyUpcomingAppiontment(
    val pathologyAppointmentList: ArrayList<AppointmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterPhysitherapyCancelMyUpcomingAppiontment.ViewHolder>() {
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
        return pathologyAppointmentList?.size!!
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

            if (pathologyAppointmentList?.get(pos)?.orderId != null && !pathologyAppointmentList[pos]?.orderId.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_appointment_name?.text =
                    pathologyAppointmentList[pos]?.orderId
            } else {
                itemView.rootView?.txt_appointment_name?.text = ""
            }

            if (pathologyAppointmentList?.get(pos)?.patientName != null && !pathologyAppointmentList[pos]?.patientName.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_patient_name?.text =
                    pathologyAppointmentList[pos]?.patientName
            } else {
                itemView.rootView?.txt_cancelappointment_patient_name?.text = ""
            }
            itemView.rootView?.txt_header?.text = "Physiotherapist Name :"
            if (pathologyAppointmentList?.get(pos)?.name != null && !pathologyAppointmentList[pos]?.name.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_doctor_name?.text =
                    pathologyAppointmentList[pos]?.name
            } else {
                itemView.rootView?.txt_cancelappointment_doctor_name?.text = ""
            }

            if (pathologyAppointmentList?.get(pos)?.bookingDate != null && !pathologyAppointmentList[pos]?.bookingDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_booking_date?.text = formatDateFromString(
                    "yyyy-MM-dd",
                    "dd MMM yyyy",
                    pathologyAppointmentList[pos]?.bookingDate
                )
            } else {
                itemView.rootView?.txt_cancelappointment_booking_date?.text = ""
            }
            if (pathologyAppointmentList?.get(pos)?.appointmentDate != null && !pathologyAppointmentList[pos]?.appointmentDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_appointment_date?.text =
                    formatDateFromString(
                        "yyyy-MM-dd",
                        "dd MMM yyyy",
                        pathologyAppointmentList[pos]?.appointmentDate
                    )
            } else {
                itemView.rootView?.txt_cancelappointment_appointment_date?.text = ""
            }
            fromtime =
                if (pathologyAppointmentList?.get(pos)?.fromTime != null && !pathologyAppointmentList[pos]?.fromTime.equals(
                        ""
                    )
                ) {
                    pathologyAppointmentList[pos]?.fromTime!!
                } else {
                    ""
                }
            totime =
                if (pathologyAppointmentList?.get(pos)?.toTime != null && !pathologyAppointmentList[pos]?.toTime.equals(
                        ""
                    )
                ) {
                    pathologyAppointmentList[pos]?.toTime!!
                } else {
                    ""
                }
            itemView.rootView?.txt_appointmentcancel_time?.text = "$fromtime-$totime"
            if (pathologyAppointmentList?.get(pos)?.patientContact != null && !pathologyAppointmentList[pos]?.patientContact.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_cancelappointment_phone_no?.text =
                    pathologyAppointmentList[pos]?.patientContact
            } else {
                itemView.rootView?.txt_cancelappointment_phone_no?.text = ""
            }

            itemView.rootView?.txt_appointment_status?.text =
                pathologyAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.txt_appointment_acceptance?.text = pathologyAppointmentList?.get(
                pos
            )?.acceptanceStatus
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