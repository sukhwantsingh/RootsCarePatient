package com.rootscare.ui.paymenthistory

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.patientpaymenthistoryreuest.PatientPaymentHistoryRequest
import com.rootscare.data.model.api.response.paymenthistoryresponse.PatientPaymentHistoryResponse
import com.rootscare.data.model.api.response.paymenthistoryresponse.ResultItem
import com.rootscare.databinding.FragmentPaymentHistoryBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.paymenthistory.adapter.AdapterPaymentHistoryRecyclerview
import com.rootscare.ui.profile.FragmentProfile

class FragmentPaymentHistory :
    BaseFragment<FragmentPaymentHistoryBinding, FragmentPaymentHistoryViewModel>(),
    FragmentPaymentHistoryNavigator {
    private var fragmentPaymentHistoryBinding: FragmentPaymentHistoryBinding? = null
    private var fragmentPaymentHistoryViewModel: FragmentPaymentHistoryViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_payment_history
    override val viewModel: FragmentPaymentHistoryViewModel
        get() {
            fragmentPaymentHistoryViewModel =
                ViewModelProviders.of(this).get(FragmentPaymentHistoryViewModel::class.java)
            return fragmentPaymentHistoryViewModel as FragmentPaymentHistoryViewModel
        }

    companion object {
        fun newInstance(): FragmentPaymentHistory {
            val args = Bundle()
            val fragment = FragmentPaymentHistory()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentPaymentHistoryViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentPaymentHistoryBinding = viewDataBinding
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val patientPaymentHistoryRequest = PatientPaymentHistoryRequest()
            patientPaymentHistoryRequest.userId =
                fragmentPaymentHistoryViewModel?.appSharedPref?.userId

            fragmentPaymentHistoryViewModel?.apipatientpaymenthistory(patientPaymentHistoryRequest)

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }

    }

    // Set up recycler view for service listing if available
    private fun setUpPaymentHistoryListingRecyclerview(paymentHistoryList: ArrayList<ResultItem?>?) {
//        assert(fragmentPaymentHistoryBinding!!.recyclerViewRootsCarePaymentHistory != null)
        val recyclerView = fragmentPaymentHistoryBinding!!.recyclerViewRootscarePaymenthistory
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterPaymentHistoryRecyclerview(paymentHistoryList, requireContext())
        recyclerView.adapter = contactListAdapter


    }

    override fun successPatientPaymentHistoryResponse(patientPaymentHistoryResponse: PatientPaymentHistoryResponse?) {
        baseActivity?.hideLoading()
        if (patientPaymentHistoryResponse?.code.equals("200")) {
            if (patientPaymentHistoryResponse?.result != null && patientPaymentHistoryResponse.result.size > 0) {
                setUpPaymentHistoryListingRecyclerview(patientPaymentHistoryResponse.result)

            } else {
                fragmentPaymentHistoryBinding?.recyclerViewRootscarePaymenthistory?.visibility =
                    View.GONE
                fragmentPaymentHistoryBinding?.tvNoDate?.visibility = View.VISIBLE
                fragmentPaymentHistoryBinding?.tvNoDate?.text = patientPaymentHistoryResponse?.message
            }

        } else {
            fragmentPaymentHistoryBinding?.recyclerViewRootscarePaymenthistory?.visibility =
                View.GONE
            fragmentPaymentHistoryBinding?.tvNoDate?.visibility = View.VISIBLE
            fragmentPaymentHistoryBinding?.tvNoDate?.text = patientPaymentHistoryResponse?.message
        }
    }

    override fun errorPatientPaymentHistoryResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }
}