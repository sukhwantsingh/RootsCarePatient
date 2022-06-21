package com.rootscare.ui.newaddition.appointments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.rootscare.R
import com.rootscare.databinding.LayoutNewDialogRateAppointmentBinding
import com.rootscare.ui.supportmore.bottomsheet.DIALOG_BACK_RANGE
import com.rootscare.ui.supportmore.bottomsheet.OnBottomSheetCallback


class DialogRateAppointment : DialogFragment() {
    private lateinit var mContext: Context
    lateinit var binding: LayoutNewDialogRateAppointmentBinding

    companion object {
        var mCallback: OnBottomSheetCallback? = null
        fun newInstance(clb: OnBottomSheetCallback?): DialogRateAppointment {
            this.mCallback = clb
            return DialogRateAppointment()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(
            LayoutInflater.from(mContext),R.layout.layout_new_dialog_rate_appointment, container,false
        )
        binding.lifecycleOwner = this
        dialog?.window?.let {
            with(it) {
                setBackgroundDrawableResource(android.R.color.transparent)
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
                setDimAmount(DIALOG_BACK_RANGE)
            }

        }

        try {
            binding.run {
                btnCancel.setOnClickListener { dismiss() }

                btnSubmit.setOnClickListener {
                    val rate = ratingBar.rating.toInt().toString()
                    val rev = edReview.text.toString()

                    mCallback?.onSubmitRating(rate,rev)
                    dismiss()
                }
            }
        } catch (e: java.lang.Exception) {
            println(e)
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.window?.requestFeature(Window.FEATURE_NO_TITLE)
        try {
            dialog.setCanceledOnTouchOutside(false)
        } catch (e: Exception) {
            println(e)
        }
        return dialog
    }

    override fun onResume() {
        super.onResume()
        requireView().isFocusableInTouchMode = true
        requireView().requestFocus()
    }

    override fun onStart() {
        super.onStart()
        try {
            dialog?.window?.let {
                with(it) {
                    val metrics = mContext.resources.displayMetrics
                    val width = metrics.widthPixels
                  //  val heigh = metrics.heightPixels * 0.70
                    setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
                   // setLayout(width.toInt(), heigh.toInt())
                    setGravity(Gravity.BOTTOM)
                    setWindowAnimations(R.style.DialogAnimation)
                }
            }
        } catch (e: Exception) {
            println(e)
        }
    }

}
