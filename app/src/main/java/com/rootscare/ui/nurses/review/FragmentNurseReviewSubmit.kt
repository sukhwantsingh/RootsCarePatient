package com.rootscare.ui.nurses.review

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.dialog.CommonDialog
import com.interfaces.DialogClickCallback
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.nurse.review.InsertNurseReviewRequest
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorreviewsubmitresponse.DoctorReviewRatingSubmiteResponse
import com.rootscare.databinding.FragmentSubmitFeedbackBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment


class FragmentNurseReviewSubmit :
    BaseFragment<FragmentSubmitFeedbackBinding, FragmentNurseReviewSubmitViewModel>(),
    FragmentNurseReviewSubmitNavigator {
    private var fragmentSubmitFeedbackBinding: FragmentSubmitFeedbackBinding? = null
    private var fragmentSubmitReviewViewModel: FragmentNurseReviewSubmitViewModel? = null
    private var nurseId: String = ""
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_submit_feedback
    override val viewModel: FragmentNurseReviewSubmitViewModel
        get() {
            fragmentSubmitReviewViewModel =
                ViewModelProviders.of(this).get(FragmentNurseReviewSubmitViewModel::class.java!!)
            return fragmentSubmitReviewViewModel as FragmentNurseReviewSubmitViewModel
        }

    companion object {
        fun newInstance(nurseid: String): FragmentNurseReviewSubmit {
            val args = Bundle()
            args.putString("nurseid", nurseid)
            val fragment = FragmentNurseReviewSubmit()
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
        fragmentSubmitFeedbackBinding = viewDataBinding
        if (arguments != null && arguments?.getString("nurseid") != null) {
            nurseId = arguments?.getString("nurseid")!!
            Log.d("nurse Id", ": $nurseId")
        }
        fragmentSubmitFeedbackBinding?.btnSubmitReview?.setOnClickListener {
            CommonDialog.showDialog(this.activity!!, object :
                DialogClickCallback {
                override fun onDismiss() {
                }

                override fun onConfirm() {

                    submitReviewAndRatingApiCall()

                }

            }, "Submit Review", "Are you sure to submit review ?")
        }
    }

    override fun successDoctorReviewRatingSubmiteResponse(doctorReviewRatingSubmiteResponse: DoctorReviewRatingSubmiteResponse?) {
        baseActivity?.hideLoading()
        if (doctorReviewRatingSubmiteResponse?.code.equals("200")) {
            Toast.makeText(activity, doctorReviewRatingSubmiteResponse?.message, Toast.LENGTH_SHORT)
                .show()
//            (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                FragmentDoctorListingDetails.newInstance(doctorId))
            (activity as HomeActivity).checkInBackstack(
                HomeFragment.newInstance()
            )
        } else {
            Toast.makeText(activity, doctorReviewRatingSubmiteResponse?.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun errorDoctorReviewRatingSubmiteResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(HomeFragment.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun submitReviewAndRatingApiCall() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            var insertNurseReviewRequest = InsertNurseReviewRequest()

            insertNurseReviewRequest.userId = fragmentSubmitReviewViewModel!!.appSharedPref!!.userId
            insertNurseReviewRequest.nurseId = nurseId
            insertNurseReviewRequest.rating =
                fragmentSubmitFeedbackBinding?.ratingBarDoctorFeedback?.rating?.toString()
            insertNurseReviewRequest.review =
                fragmentSubmitFeedbackBinding?.edtReattingComment?.text?.toString()?.trim()

            fragmentSubmitReviewViewModel?.apiinsertnursereview(insertNurseReviewRequest)

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT).show()
        }
    }
}