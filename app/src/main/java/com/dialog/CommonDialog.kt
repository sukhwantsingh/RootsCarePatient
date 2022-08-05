package com.dialog

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.text.Spanned
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.text.parseAsHtml
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.interfaces.*
import com.rootscare.R
import com.rootscare.adapter.AdapterCommonDropdown
import com.rootscare.adapter.AdapterRowItemListDropdown
import com.model.RowItem

@SuppressLint("StaticFieldLeak")
object CommonDialog {

    private val TAG = "CommonDialog"

    fun showDialog(
        activity: Context, dialogClickCallback: DialogClickCallback, dialog_title: String,
        dialog_message: String, pBtnText:String? = "Yes"
    ) {
     AlertDialog.Builder(activity).apply {
            setTitle(dialog_title)
            setMessage(dialog_message)
            setIcon(R.drawable.app_icon)

            setCancelable(true)
            setPositiveButton(pBtnText) { dialogInterface, _ ->
                dialogClickCallback.onConfirm()
                dialogInterface.dismiss()
            }
            setNegativeButton(R.string.no) { dialog, _ ->
                dialogClickCallback.onDismiss()
                dialog.dismiss()
            }
            show()
        }


    }

    fun showTwoButtonDialog(
        activity: Context, dialogClickCallback: DialogClickCallback, dialog_title: Spanned,
        dialog_message: Spanned, pBtnText:String?, nBtnText: String?
    ) {
     AlertDialog.Builder(activity).apply {
            setTitle(dialog_title)
            setMessage(dialog_message)
            setIcon(R.drawable.app_icon)

            setCancelable(true)
            setPositiveButton(pBtnText) { dialogInterface, _ ->
                dialogClickCallback.onConfirm()
                dialogInterface.dismiss()
            }
            setNegativeButton(nBtnText) { dialog, _ ->
                dialogClickCallback.onDismiss()
                dialog.dismiss()
            }
            show()
        }


    }


    fun showDialogForSingleButton(
        activity: Context, dialogClickCallback: DialogClickCallback, dialog_title: String?,
        dialog_message: String
    ) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        if(dialog_title.isNullOrBlank().not()){
            alertDialogBuilder.setTitle(dialog_title)
        }
        alertDialogBuilder.setIcon(R.drawable.app_icon)
        alertDialogBuilder.setMessage(dialog_message.parseAsHtml())
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(R.string.ok) { dialogInterface, i ->
            dialogClickCallback.onConfirm()
            dialogInterface.dismiss()
        }
        alertDialogBuilder.show()
    }



    fun showDialogForDropDownList( context: Context,  title: String, dropdownList: ArrayList<String?>?,
        dialogClickCallback: DropDownDialogCallBack
    ) {
        val dialog = Dialog(context)
        dialog.setContentView(R.layout.dialog_dropdown)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        val rv_ = dialog.findViewById<RecyclerView>(R.id.recyclerView_dropdown_list)
        val tv_title = dialog.findViewById<TextView>(R.id.txt_header_title)
        tv_title?.text = title
        val gridLayoutManager = GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false)
        rv_.layoutManager = gridLayoutManager
        val dropdownListAdapter = AdapterCommonDropdown(dropdownList!!, context)
        rv_.adapter = dropdownListAdapter
        dropdownListAdapter.recyclerViewItemClick = object : OnDropDownListItemClickListener {
            override fun onConfirm(text: String) {
                dialogClickCallback.onConfirm(text)
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    fun showDialogWithSingleButton(activity: Context, dClb: DialogClickCallback, dialog_title: String?,  dialog_message: String) {
        val adB = AlertDialog.Builder(activity)
        if(dialog_title.isNullOrBlank().not()) adB.setTitle(dialog_title)
        adB.setMessage(dialog_message)
        adB.setIcon(R.drawable.app_icon)
        adB.setCancelable(false)
        adB.setPositiveButton(R.string.ok) { dialogInterface, _ ->
            dClb.onConfirm()
            dialogInterface.dismiss()
        }
        adB.show()
    }

    fun showDialogWithProceed(
        activity: Context, dialogClickCallback: DialogClickCallback, dialog_title: String?,
        msg: String
    ) {
        val alertDialogBuilder = AlertDialog.Builder(activity)
        if(dialog_title.isNullOrBlank().not()) alertDialogBuilder.setTitle(dialog_title)
        alertDialogBuilder.setMessage(msg)
        alertDialogBuilder.setIcon(R.drawable.app_icon)
        alertDialogBuilder.setCancelable(false)
        alertDialogBuilder.setPositiveButton(R.string.proceed) { dialogInterface, _ ->
            dialogClickCallback.onConfirm()
            dialogInterface.dismiss()
        }
        alertDialogBuilder.show()
    }

  // Show Image uploading progress
    var dialog: Dialog? = null

}
