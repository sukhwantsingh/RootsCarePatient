package com.rootscare.ui.reviewandrating

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.patientreviewandratingrequest.PatientReviewAndRatingRequest
import com.rootscare.data.model.api.response.patientreviewandratingresponse.PatientReviewAndRatingResponse
import com.rootscare.data.model.api.response.patientreviewandratingresponse.ResultItem
import com.rootscare.databinding.FragmentReviewAndRatingBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.profile.FragmentProfile
import com.rootscare.ui.reviewandrating.adapter.AdapterReviewAndRatingRecyclerview

class FragmentReviewAndRating :
    BaseFragment<FragmentReviewAndRatingBinding, FragmentReviewAndRatingViewModel>(),
    FragmentReviewAndRatingNavigator {
    private var fragmentReviewAndRatingBinding: FragmentReviewAndRatingBinding? = null
    private var fragmentReviewAndRatingViewModel: FragmentReviewAndRatingViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_review_and_rating
    override val viewModel: FragmentReviewAndRatingViewModel
        get() {
            fragmentReviewAndRatingViewModel =
                ViewModelProviders.of(this).get(FragmentReviewAndRatingViewModel::class.java)
            return fragmentReviewAndRatingViewModel as FragmentReviewAndRatingViewModel
        }

    companion object {
        fun newInstance(): FragmentReviewAndRating {
            val args = Bundle()
            val fragment = FragmentReviewAndRating()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentReviewAndRatingViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentReviewAndRatingBinding = viewDataBinding
//        setUpViewReviewAndRatinglistingRecyclerview()

        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val patientReviewAndRatingResponse = PatientReviewAndRatingRequest()
            patientReviewAndRatingResponse.userId =
                fragmentReviewAndRatingViewModel?.appSharedPref?.userId

            fragmentReviewAndRatingViewModel?.apipatientreview(patientReviewAndRatingResponse)

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Set up recycler view for service listing if available
    private fun setUpViewReviewAndRatinglistingRecyclerview(reviewandratinglist: ArrayList<ResultItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentReviewAndRatingBinding!!.recyclerViewRootscareReviewandrating != null)
        val recyclerView = fragmentReviewAndRatingBinding!!.recyclerViewRootscareReviewandrating
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter = AdapterReviewAndRatingRecyclerview(reviewandratinglist, context!!)
        recyclerView.adapter = contactListAdapter

    }

    override fun successPatientReviewAndRatingResponse(patientReviewAndRatingResponse: PatientReviewAndRatingResponse?) {
        baseActivity?.hideLoading()

        if (patientReviewAndRatingResponse?.code.equals("200")) {

            if (patientReviewAndRatingResponse?.result != null && patientReviewAndRatingResponse.result.size > 0) {
                fragmentReviewAndRatingBinding?.recyclerViewRootscareReviewandrating?.visibility =
                    View.VISIBLE
                fragmentReviewAndRatingBinding?.tvNoDate?.visibility = View.GONE
                setUpViewReviewAndRatinglistingRecyclerview(patientReviewAndRatingResponse.result)

            } else {
                fragmentReviewAndRatingBinding?.recyclerViewRootscareReviewandrating?.visibility =
                    View.GONE
                fragmentReviewAndRatingBinding?.tvNoDate?.visibility = View.VISIBLE
                fragmentReviewAndRatingBinding?.tvNoDate?.text = patientReviewAndRatingResponse?.message
            }

        } else {
            fragmentReviewAndRatingBinding?.recyclerViewRootscareReviewandrating?.visibility =
                View.GONE
            fragmentReviewAndRatingBinding?.tvNoDate?.visibility = View.VISIBLE
            fragmentReviewAndRatingBinding?.tvNoDate?.text = patientReviewAndRatingResponse?.message
        }

    }

    override fun errorPatientReviewAndRatingResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }


}