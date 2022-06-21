package com.rootscare.ui.viewprescription

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.appointmentrequest.AppointmentRequest
import com.rootscare.data.model.api.response.patientprescription.PatientPrescriptionResponse
import com.rootscare.data.model.api.response.patientprescription.ResultItem
import com.rootscare.databinding.FragmentViewPrescriptionBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.profile.FragmentProfile
import com.rootscare.ui.viewprescription.adapter.AdapterViewPrescriptionRecyclerview

class FragmnetViewPrespriction :
    BaseFragment<FragmentViewPrescriptionBinding, FragmnetViewPresprictionViewModel>(),
    FragmnetViewPresprictionNavigator {
    private var fragmentViewPrescriptionBinding: FragmentViewPrescriptionBinding? = null
    private var fragmnetViewPresprictionViewModel: FragmnetViewPresprictionViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_view_prescription
    override val viewModel: FragmnetViewPresprictionViewModel
        get() {
            fragmnetViewPresprictionViewModel =
                ViewModelProviders.of(this).get(FragmnetViewPresprictionViewModel::class.java)
            return fragmnetViewPresprictionViewModel as FragmnetViewPresprictionViewModel
        }

    companion object {
        fun newInstance(): FragmnetViewPrespriction {
            val args = Bundle()
            val fragment = FragmnetViewPrespriction()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmnetViewPresprictionViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentViewPrescriptionBinding = viewDataBinding
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val appointmentRequest = AppointmentRequest()
            appointmentRequest.userId = fragmnetViewPresprictionViewModel?.appSharedPref?.userId
//            appointmentRequest?.userId="11"

            fragmnetViewPresprictionViewModel?.apipatientprescription(appointmentRequest)

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
        val contactListAdapter = AdapterViewPrescriptionRecyclerview(prescriptionList, context!!)
        recyclerView.adapter = contactListAdapter


    }

    override fun successPatientPrescriptionResponse(patientPrescriptionResponse: PatientPrescriptionResponse?) {
        baseActivity?.hideLoading()
        if (patientPrescriptionResponse?.code.equals("200")) {
            if (patientPrescriptionResponse?.result != null && patientPrescriptionResponse.result.size > 0) {
                fragmentViewPrescriptionBinding!!.recyclerViewRootscareViewprescription.visibility =
                    View.VISIBLE
                fragmentViewPrescriptionBinding?.tvNoDate?.visibility = View.GONE
                setUpViewPrescriptionListingRecyclerview(patientPrescriptionResponse.result)

            } else {
                fragmentViewPrescriptionBinding!!.recyclerViewRootscareViewprescription.visibility =
                    View.GONE
                fragmentViewPrescriptionBinding?.tvNoDate?.visibility = View.VISIBLE
                fragmentViewPrescriptionBinding?.tvNoDate?.text = patientPrescriptionResponse?.message
            }

        } else {
            fragmentViewPrescriptionBinding!!.recyclerViewRootscareViewprescription.visibility =
                View.GONE
            fragmentViewPrescriptionBinding?.tvNoDate?.visibility = View.VISIBLE
            fragmentViewPrescriptionBinding?.tvNoDate?.text = patientPrescriptionResponse?.message

        }
    }

    override fun errorPatientPrescriptionResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

}