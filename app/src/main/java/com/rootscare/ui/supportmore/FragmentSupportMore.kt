package com.rootscare.ui.supportmore

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.google.gson.JsonObject
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.ActivityNeedSupportScreenBinding
import com.rootscare.databinding.ActivitySupportAndMoreBinding
import com.rootscare.databinding.FragmentProviderListingBinding
import com.rootscare.databinding.LayoutNewFragmentSupportAndMoreBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.utilitycommon.*

class FragmentSupportMore : BaseFragment<LayoutNewFragmentSupportAndMoreBinding, CommonViewModel>(){
    private var binding: LayoutNewFragmentSupportAndMoreBinding? = null
    private var mViewModel: CommonViewModel? = null

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.layout_new_fragment_support_and_more
    override val viewModel: CommonViewModel
        get() {
            mViewModel = ViewModelProviders.of(this).get(CommonViewModel::class.java)
            return mViewModel as CommonViewModel
        }
    companion object {
        fun newInstance(): FragmentSupportMore {
            val args = Bundle()
//            args.putString(ARG_PROVIDER_TYPE, userType)
            val fragment = FragmentSupportMore()
              fragment.arguments = args
            return fragment
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        initViews()
    }

    private fun initViews() {
        setLanguagePrefernce()
        binding?.run {
            tvVersion.text = getAppVersionText()

            tvhEng.setOnClickListener {
                mViewModel?.appSharedPref?.languagePref = LanguageModes.ENG.get()
                tvhEng.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lang_pref_select))
                tvhAr.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
            tvhAr.setOnClickListener {
                mViewModel?.appSharedPref?.languagePref = LanguageModes.AR.get()
                tvhAr.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lang_pref_select))
                tvhEng.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
            }

            tvhTermsnConditions.setOnClickListener {
                navigate<CommonWebviewScreen>(listOf(IntentParams(OPEN_FOR, OPEN_FOR_TERMS)))
            }
            tvhAboutRoot.setOnClickListener {
                navigate<CommonWebviewScreen>(listOf(IntentParams(OPEN_FOR, OPEN_FOR_ABOUT)))
            }
            tvhPrivacyp.setOnClickListener {
                navigate<CommonWebviewScreen>(listOf(IntentParams(OPEN_FOR, OPEN_FOR_PRIVACY)))
            }

            tvhNeedsupport.setOnClickListener {
                navigate<NeedSupportScreen>()
            }
        }
    }

    private fun setLanguagePrefernce() {
        if (mViewModel?.appSharedPref?.languagePref.equals(LanguageModes.ENG.get(), ignoreCase = true)) {
            binding?.tvhEng?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lang_pref_select))
            binding?.tvhAr?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            binding?.tvhAr?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lang_pref_select))
            binding?.tvhEng?.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

}