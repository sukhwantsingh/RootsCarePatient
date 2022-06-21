package com.rootscare.ui.myupcomingappointment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.data.model.api.response.appointmenthistoryresponse.AppointmentItem
import com.rootscare.data.model.api.response.appointmenthistoryresponse.BabysitterAppointmentItem
import com.rootscare.databinding.ItemMyUpcomingAppointmentListNewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.*
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.tv_status
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_appointment_date
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_booking_date
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_doctor_name
import kotlinx.android.synthetic.main.item_my_upcoming_appointment_list_new.view.txt_patient_name
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AdapteBabySitterUpComingAppointment(
    val babysitterAppointmentList: ArrayList<AppointmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapteBabySitterUpComingAppointment.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
    var fromtime = ""
    var totime = ""
    internal lateinit var recyclerViewItemClickWithView: OnUpcomingAppointmentButtonClickListener

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
        return babysitterAppointmentList?.size!!
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
                    babysitterAppointmentList?.get(localPosition)!!, localPosition.toString()
                )
            }

            itemView.root.btn_appointment_reschedule?.setOnClickListener {
                recyclerViewItemClickWithView.onRescheduleBtnClick(
                    babysitterAppointmentList?.get(localPosition)!!, localPosition.toString()
                )
            }
            itemView.root.btnViewDetails?.setOnClickListener {
                recyclerViewItemClickWithView.onViewDetailsClick(
                    babysitterAppointmentList?.get(localPosition)!!, localPosition.toString()
                )
            }

        }

//        init {
//
//            itemView.root.btn_appointment_cancel?.setOnClickListener {
//                recyclerViewItemClickWithView.onItemClick(
//                    babysitterAppointmentList?.get(
//                        localPosition
//                    )?.id?.toInt()!!
//                )
//            }
//            itemView?.root?.crdview_appoitment_list?.setOnClickListener(View.OnClickListener {
//                recyclerViewItemClickWithView?.onItemClick(1)
//            })
//            itemView?.root?.btn_view_trainner_profile?.setOnClickListener(View.OnClickListener {
//                recyclerViewItemClickWithView?.onItemClick(trainerList?.get(local_position)?.id?.toInt()!!)
//            })

//            itemView.root?.img_bid?.setOnClickListener {
//                run {
//                    recyclerViewItemClick?.onClick(itemView.root?.img_bid,local_position)
//                    //serviceListItem?.get(local_position)?.requestid?.let { it1 -> recyclerViewItemClick.onClick(itemView.root?.img_bid,it1) }
//                }
//            }
//
//            itemView.root?.img_details?.setOnClickListener {
//                run {
//                    recyclerViewItemClick?.onClick(itemView.root?.img_details,local_position)
//                    // serviceListItem?.get(local_position)?.requestid?.let { it1 -> recyclerViewItemClick.onClick(itemView.root?.img_details,it1) }
//                }
//            }


//        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            when {
                babysitterAppointmentList?.get(pos)?.acceptanceStatus.equals("Pending") -> {
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
                babysitterAppointmentList?.get(pos)?.acceptanceStatus.equals("Rejected") -> {
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

            if (babysitterAppointmentList?.get(pos)?.orderId != null && !babysitterAppointmentList[pos]?.orderId.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_upcomming_appointment?.text =
                    babysitterAppointmentList[pos]?.orderId
            } else {
                itemView.rootView?.txt_upcomming_appointment?.text = ""
            }

            if (babysitterAppointmentList?.get(pos)?.patientName != null && !babysitterAppointmentList[pos]?.patientName.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_patient_name?.text =
                    babysitterAppointmentList[pos]?.patientName
            } else {
                itemView.rootView?.txt_patient_name?.text = ""
            }
            itemView.rootView?.txt_name_header?.text = "Babysitter Name :"
            if (babysitterAppointmentList?.get(pos)?.name != null && !babysitterAppointmentList[pos]?.name.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_doctor_name?.text =
                    babysitterAppointmentList[pos]?.name
            } else {
                itemView.rootView?.txt_doctor_name?.text = ""
            }

            if (babysitterAppointmentList?.get(pos)?.bookingDate != null && !babysitterAppointmentList[pos]?.bookingDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_booking_date?.text = formatDateFromString(
                    "yyyy-MM-dd", "dd MMM yyyy",
                    babysitterAppointmentList[pos]?.bookingDate
                )
            } else {
                itemView.rootView?.txt_booking_date?.text = ""
            }

            if (babysitterAppointmentList?.get(pos)?.patientContact != null && !babysitterAppointmentList[pos]?.patientContact.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_upcoming_appointmentphone_no?.text =
                    babysitterAppointmentList[pos]?.patientContact
            } else {
                itemView.rootView?.txt_upcoming_appointmentphone_no?.text = ""
            }

            if (babysitterAppointmentList?.get(pos)?.appointmentDate != null && !babysitterAppointmentList[pos]?.appointmentDate.equals(
                    ""
                )
            ) {
                itemView.rootView?.txt_appointment_date?.text =
                    formatDateFromString(
                        "yyyy-MM-dd", "dd MMM yyyy",
                        babysitterAppointmentList[pos]?.appointmentDate
                    )
            } else {
                itemView.rootView?.txt_appointment_date?.text = ""
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
            itemView.rootView?.txt_appointment_time?.text = "$fromtime-$totime"
            itemView.rootView?.txt_upappointment_status?.text =
                babysitterAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.txt_upappointment_acceptance?.text =
                babysitterAppointmentList?.get(pos)?.acceptanceStatus
//            itemView.rootView?.tv_status?.text =
//                babysitterAppointmentList?.get(pos)?.appointmentStatus
            itemView.rootView?.tv_status?.text =
                if (babysitterAppointmentList?.get(pos)?.acceptanceStatus == "Pending")
                    babysitterAppointmentList[pos]?.acceptanceStatus
                else babysitterAppointmentList?.get(pos)?.acceptanceStatus

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

    interface OnUpcomingAppointmentButtonClickListener {
        fun onCancelBtnClick(
            modelDoctorAppointmentItem: AppointmentItem, clickPosition: String
        )

        fun onRescheduleBtnClick(
            modelDoctorAppointmentItem: AppointmentItem, clickPosition: String
        )

        fun onViewDetailsClick(
            modelDoctorAppointmentItem: AppointmentItem, position: String
        )
    }

}