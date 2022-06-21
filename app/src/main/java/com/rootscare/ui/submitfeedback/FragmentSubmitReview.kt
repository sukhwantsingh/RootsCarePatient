package com.rootscare.ui.submitfeedback

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.dialog.CommonDialog
import com.interfaces.DialogClickCallback
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.insertdoctorreviewratingrequest.InsertDoctorReviewRatingRequest
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorreviewsubmitresponse.DoctorReviewRatingSubmiteResponse
import com.rootscare.databinding.FragmentSubmitFeedbackBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment


class FragmentSubmitReview :
    BaseFragment<FragmentSubmitFeedbackBinding, FragmentSubmitReviewViewModel>(),
    FragmentSubmitReviewNavigator {
    private var fragmentSubmitFeedbackBinding: FragmentSubmitFeedbackBinding? = null
    private var fragmentSubmitReviewViewModel: FragmentSubmitReviewViewModel? = null
    private var doctorId: String = ""
    private var orderId: String = ""
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_submit_feedback
    override val viewModel: FragmentSubmitReviewViewModel
        get() {
            fragmentSubmitReviewViewModel =
                ViewModelProviders.of(this).get(FragmentSubmitReviewViewModel::class.java)
            return fragmentSubmitReviewViewModel as FragmentSubmitReviewViewModel
        }

    companion object {
        fun newInstance(doctorId: String, orderId: String): FragmentSubmitReview {
            val args = Bundle()
            args.putString("doctorId", doctorId)
            args.putString("orderId", orderId)
            val fragment = FragmentSubmitReview()
            fragment.arguments = args
            return fragment
        }

        fun newInstance(doctorId: String): FragmentSubmitReview {
            val args = Bundle()
            args.putString("doctorId", doctorId)
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
        fragmentSubmitFeedbackBinding = viewDataBinding
        if (arguments != null && arguments?.getString("doctorId") != null) {
            doctorId = arguments?.getString("doctorId")!!
            Log.d("Doctor Id", ": $doctorId")
        }
        if (arguments != null && arguments?.getString("orderId") != null) {
            orderId = arguments?.getString("orderId")!!
            Log.d("Order Id", ": $orderId")
        }

        fragmentSubmitFeedbackBinding?.ratingBarDoctorFeedback?.onRatingBarChangeListener =
            OnRatingBarChangeListener { _, rating, _ ->
                //            txtRatingValue.setText(
                //                rating.toString()


                //            )
                fragmentSubmitFeedbackBinding?.ratingBarDoctorFeedback?.rating = rating
            }

//        fragmentSubmitFeedbackBinding?.ratingBarteacherFeedback?.setOnTouchListener(OnTouchListener { v, event ->
//            if (event.action == MotionEvent.ACTION_UP) {
//                val touchPositionX = event.x
//                val width: Float =
//                    fragmentSubmitFeedbackBinding?.ratingBarteacherFeedback?.getWidth()?.toFloat()!!
//                val starsf = touchPositionX / width * 5.0f
//                val stars = starsf.toInt() + 1
//                fragmentSubmitFeedbackBinding?.ratingBarteacherFeedback?.setRating(stars.toFloat())
//                Toast.makeText(activity, "test", Toast.LENGTH_SHORT).show()
//                v.isPressed = false
//            }
//            if (event.action == MotionEvent.ACTION_DOWN) {
//                v.isPressed = true
//            }
//            if (event.action == MotionEvent.ACTION_CANCEL) {
//                v.isPressed = false
//            }
//            true
//        })
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
            val insertDoctorReviewRatingRequest = InsertDoctorReviewRatingRequest()
            insertDoctorReviewRatingRequest.userId =
                fragmentSubmitReviewViewModel!!.appSharedPref!!.userId
            insertDoctorReviewRatingRequest.doctorId = doctorId
            insertDoctorReviewRatingRequest.rating =
                fragmentSubmitFeedbackBinding?.ratingBarDoctorFeedback?.rating.toString()
            insertDoctorReviewRatingRequest.review =
                fragmentSubmitFeedbackBinding?.edtReattingComment?.text?.toString()?.trim()
            insertDoctorReviewRatingRequest.orderId = orderId
            fragmentSubmitReviewViewModel?.apiinsertdoctorreview(insertDoctorReviewRatingRequest)

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }
}