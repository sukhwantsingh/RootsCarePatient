package com.rootscare.ui.newaddition.providerlisting.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rootscare.R
import com.rootscare.databinding.LayoutNewItemRowTasksSelectedBinding
import com.rootscare.ui.newaddition.providerlisting.models.ModelTaskListWithPrice
import java.util.*

interface OnSelectedTasksCallback {
    fun onCancelTask(node: ModelTaskListWithPrice.Result?)
}

class AdapterSelectedTasks :
    ListAdapter<ModelTaskListWithPrice.Result, AdapterSelectedTasks.ViewHolder>( SelectedTasksListDiffUtil()) {

    var mCallback: OnSelectedTasksCallback? = null
    val updatedArrayList: MutableList<ModelTaskListWithPrice.Result?> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = DataBindingUtil.inflate<LayoutNewItemRowTasksSelectedBinding>(
            LayoutInflater.from(parent.context),
            R.layout.layout_new_item_row_tasks_selected, parent, false
        )
        return ViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBindView(updatedArrayList[position])
    }

    override fun submitList(list: MutableList<ModelTaskListWithPrice.Result?>?) {
        super.submitList(if (list.isNullOrEmpty()) null else ArrayList(list))
    }

    fun loadDataIntoList(node: ModelTaskListWithPrice.Result?) {
        node?.let {
            updatedArrayList.add(node)
            submitList(updatedArrayList)
        }
    }

    fun removeNode(node: ModelTaskListWithPrice.Result?) {
        node?.let {
            updatedArrayList.remove(node)
            submitList(updatedArrayList)
        }
    }

    inner class ViewHolder(val binding: LayoutNewItemRowTasksSelectedBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.imgCancel.setOnClickListener {
                mCallback?.onCancelTask(updatedArrayList[absoluteAdapterPosition])
                removeNode(updatedArrayList[absoluteAdapterPosition])
            }
        }

        fun onBindView(item: ModelTaskListWithPrice.Result?) {
            binding.run {
                tvTask.text = updatedArrayList[absoluteAdapterPosition]?.name ?: ""
            }
        }

    }


}

class SelectedTasksListDiffUtil : DiffUtil.ItemCallback<ModelTaskListWithPrice.Result>() {
    override fun areItemsTheSame(
        oldItem: ModelTaskListWithPrice.Result,
        newItem: ModelTaskListWithPrice.Result
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ModelTaskListWithPrice.Result,
        newItem: ModelTaskListWithPrice.Result
    ): Boolean {
        return oldItem == newItem
    }

}