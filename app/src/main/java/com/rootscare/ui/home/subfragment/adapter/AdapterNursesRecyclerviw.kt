package com.rootscare.ui.home.subfragment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.R
import com.rootscare.data.model.api.response.patienthome.NurseItem
import com.rootscare.databinding.ItemHomeNursesRecyclerviewBinding
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_home_nurses_recyclerview.view.*

class AdapterNursesRecyclerviw(
    val nurseList: ArrayList<NurseItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterNursesRecyclerviw.ViewHolder>() {
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    internal lateinit var recyclerViewItemClickWithView: OnItemClikWithIdListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemHomeNursesRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_home_nurses_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return nurseList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemHomeNursesRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.crdview_nurses?.setOnClickListener {
                recyclerViewItemClickWithView.onItemClick(nurseList?.get(localPosition)?.userId?.toInt()!!)
            }
        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos

            itemView.rootView?.txt_home_nurse_name?.text =
                nurseList?.get(pos)?.firstName + " " + nurseList?.get(pos)?.lastName
            itemView.rootView?.txt_home_nurse_qualification?.text =
                nurseList?.get(pos)?.qualification
            Glide.with(context)
                .load(BaseMediaUrls.USERIMAGE.url + (nurseList?.get(pos)?.image))
                .into(itemView.rootView?.img_home_nurseprofile!!)

        }
    }

}