package com.rootscare.ui.newaddition.providerlisting

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.core.text.parseAsHtml
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import com.rootscare.R
import com.rootscare.databinding.LayoutNewDialogBadGaewayBinding
import com.rootscare.databinding.LayoutNewDialogHowItWorksBinding
import com.rootscare.ui.supportmore.bottomsheet.OnBottomSheetCallback

class DialogBadGateway : DialogFragment() {

    private lateinit var mContext: Context
    lateinit var binding: LayoutNewDialogBadGaewayBinding

    companion object {
        var mCallback :OnBottomSheetCallback? = null
        var shRetry :String? = ""
        fun newInstance(clb: OnBottomSheetCallback, vararg showRetry: String): DialogBadGateway {
         this.mCallback = clb
         this.shRetry = showRetry.getOrNull(0)
         return DialogBadGateway()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.mContext = context
    }

    @SuppressLint("SetTextI18n")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.layout_new_dialog_bad_gaeway, container, false)
        binding.lifecycleOwner = this
        dialog?.window?.let {
            with(it) {
                setBackgroundDrawableResource(android.R.color.transparent)
                setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN)
                setDimAmount(DIALOG_BACK_RANGE)
            }

        }

        try {
            binding.run{
              btnSubmit.visibility = if(shRetry.isNullOrBlank()) View.GONE else View.VISIBLE
           // ivTop.setImageResource(R.drawable.)

                btnGoBack.setOnClickListener {
                    try {
                    mCallback?.onGoBack()
                   } catch (e: java.lang.Exception) {
                      println(e)
                  }
                }
                btnSubmit.setOnClickListener {
                    try {
                        mCallback?.onRetry()
                        dismiss()
                    } catch (e: java.lang.Exception) {
                        println(e)
                    }

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
                    val width = metrics.widthPixels * 0.90
                  //  val heigh= metrics.heightPixels * 0.70
                     setLayout(width.toInt(), ViewGroup.LayoutParams.WRAP_CONTENT)
                  //  setLayout(width.toInt(), heigh.toInt())
                    setGravity(Gravity.CENTER)
                    setWindowAnimations(R.style.DialogAnimation)
                }
            }
        } catch (e: Exception) {
           println(e)
        }
    }
}
