package com.rootscare.ui.bookingappointment.adapter

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.OnDoctorSlotClickListner
import com.rootscare.R
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse.SlotItem
import com.rootscare.databinding.ItemNursetimingRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_nursetiming_recyclerview.view.*

class AdapterDoctorSlotDivision(
    val slotList: ArrayList<SlotItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterDoctorSlotDivision.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    val sdk = Build.VERSION.SDK_INT

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    var selectedPosition = -1
    internal lateinit var recyclerViewItemClickWithView: OnDoctorSlotClickListner

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemNursetimingRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_nursetiming_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
//        return trainerList!!.size
        return slotList?.size!!
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemNursetimingRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.ll_nurse_slot_time?.setOnClickListener {
                selectedPosition = localPosition
                notifyDataSetChanged()
                recyclerViewItemClickWithView.onSloctClick(slotList?.get(localPosition)!!)
            }
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


        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos
//            if (selectedPosition == pos) {
////                itemView?.rootView?.ll_slot?.setBackgroundColor(Color.parseColor("#D2F2F5"))
//
//                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
//                    itemView.rootView?.txt_start_time?.setBackgroundDrawable(
//                        ContextCompat.getDrawable(
//                            context,
//                            R.drawable.border_with_green_bg
//                        )
//                    )
//                    itemView.rootView?.txt_end_time?.background =
//                        ContextCompat.getDrawable(context, R.drawable.border_with_green_bg)
//                } else {
//                    itemView.rootView?.txt_start_time?.background =
//                        ContextCompat.getDrawable(context, R.drawable.border_with_green_bg)
//                    itemView.rootView?.txt_end_time?.background =
//                        ContextCompat.getDrawable(context, R.drawable.border_with_green_bg)
//                }
//            } else {
//                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
//                    itemView.rootView?.txt_start_time?.setBackgroundDrawable(
//                        ContextCompat.getDrawable(
//                            context,
//                            R.drawable.bg_rounded_grey_border
//                        )
//                    )
//                    itemView.rootView?.txt_end_time?.background =
//                        ContextCompat.getDrawable(context, R.drawable.bg_rounded_grey_border)
//                } else {
//                    itemView.rootView?.txt_start_time?.background =
//                        ContextCompat.getDrawable(context, R.drawable.bg_rounded_grey_border)
//                    itemView.rootView?.txt_end_time?.background =
//                        ContextCompat.getDrawable(context, R.drawable.bg_rounded_grey_border)
//                }
////                itemView?.rootView?.ll_slot?.setBackgroundColor(Color.parseColor("#ffffff"))
//            }
            if (selectedPosition == pos) {
                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                    itemView.rootView?.txt_nurse_view_timings?.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.rounded_green_btn
                        )
                    )
                    itemView.rootView?.txt_nurse_view_timings?.background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.rounded_green_btn
                        )
                    itemView.rootView?.txt_nurse_view_timings?.setTextColor(Color.parseColor("#ffffff"))
                } else {
                    itemView.rootView?.txt_nurse_view_timings?.background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.rounded_green_btn
                        )
                    itemView.rootView?.txt_nurse_view_timings?.background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.rounded_green_btn
                        )
                    itemView.rootView?.txt_nurse_view_timings?.setTextColor(Color.parseColor("#ffffff"))
                }
            }
            // itemView?.rootView?.setBackgroundColor(Color.parseColor("#D2F2F5"))
            else {
                if (sdk < Build.VERSION_CODES.JELLY_BEAN) {
                    itemView.rootView?.txt_nurse_view_timings?.setBackgroundDrawable(
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.rounded_blue_broder
                        )
                    )
                    itemView.rootView?.txt_nurse_view_timings?.background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.rounded_blue_broder
                        )
                    itemView.rootView?.txt_nurse_view_timings?.setTextColor(Color.parseColor("#515C6F"))
                } else {
                    itemView.rootView?.txt_nurse_view_timings?.background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.rounded_blue_broder
                        )
                    itemView.rootView?.txt_nurse_view_timings?.background =
                        ContextCompat.getDrawable(
                            context,
                            R.drawable.rounded_blue_broder
                        )
                    itemView.rootView?.txt_nurse_view_timings?.setTextColor(Color.parseColor("#515C6F"))
                }
            }

//            itemView.rootView?.txt_start_time?.text = slotList?.get(pos)?.timeFrom
//            itemView.rootView?.txt_end_time?.text = slotList?.get(pos)?.timeTo

            itemView.rootView?.setBackgroundColor(Color.parseColor("#ffffff"))

            itemView.rootView?.txt_nurse_view_timings?.text = slotList?.get(pos)?.timeFrom

            if (slotList?.get(pos)?.status.equals("Available")) {
                itemView.rootView?.ll_nurse_slot_time?.alpha = 1.0F
                itemView.rootView?.ll_nurse_slot_time?.isClickable = true
            } else if (slotList?.get(pos)?.status.equals("Booked")) {
                itemView.rootView?.ll_nurse_slot_time?.alpha = 0.4F
                itemView.rootView?.ll_nurse_slot_time?.isClickable = false
            }

//            itemView?.rootView?.txt_teacher_name?.text= trainerList?.get(pos)?.name
//            itemView?.rootView?.txt_teacher_qualification?.text= "Qualification : "+" "+trainerList?.get(pos)?.qualification
//            if(trainerList?.get(pos)?.avgRating!=null && !trainerList?.get(pos)?.avgRating.equals("")){
//                itemView?.rootView?.ratingBarteacher?.rating= trainerList?.get(pos)?.avgRating?.toFloat()!!
//            }


//            itemView?.rootView?.txt_rating_count?.text="("+contactListItem?.get(pos)?.contactRating+")"
//            (contactListItem?.get(pos)?.contactRating)?.toFloat()?.let { itemView?.rootView?.ratingBar?.setRating(it) }
////            itemView?.rootView?.ratingBar?.rating=1.5f


        }
    }

}