package com.rootscare.ui.patientbookpaynow

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.dialog.CommonDialog
import com.interfaces.DialogClickCallback
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentPatientBookingPaynowBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment

class FragmentPatientbookPayNow :
    BaseFragment<FragmentPatientBookingPaynowBinding, FragmentPatientbookPayNowViewModel>(),
    FragmentPatientbookPayNowNavigator {

    private var fragmentPatientBookingPaynowBinding: FragmentPatientBookingPaynowBinding? = null
    private var fragmentPatientbookPayNowViewModel: FragmentPatientbookPayNowViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_patient_booking_paynow
    override val viewModel: FragmentPatientbookPayNowViewModel
        get() {
            fragmentPatientbookPayNowViewModel =
                ViewModelProviders.of(this).get(FragmentPatientbookPayNowViewModel::class.java)
            return fragmentPatientbookPayNowViewModel as FragmentPatientbookPayNowViewModel
        }

    companion object {
        fun newInstance(): FragmentPatientbookPayNow {
            val args = Bundle()
            val fragment = FragmentPatientbookPayNow()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentPatientbookPayNowViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentPatientBookingPaynowBinding = viewDataBinding
        // setUpAppointmentlistingRecyclerview()

        fragmentPatientBookingPaynowBinding?.btnBookingPayNow?.setOnClickListener {
            CommonDialog.showDialog(this.activity!!, object :
                DialogClickCallback {
                override fun onDismiss() {
                }

                override fun onConfirm() {
                    (activity as HomeActivity).checkInBackstack(HomeFragment.newInstance())

                }

            }, "Confirm Payment", "Are you sure for this payment ?")
        }
    }
}