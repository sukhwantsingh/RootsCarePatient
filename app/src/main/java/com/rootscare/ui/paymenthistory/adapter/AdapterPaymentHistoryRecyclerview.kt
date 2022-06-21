package com.rootscare.ui.paymenthistory.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.data.model.api.response.paymenthistoryresponse.ResultItem
import com.rootscare.databinding.ItemRootscarePaymentHistoryRecyclerviewBinding
import com.interfaces.OnItemClikWithIdListener
import com.rootscare.ui.home.subfragment.adapter.AdapterHospitalRecyclerviw
import kotlinx.android.synthetic.main.item_rootscare_payment_history_recyclerview.view.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AdapterPaymentHistoryRecyclerview(
    val paymentHistoryList: ArrayList<ResultItem?>?, internal var context: Context
) : RecyclerView.Adapter<AdapterPaymentHistoryRecyclerview.ViewHolder>() {
    companion object {
        val TAG: String = AdapterHospitalRecyclerviw::class.java.simpleName
    }

    internal lateinit var recyclerViewItemClickWithView: OnItemClikWithIdListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<ItemRootscarePaymentHistoryRecyclerviewBinding>(
                LayoutInflater.from(context),
                R.layout.item_rootscare_payment_history_recyclerview, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return paymentHistoryList!!.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(itemView: ItemRootscarePaymentHistoryRecyclerviewBinding) :
        RecyclerView.ViewHolder(itemView.root) {

        private var localPosition: Int = 0

        fun onBind(pos: Int) {
            Log.d(TAG, " true")
            localPosition = pos
            if (localPosition == 0) {
                itemView.rootView?.ll_header?.visibility = View.VISIBLE
                itemView.rootView?.view_header?.visibility = View.VISIBLE
            } else {
                itemView.rootView?.ll_header?.visibility = View.GONE
                itemView.rootView?.view_header?.visibility = View.GONE
            }

            itemView.rootView?.txt_payment_id?.text = paymentHistoryList?.get(pos)?.orderId
            itemView.rootView?.txt_payment_date?.text = formatDateFromString(
                "yyyy-MM-dd",
                "dd/MM/yyyy",
                paymentHistoryList?.get(pos)?.date
            )
            itemView.rootView?.txt_payment_amount?.text = paymentHistoryList?.get(pos)?.amount
            if (paymentHistoryList?.get(pos)?.paymentType.equals("Offline")) {
                itemView.rootView?.txt_payment_type?.text = "COD"
            } else {
                itemView.rootView?.txt_payment_type?.text =
                    paymentHistoryList?.get(pos)?.paymentType
            }
            itemView.rootView?.txt_payment_status?.text =
                paymentHistoryList?.get(pos)?.paymentStatus


//            itemView?.rootView?.txt_rating_count?.text="("+contactListItem?.get(pos)?.contactRating+")"
//            (contactListItem?.get(pos)?.contactRating)?.toFloat()?.let { itemView?.rootView?.ratingBar?.setRating(it) }
////            itemView?.rootView?.ratingBar?.rating=1.5f


        }
    }


    fun formatDateFromString(
        inputFormat: String?,
        outputFormat: String?,
        inputDate: String?
    ): String {
        val parsed: Date?
        var outputDate = ""
        val dfInput =
            SimpleDateFormat(inputFormat, Locale.ENGLISH)
        val dfOutput =
            SimpleDateFormat(outputFormat, Locale.ENGLISH)
        try {
            parsed = dfInput.parse(inputDate)
            outputDate = dfOutput.format(parsed)
        } catch (e: ParseException) {
        }
        return outputDate
    }

}