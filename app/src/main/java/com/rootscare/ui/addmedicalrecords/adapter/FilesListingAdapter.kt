package com.rootscare.ui.addmedicalrecords.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.interfaces.RecyclerViewItemClick
import com.rootscare.R
import com.rootscare.data.model.api.response.addmedicallrecords.AddlabTestImageSelectionModel
import com.rootscare.databinding.SingleItemFileListingBinding
import java.util.*

class FilesListingAdapter(internal var activity: Activity) :
    RecyclerView.Adapter<FilesListingAdapter.ViewHolder>() {


    companion object {
        val TAG: String = FilesListingAdapter::class.java.simpleName
    }

    private var initiallyDoNotOpenKeyboardIfAnyCheckboxIsChecked = false
    var booleanIsFocusOn = true
    internal lateinit var recyclerViewItemClick: RecyclerViewItemClick
    var data: LinkedList<AddlabTestImageSelectionModel> = LinkedList()
        //    var data: LinkedList<String> = LinkedList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val singleItemDashboardListingBinding =
            DataBindingUtil.inflate<SingleItemFileListingBinding>(
                LayoutInflater.from(activity),
                R.layout.single_item_file_listing, parent, false
            )
        return ViewHolder(singleItemDashboardListingBinding)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(position)
    }


    inner class ViewHolder(var itemVie: SingleItemFileListingBinding) :
        RecyclerView.ViewHolder(itemVie.root) {

        private var local_position: Int = 0

        fun onBind(myPosition: Int) {
            local_position = adapterPosition
            with(itemVie) {
                imgDelete.setOnClickListener {
                    recyclerViewItemClick.onItemClick(local_position)
                }
                if (data[local_position].type != null) {
                    if (data[local_position].type?.trim()?.toLowerCase(Locale.getDefault())
                            ?.contains("image")!! ||
                        data[local_position].fileName?.trim()?.toLowerCase(Locale.getDefault())
                            ?.contains("jpg")!! ||
                        data[local_position].fileName?.trim()?.toLowerCase(Locale.getDefault())
                            ?.contains("png")!!
                    ) {
                        if (data[local_position].filePath?.toUri() != null) {
                            Glide.with(activity).load(data[local_position].filePath.toString())
                                .thumbnail(0.10f).timeout(1000 * 60).into(imgShow)
                        }
                    } else if (data[local_position].type?.trim()?.toLowerCase(Locale.getDefault())
                            ?.contains("pdf")!! ||
                        data[local_position].fileName?.trim()?.toLowerCase(Locale.getDefault())
                            ?.contains("pdf")!!
                    ) {
                        if (data[local_position].filePath?.toUri() != null) {
                            Glide.with(activity).load(R.drawable.pdf_file_logo).into(imgShow)
                        }
                    }
                }
                if (data[local_position].fileName != null) {
//                    tvFileName.text = data[local_position].fileName
                    tvFileName.text = data[local_position].fileNameAsOriginal
                }
            }
        }
    }


}