package com.rootscare.ui.nurses.nurseslistingdetails.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.data.model.api.response.nurses.nursedetails.QualificationDataItem
import com.rootscare.databinding.ItemDoctorldetailsEducationlistRecyclerviewBinding
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_doctorldetails_educationlist_recyclerview.view.*

class AdapterNurseEducationListRecyclerView (val qualificationDataList: ArrayList<QualificationDataItem?>?, internal var context: Context) : RecyclerView.Adapter<AdapterNurseEducationListRecyclerView.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding = DataBindingUtil.inflate<ItemDoctorldetailsEducationlistRecyclerviewBinding>(
            LayoutInflater.from(context),
            R.layout.item_doctorldetails_educationlist_recyclerview, parent, false)
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return qualificationDataList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemDoctorldetailsEducationlistRecyclerviewBinding) : RecyclerView.ViewHolder(itemView.root) {

        private var local_position:Int = 0
        init {
        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            local_position = pos
            itemView?.rootView?.txt_doctordetails_qualification?.setText(qualificationDataList?.get(pos)?.qualification)
            itemView?.rootView?.txt_doctordetails_institute_name?.setText("Institute:"+" "+qualificationDataList?.get(pos)?.institute)
            itemView?.rootView?.txt_doctordetails_passingyear?.setText("Passing year:"+" "+qualificationDataList?.get(pos)?.passingYear)
        }
    }

}