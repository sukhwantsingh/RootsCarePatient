package com.rootscare.ui.home.subfragment

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.response.patienthome.*
import com.rootscare.databinding.FragmentHomeBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.adapter.*
import com.rootscare.ui.newaddition.providerlisting.FragmentProvderBooking
import com.rootscare.ui.newaddition.providerlisting.FragmentProvderBookingForDoctor
import com.rootscare.ui.newaddition.providerlisting.FragmentProviderListing
import com.rootscare.utilitycommon.DoctorEnabledFor
import com.rootscare.utilitycommon.ProviderTypes
import com.rootscare.utilitycommon.getBookingTypeFromProviderType
import com.rootscare.utils.ManagePermissions

class HomeFragment : BaseFragment<FragmentHomeBinding, HomeFragmentViewModel>(),
    HomeFragmentNavigator {
    private var fragmentHomeBinding: FragmentHomeBinding? = null
    private var homeFragmentViewModel: HomeFragmentViewModel? = null

    private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    private var managePermissions: ManagePermissions? = null

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_home
    override val viewModel: HomeFragmentViewModel
        get() {
            homeFragmentViewModel = ViewModelProviders.of(this).get(
                HomeFragmentViewModel::class.java
            )
            return homeFragmentViewModel as HomeFragmentViewModel
        }

    companion object {
        val TAG: String = HomeFragment::class.java.simpleName
        fun newInstance(): HomeFragment {
            val args = Bundle()
            val fragment = HomeFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeFragmentViewModel?.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentHomeBinding = viewDataBinding
        initViews()

    }

    private fun initViews() {
     fragmentHomeBinding?.inclNewHome?.run {

            // nurse
            crdNurse.setOnClickListener {
                moveToProviderListing(ProviderTypes.NURSE.getDisplayHeading(),ProviderTypes.NURSE.getType())
           }

            // Nurse Assistant
            crdNa.setOnClickListener {
                moveToProviderListing(ProviderTypes.CAREGIVER.getDisplayHeading(),ProviderTypes.CAREGIVER.getType())
            }

            // Baby sitter
            crdBabysitter.setOnClickListener {
                moveToProviderListing(ProviderTypes.BABYSITTER.getDisplayHeading(),
                    ProviderTypes.BABYSITTER.getType())
            }

            // PhysioTherapist
            crdPhy.setOnClickListener {
              moveToProviderListing(ProviderTypes.PHYSIOTHERAPY.getDisplayHeading(),
                  ProviderTypes.PHYSIOTHERAPY.getType())
            }

            // normal Doctor
            crdDocConsult.setOnClickListener {
              moveToProviderListing(ProviderTypes.DOCTOR.getDisplayHeading(), ProviderTypes.DOCTOR.getType(), docEnableFor = DoctorEnabledFor.ONLINE.get())
            }
            crdDocHomeVisit.setOnClickListener {
                moveToProviderListing(ProviderTypes.DOCTOR.getDisplayHeading(), ProviderTypes.DOCTOR.getType(), docEnableFor =  DoctorEnabledFor.HOME_VIST.get())
            }

            // Hospital Doc
            crdDocHosp.setOnClickListener {
             moveToProviderListing(ProviderTypes.HOSPITAL_DOCTOR.getDisplayName(), ProviderTypes.HOSPITAL_DOCTOR.getType(),needToMove = true)
            }

           // Hospital Lab
            crdLab.setOnClickListener {
              moveToProviderListing(ProviderTypes.LAB_TECHNICIAN.getDisplayName(), ProviderTypes.LAB_TECHNICIAN.getType(),needToMove = false)
            }

        }

    }

    private fun moveToProviderListing(titleName:String, provType:String, needToMove:Boolean = true, docEnableFor: String = "") {
        if(needToMove) {
            if(homeFragmentViewModel?.appSharedPref?.userCurrentLocation.isNullOrBlank().not()) {
                HomeActivity.providerName = titleName
                (activity as? HomeActivity)?.checkInBackstack(FragmentProviderListing.newInstance(provType, docEnableFor))
            } else {
                (activity as? HomeActivity)?.requestPermissionForLocation()
            }
        } else {
            showToast("Coming Soon")
        }
    }


    override fun successPatientHomeApiResponse(patientHomeApiResponse: PatientHomeApiResponse?) {
        baseActivity?.hideLoading()
    }

    override fun errorPatientHomeApiResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            getString(R.string.something_went_wrong)
        }
    }

}

