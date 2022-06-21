package com.rootscare.ui.viewPathologyReport

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.appointmentrequest.AppointmentRequest
import com.rootscare.data.model.api.response.patientReport.PatientReportResponse
import com.rootscare.data.model.api.response.patientReport.ResultItem
import com.rootscare.databinding.FragmentViewPrescriptionBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.profile.FragmentProfile
import com.rootscare.ui.viewPathologyReport.adapter.AdapterViewReportRecyclerView

class FragmentViewReport :
    BaseFragment<FragmentViewPrescriptionBinding, FragmentViewReportViewModel>(),
    FragmentViewReportNavigator {

    private var fragmentViewPrescriptionBinding: FragmentViewPrescriptionBinding? = null
    private var fragmentViewReportViewModel: FragmentViewReportViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_view_prescription
    override val viewModel: FragmentViewReportViewModel
        get() {
            fragmentViewReportViewModel =
                ViewModelProviders.of(this).get(FragmentViewReportViewModel::class.java)
            return fragmentViewReportViewModel as FragmentViewReportViewModel
        }

    companion object {
        fun newInstance(): FragmentViewReport {
            val args = Bundle()
            val fragment = FragmentViewReport()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentViewReportViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentViewPrescriptionBinding = viewDataBinding
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val appointmentRequest = AppointmentRequest()
            appointmentRequest.userId = fragmentViewReportViewModel?.appSharedPref?.userId

            fragmentViewReportViewModel?.apiPatientReport(appointmentRequest)

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Set up recycler view for service listing if available
    private fun setUpViewPrescriptionListingRecyclerview(prescriptionList: ArrayList<ResultItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentViewPrescriptionBinding!!.recyclerViewRootscareViewprescription != null)
        val recyclerView = fragmentViewPrescriptionBinding!!.recyclerViewRootscareViewprescription
        val gridLayoutManager = GridLayoutManager(activity, 2, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerview(trainerList,context!!)
        val contactListAdapter = AdapterViewReportRecyclerView(prescriptionList, context!!)
        recyclerView.adapter = contactListAdapter


    }

    override fun successPatientReportResponse(patientReportResponse: PatientReportResponse?) {
        baseActivity?.hideLoading()
        if (patientReportResponse?.code.equals("200")) {
            if (patientReportResponse?.result != null && patientReportResponse.result.size > 0) {
                fragmentViewPrescriptionBinding!!.recyclerViewRootscareViewprescription.visibility =
                    View.VISIBLE
                fragmentViewPrescriptionBinding?.tvNoDate?.visibility = View.GONE
                setUpViewPrescriptionListingRecyclerview(patientReportResponse.result)

            } else {
                fragmentViewPrescriptionBinding!!.recyclerViewRootscareViewprescription.visibility =
                    View.GONE
                fragmentViewPrescriptionBinding?.tvNoDate?.visibility = View.VISIBLE
                fragmentViewPrescriptionBinding?.tvNoDate?.text =
                    patientReportResponse?.message
            }
        } else {
            fragmentViewPrescriptionBinding!!.recyclerViewRootscareViewprescription.visibility =
                View.GONE
            fragmentViewPrescriptionBinding?.tvNoDate?.visibility = View.VISIBLE
            fragmentViewPrescriptionBinding?.tvNoDate?.text = patientReportResponse?.message
        }
    }

    override fun errorPatientReportResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }
}