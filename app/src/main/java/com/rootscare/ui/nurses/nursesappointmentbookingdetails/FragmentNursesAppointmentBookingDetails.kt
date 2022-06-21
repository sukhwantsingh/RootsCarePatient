package com.rootscare.ui.nurses.nursesappointmentbookingdetails

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentDoctorListingDetailsBinding
import com.rootscare.databinding.FragmentNursesAppointmentBookingDetailsBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.bookingappointment.FragmentBookingAppointment
import com.rootscare.ui.doctorlistingdetails.FragmentDoctorListingDetails
import com.rootscare.ui.doctorlistingdetails.FragmentDoctorListingDetailsNavigator
import com.rootscare.ui.doctorlistingdetails.FragmentDoctorListingDetailsViewModel
import com.rootscare.ui.home.HomeActivity

class FragmentNursesAppointmentBookingDetails : BaseFragment<FragmentNursesAppointmentBookingDetailsBinding, FragmentNursesAppointmentBookingDetailsViewModel>(),
    FragmentNursesAppointmentBookingDetailsNavigator {
    private var fragmentNursesAppointmentBookingDetailsBinding: FragmentNursesAppointmentBookingDetailsBinding? = null
    private var fragmentNursesAppointmentBookingDetailsViewModel: FragmentNursesAppointmentBookingDetailsViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_nurses_appointment_booking_details
    override val viewModel: FragmentNursesAppointmentBookingDetailsViewModel
        get() {
            fragmentNursesAppointmentBookingDetailsViewModel =
                ViewModelProviders.of(this).get(FragmentNursesAppointmentBookingDetailsViewModel::class.java!!)
            return fragmentNursesAppointmentBookingDetailsViewModel as FragmentNursesAppointmentBookingDetailsViewModel
        }
    companion object {
        fun newInstance(): FragmentNursesAppointmentBookingDetails {
            val args = Bundle()
            val fragment = FragmentNursesAppointmentBookingDetails()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentNursesAppointmentBookingDetailsViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentNursesAppointmentBookingDetailsBinding = viewDataBinding
//        fragmentNursesAppointmentBookingDetailsBinding?.btnRootscareBookingDoctor?.setOnClickListener(View.OnClickListener {
//            (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                FragmentBookingAppointment.newInstance())
//        })
    }


}