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
import com.rootscare.data.model.api.response.patienthome.BabysitterItem
import com.rootscare.databinding.ItemHomeBabysitterRecyclerviewlistBinding
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.utilitycommon.BaseMediaUrls
import kotlinx.android.synthetic.main.item_home_babysitter_recyclerviewlist.view.*

class AdapterbabysitterRecyclerview (val babysitterList: ArrayList<BabysitterItem?>?, internal var context: Context) : RecyclerView.Adapter<AdapterbabysitterRecyclerview.ViewHolder>() {
    //    val trainerList: ArrayList<TrainerListItem?>?,
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    internal lateinit var recyclerViewItemClick: OnItemClikWithIdListener
//
//    internal lateinit var recyclerViewItemClickWithView: RecyclerViewItemClickWithView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding = DataBindingUtil.inflate<ItemHomeBabysitterRecyclerviewlistBinding>(
            LayoutInflater.from(context),
            R.layout.item_home_babysitter_recyclerviewlist, parent, false)
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return babysitterList!!.size
//        return 3
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemHomeBabysitterRecyclerviewlistBinding) : RecyclerView.ViewHolder(itemView.root) {

        private var local_position:Int = 0
        init {
            itemView?.root?.crdview_babysitter?.setOnClickListener(View.OnClickListener {
                recyclerViewItemClick?.onItemClick(babysitterList?.get(local_position)?.userId?.toInt()!!)
            })





        }

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            local_position = pos

            itemView?.rootView?.txt_home_babysitter_name?.setText(babysitterList?.get(pos)?.firstName+" "+babysitterList?.get(pos)?.lastName)
            itemView?.rootView?.txt_home_babysitter_qualification?.setText(babysitterList?.get(pos)?.qualification)
            Glide.with(context)
                .load(BaseMediaUrls.USERIMAGE.url + (babysitterList?.get(pos)?.image))
                .into(itemView?.rootView?.img_babysitter_profile!!)




        }
    }

}