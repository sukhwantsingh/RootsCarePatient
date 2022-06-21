package com.rootscare.ui.physiotherapy.submitreviewforservice

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentSeeAllPhysiotherapyListingBinding
import com.rootscare.databinding.FragmentSubmitReviewBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.physiotherapy.FragmentSeeAllPhysiotherapyListing
import com.rootscare.ui.physiotherapy.FragmentSeeAllPhysiotherapyListingNavigator
import com.rootscare.ui.physiotherapy.FragmentSeeAllPhysiotherapyListingViewModel

class FragmentSubmitReview :
    BaseFragment<FragmentSubmitReviewBinding, FragmentSubmitReviewViewModel>(),
    FragmentSubmitReviewNavigator {
    private var fragmentSubmitReviewBinding: FragmentSubmitReviewBinding? = null
    private var fragmentSubmitReviewViewModel: FragmentSubmitReviewViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_submit_review
    override val viewModel: FragmentSubmitReviewViewModel
        get() {
            fragmentSubmitReviewViewModel =
                ViewModelProviders.of(this).get(FragmentSubmitReviewViewModel::class.java)
            return fragmentSubmitReviewViewModel as FragmentSubmitReviewViewModel
        }

    companion object {
        fun newInstance(): FragmentSubmitReview {
            val args = Bundle()
            val fragment = FragmentSubmitReview()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentSubmitReviewViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentSubmitReviewBinding = viewDataBinding
    }

}