package com.rootscare.ui.babysitter.bookingdetails

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentBabysitterBookingDetailsBinding
import com.rootscare.databinding.FragmentCaregiverBookingDetailsBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.caregiver.bookingdetails.FragmentCaregiverBookingDetails
import com.rootscare.ui.caregiver.bookingdetails.FragmentCaregiverBookingDetailsViewModel
import com.rootscare.ui.caregiver.bookingdetails.FragmentCaregiverBookingDetailsnavigator

class FragmentBabySitterBookingDetails  : BaseFragment<FragmentBabysitterBookingDetailsBinding, FragmentBabySitterBookingDetailsViewModel>(),
    FragmentBabySitterBookingDetailsNavigator {
    private var fragmentBabysitterBookingDetailsBinding: FragmentBabysitterBookingDetailsBinding? = null
    private var fragmentBabySitterBookingDetailsViewModel: FragmentBabySitterBookingDetailsViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_babysitter_booking_details
    override val viewModel: FragmentBabySitterBookingDetailsViewModel
        get() {
            fragmentBabySitterBookingDetailsViewModel =
                ViewModelProviders.of(this).get(FragmentBabySitterBookingDetailsViewModel::class.java!!)
            return fragmentBabySitterBookingDetailsViewModel as FragmentBabySitterBookingDetailsViewModel
        }
    companion object {
        fun newInstance(): FragmentBabySitterBookingDetails {
            val args = Bundle()
            val fragment = FragmentBabySitterBookingDetails()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentBabySitterBookingDetailsViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentBabysitterBookingDetailsBinding = viewDataBinding
//        fragmentNursesAppointmentBookingDetailsBinding?.btnRootscareBookingDoctor?.setOnClickListener(View.OnClickListener {
//            (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                FragmentBookingAppointment.newInstance())
//        })
    }


}