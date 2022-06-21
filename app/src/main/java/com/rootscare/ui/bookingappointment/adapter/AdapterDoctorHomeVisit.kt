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
import com.rootscare.R
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorHomeSlotResponse.Slot
import com.rootscare.databinding.ItemNursetimingRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_nursetiming_recyclerview.view.*
import java.util.*

class AdapterDoctorHomeVisit(
    var resultItem: LinkedList<Slot> = LinkedList(),
//    val resultItem: ArrayList<Slot?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterDoctorHomeVisit.ViewHolder>() {
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    val sdk = Build.VERSION.SDK_INT

    var selectedPosition = -1
    internal lateinit var recyclerViewItemClickWithView: OnDoctorHomeVisitSlotClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemNursetimingRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_nursetiming_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return resultItem.size
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
                recyclerViewItemClickWithView.onSlotClick(resultItem[localPosition])
            }

        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos
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
            } else {
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

            itemView.rootView?.setBackgroundColor(Color.parseColor("#ffffff"))

            itemView.rootView?.txt_nurse_view_timings?.text = resultItem[pos].timeFrom

//            if (slotList?.get(pos)?.status.equals("Available")) {
//                itemView.rootView?.ll_nurse_slot_time?.alpha = 1.0F
//                itemView.rootView?.ll_nurse_slot_time?.isClickable = true
//            } else if (slotList?.get(pos)?.status.equals("Booked")) {
//                itemView.rootView?.ll_nurse_slot_time?.alpha = 0.4F
//                itemView.rootView?.ll_nurse_slot_time?.isClickable = false
//            }

        }
    }

    interface OnDoctorHomeVisitSlotClickListener {
        fun onSlotClick(position: Slot)
    }

}