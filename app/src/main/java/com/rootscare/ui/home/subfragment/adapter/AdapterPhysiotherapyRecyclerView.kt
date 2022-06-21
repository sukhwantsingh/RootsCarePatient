package com.rootscare.ui.home.subfragment.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rootscare.R
import com.rootscare.data.model.api.response.patienthome.PhysiotherapyItem
import com.rootscare.databinding.ItemHomePhysiotherapyRecyclerviewBinding
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_home_physiotherapy_recyclerview.view.*

class AdapterPhysiotherapyRecyclerView(
    val physiotherapyList: ArrayList<PhysiotherapyItem?>?,
    internal var context: Context
) : RecyclerView.Adapter<AdapterPhysiotherapyRecyclerView.ViewHolder>() {
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    internal lateinit var recyclerViewItemClickWithView: OnItemClickWithIdListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemHomePhysiotherapyRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_home_physiotherapy_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return physiotherapyList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemHomePhysiotherapyRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        init {
            itemView.root.crdview_nurses?.setOnClickListener {
                recyclerViewItemClickWithView.onItemClick(physiotherapyList?.get(localPosition)?.userId?.toInt()!!)
            }
        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos
            itemView.rootView?.txt_home_physiotherapy_name?.text =
                physiotherapyList?.get(pos)?.firstName + " " + physiotherapyList?.get(
                    pos
                )?.lastName
            itemView.rootView?.txt_home_physiotherapy_qualification?.text = physiotherapyList?.get(
                pos
            )?.qualification
            Glide.with(context)
                .load(
                    BaseMediaUrls.USERIMAGE.url + (physiotherapyList?.get(
                        pos
                    )?.image)
                )
                .into(itemView.rootView?.img_home_pathologyprofile!!)
        }
    }

    interface OnItemClickWithIdListener {
        fun onItemClick(id: Int)

    }

}