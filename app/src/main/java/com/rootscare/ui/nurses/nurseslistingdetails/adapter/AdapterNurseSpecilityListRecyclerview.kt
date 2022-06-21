package com.rootscare.ui.nurses.nurseslistingdetails.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.data.model.api.response.nurses.nursedetails.DepartmentItem
import com.rootscare.databinding.ItemDoctorldetailsSpecilitylistRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_doctorldetails_specilitylist_recyclerview.view.*

class AdapterNurseSpecilityListRecyclerview(
    val departmentList: ArrayList<DepartmentItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterNurseSpecilityListRecyclerview.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

//    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
//    internal lateinit var recyclerViewItemClickWithView: RecyclerViewItemClickWithView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemDoctorldetailsSpecilitylistRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_doctorldetails_specilitylist_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return departmentList!!.size
//        return 3
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemDoctorldetailsSpecilitylistRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos
            itemView.rootView?.txt_doctordetails_specility?.text = departmentList?.get(pos)?.title

        }
    }

}