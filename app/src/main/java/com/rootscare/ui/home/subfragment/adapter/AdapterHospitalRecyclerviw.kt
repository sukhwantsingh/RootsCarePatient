package com.rootscare.ui.home.subfragment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rootscare.R
import com.rootscare.data.model.api.response.patienthome.HospitalItem
import com.rootscare.databinding.ItemHomeHospitalRecyclerviewBinding
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_home_hospital_recyclerview.view.*

class AdapterHospitalRecyclerviw (val hospitalList: ArrayList<HospitalItem?>?,internal var context: Context) : RecyclerView.Adapter<AdapterHospitalRecyclerviw.ViewHolder>() {
//    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }
    internal lateinit var recyclerViewItemClickWithView: OnItemClikWithIdListener

//    internal lateinit var recyclerViewItemClick: ItemStudyMaterialRecyclerviewOnItemClick
//
//    internal lateinit var recyclerViewItemClickWithView: RecyclerViewItemClickWithView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding = DataBindingUtil.inflate<ItemHomeHospitalRecyclerviewBinding>(
            LayoutInflater.from(context),
            R.layout.item_home_hospital_recyclerview, parent, false)
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return hospitalList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemHomeHospitalRecyclerviewBinding) : RecyclerView.ViewHolder(itemView.root) {

        private var local_position:Int = 0
        init {

                itemView?.root?.crdview_order_doctor?.setOnClickListener(View.OnClickListener {
                    recyclerViewItemClickWithView?.onItemClick(hospitalList?.get(local_position)?.userId?.toInt()!!)
                })


        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            local_position = pos
            itemView?.rootView?.txt_home_hospital_name?.setText(hospitalList?.get(pos)?.name)
            itemView?.rootView?.txt_home_hospital_address?.setText(hospitalList?.get(pos)?.address)
            Glide.with(context)
                .load(BaseMediaUrls.USERIMAGE.url + (hospitalList?.get(pos)?.image))
                .into(itemView?.rootView?.img_home_hospitalprofile!!)



        }
    }

}