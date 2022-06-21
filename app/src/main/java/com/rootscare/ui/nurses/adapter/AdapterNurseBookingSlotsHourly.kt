package com.rootscare.ui.nurses.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.GetSlotTimingInterface
import com.interfaces.OnClickWithTwoButton
import com.model.SlotModel
import com.rootscare.R
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.ResultItem
import com.rootscare.databinding.ItemNurseHourlySlotBinding
import com.rootscare.databinding.ItemNurseTaskSlotsBinding
import com.rootscare.databinding.ItemNursetimingRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import com.rootscare.ui.nurses.nursesbookingappointment.FragmentNursesBookingAppointment
import com.rootscare.ui.nurses.nursesbookingappointment.FragmentNursesBookingAppointment.Companion.mySlotListTemp
import com.rootscare.utils.FileUtil
import com.rootscare.utils.ViewUtils.addThirtyMin
import com.rootscare.utils.ViewUtils.changeIconDrawableToGray
import kotlinx.android.synthetic.main.item_nursetiming_recyclerview.view.*

class AdapterNurseBookingSlotsHourly(
    val nurseTimingList: ArrayList<SlotModel>,
    internal var context: Context, var getslottiminginterface: GetSlotTimingInterface
) : RecyclerView.Adapter<AdapterNurseBookingSlotsHourly.ViewHolder>() {
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    var isChecked = false


    internal lateinit var recyclerViewItemClickWithView: OnClickWithTwoButton

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding = DataBindingUtil.inflate<ItemNurseTaskSlotsBinding>(
            LayoutInflater.from(context),
            R.layout.item_nurse_task_slots, parent, false
        )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
//        return hourlyRatesList?.size!!
        return nurseTimingList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //    holder.onBind(position)

        holder.itemView.txt_nurse_view_timings?.setText(nurseTimingList.get(position).time)

        //set hightlighted


        if (nurseTimingList.get(position).isvalid) {

            if (nurseTimingList.get(position).isSelected) {
                holder.itemView.txt_nurse_view_timings?.background =
                    ContextCompat.getDrawable(context, R.drawable.rounded_blue_broder_filled)

                holder.itemView.txt_nurse_view_timings?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.white
                    )
                )
            } else {
                holder.itemView.txt_nurse_view_timings?.background =
                    ContextCompat.getDrawable(context, R.drawable.rounded_blue_broder)

                holder.itemView.txt_nurse_view_timings?.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.txt_color
                    )
                )
            }
        } else {

            holder.itemView.txt_nurse_view_timings?.background =
                ContextCompat.getDrawable(context, R.drawable.rounded_blue_broder_disabled)

            holder.itemView.txt_nurse_view_timings?.setTextColor(
                ContextCompat.getColor(
                    context,
                    R.color.txt_color
                )
            )

        }


        holder.itemView.txt_nurse_view_timings?.setOnClickListener {


            if (nurseTimingList.get(position).isvalid) {
                highLIghtView(position)
                getslottiminginterface.onClickSlot(
                    nurseTimingList.get(position).time,
                    addThirtyMin(nurseTimingList.get(position).time,nurseTimingList.get(position).timetoAdd)
                )
            }
        }

    }

    fun highLIghtView(position: Int) {
        for (i in 0 until nurseTimingList.size) {
            if (position == i) {
                nurseTimingList.get(i).isSelected = true
                /*   nurseTimingList.get(i).time=
                   (nurseTimingList.get(position).time +"-"+addThirtyMin(nurseTimingList.get(position).time))
                 */

            } else {
                nurseTimingList.get(i).isSelected = false

                //   nurseTimingList.get(i).time= FragmentNursesBookingAppointment.mySlotListTemp.get(i).time

            }
        }
        notifyDataSetChanged()

    }

    inner class ViewHolder(itemView: ItemNurseTaskSlotsBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var local_position: Int = 0

        init {
//
        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            local_position = pos


//            if(hourlyRatesList?.get(pos)?.duration!=null && !hourlyRatesList?.get(pos)?.duration.equals("")){
//                itemView?.rootView?.txt_nurse_hourtext?.setText("For"+" "+hourlyRatesList?.get(pos)?.duration)
//            }else{
//                itemView?.rootView?.txt_nurse_hourtext?.setText("")
//            }
//
//            if (hourlyRatesList?.get(pos)?.price!=null && !hourlyRatesList?.get(pos)?.price.equals("")){
//                itemView?.rootView?.txt_nurse_hourvalue?.setText("SR"+hourlyRatesList?.get(pos)?.price)
//            }else{
//                itemView?.rootView?.txt_nurse_hourvalue?.setText("")
//            }
        }
    }

}