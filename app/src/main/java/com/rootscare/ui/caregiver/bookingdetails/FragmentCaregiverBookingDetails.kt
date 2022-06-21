package com.rootscare.ui.caregiver.bookingdetails

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentCaregiverBookingDetailsBinding
import com.rootscare.databinding.FragmentNursesAppointmentBookingDetailsBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.nurses.nursesappointmentbookingdetails.FragmentNursesAppointmentBookingDetails
import com.rootscare.ui.nurses.nursesappointmentbookingdetails.FragmentNursesAppointmentBookingDetailsNavigator
import com.rootscare.ui.nurses.nursesappointmentbookingdetails.FragmentNursesAppointmentBookingDetailsViewModel

class FragmentCaregiverBookingDetails : BaseFragment<FragmentCaregiverBookingDetailsBinding, FragmentCaregiverBookingDetailsViewModel>(),
    FragmentCaregiverBookingDetailsnavigator {
    private var fragmentCaregiverBookingDetailsBinding: FragmentCaregiverBookingDetailsBinding? = null
    private var fragmentCaregiverBookingDetailsViewModel: FragmentCaregiverBookingDetailsViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_caregiver_booking_details
    override val viewModel: FragmentCaregiverBookingDetailsViewModel
        get() {
            fragmentCaregiverBookingDetailsViewModel =
                ViewModelProviders.of(this).get(FragmentCaregiverBookingDetailsViewModel::class.java!!)
            return fragmentCaregiverBookingDetailsViewModel as FragmentCaregiverBookingDetailsViewModel
        }
    companion object {
        fun newInstance(): FragmentCaregiverBookingDetails {
            val args = Bundle()
            val fragment = FragmentCaregiverBookingDetails()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentCaregiverBookingDetailsViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentCaregiverBookingDetailsBinding = viewDataBinding
//        fragmentNursesAppointmentBookingDetailsBinding?.btnRootscareBookingDoctor?.setOnClickListener(View.OnClickListener {
//            (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                FragmentBookingAppointment.newInstance())
//        })
    }


}