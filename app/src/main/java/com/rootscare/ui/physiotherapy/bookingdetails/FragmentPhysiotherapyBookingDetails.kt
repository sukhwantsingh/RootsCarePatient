package com.rootscare.ui.physiotherapy.bookingdetails

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentNursesAppointmentBookingDetailsBinding
import com.rootscare.databinding.FragmentPhysiotherapyBookingDetailsBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.nurses.nursesappointmentbookingdetails.FragmentNursesAppointmentBookingDetails
import com.rootscare.ui.nurses.nursesappointmentbookingdetails.FragmentNursesAppointmentBookingDetailsNavigator
import com.rootscare.ui.nurses.nursesappointmentbookingdetails.FragmentNursesAppointmentBookingDetailsViewModel

class FragmentPhysiotherapyBookingDetails : BaseFragment<FragmentPhysiotherapyBookingDetailsBinding, FragmentPhysiotherapyBookingDetailsViewModel>(),
    FragmentPhysiotherapyBookingDetailsNavigator {
    private var fragmentPhysiotherapyBookingDetailsBinding: FragmentPhysiotherapyBookingDetailsBinding? = null
    private var fragmentPhysiotherapyBookingDetailsViewModel: FragmentPhysiotherapyBookingDetailsViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_physiotherapy_booking_details
    override val viewModel: FragmentPhysiotherapyBookingDetailsViewModel
        get() {
            fragmentPhysiotherapyBookingDetailsViewModel =
                ViewModelProviders.of(this).get(FragmentPhysiotherapyBookingDetailsViewModel::class.java!!)
            return fragmentPhysiotherapyBookingDetailsViewModel as FragmentPhysiotherapyBookingDetailsViewModel
        }
    companion object {
        fun newInstance(): FragmentPhysiotherapyBookingDetails {
            val args = Bundle()
            val fragment = FragmentPhysiotherapyBookingDetails()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentPhysiotherapyBookingDetailsViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentPhysiotherapyBookingDetailsBinding = viewDataBinding
//        fragmentNursesAppointmentBookingDetailsBinding?.btnRootscareBookingDoctor?.setOnClickListener(View.OnClickListener {
//            (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                FragmentBookingAppointment.newInstance())
//        })
    }


}