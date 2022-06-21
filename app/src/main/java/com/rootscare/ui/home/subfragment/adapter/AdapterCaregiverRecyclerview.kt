package com.rootscare.ui.home.subfragment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rootscare.R
import com.rootscare.data.model.api.response.patienthome.CaregiverItem
import com.rootscare.databinding.ItemHomeCaregiverRecyclerviewlistBinding
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_home_caregiver_recyclerviewlist.view.*

class AdapterCaregiverRecyclerview(
    val caregiverList: ArrayList<CaregiverItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterCaregiverRecyclerview.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    //    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
    internal lateinit var recyclerViewRootCareCareGiverList: OnItemClikWithIdListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemHomeCaregiverRecyclerviewlistBinding>(
                LayoutInflater.from(context),
                R.layout.item_home_caregiver_recyclerviewlist, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return caregiverList!!.size
//        return 3
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemHomeCaregiverRecyclerviewlistBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.crdview_caregiver?.setOnClickListener {
                recyclerViewRootCareCareGiverList.onItemClick(caregiverList?.get(localPosition)?.userId?.toInt()!!)
            }
        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            itemView.rootView?.txt_home_caregiver_name?.text =
                caregiverList?.get(pos)?.firstName + " " + caregiverList?.get(pos)?.lastName
            itemView.rootView?.txt_home_caregiver_qualification?.text =
                caregiverList?.get(pos)?.qualification
            Glide.with(context)
                .load(BaseMediaUrls.USERIMAGE.url + (caregiverList?.get(pos)?.image))
                .into(itemView.rootView?.img_caregiver_profile!!)


        }
    }

}