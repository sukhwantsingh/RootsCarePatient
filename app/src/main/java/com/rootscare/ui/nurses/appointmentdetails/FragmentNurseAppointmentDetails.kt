package com.rootscare.ui.nurses.appointmentdetails

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.appointmentdetailsrequest.AppointmentDetailsRequest
import com.rootscare.data.model.api.response.nurses.nurseappointmentdetails.NurseAppointmentDetailsResponse
import com.rootscare.databinding.FragmentAppiontmentDetailsBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.subfragment.HomeFragment
import com.rootscare.utilitycommon.BaseMediaUrls

class FragmentNurseAppointmentDetails :
    BaseFragment<FragmentAppiontmentDetailsBinding, FragmentNurseAppointmentDetailsViewModel>(),
    FragmentNurseAppointmentDetailsNavigator {
    private var fragmentAppiontmentDetailsBinding: FragmentAppiontmentDetailsBinding? = null
    private var fragmentAppiontmentDetailsViewModel: FragmentNurseAppointmentDetailsViewModel? =
        null
    private var appointmentId: String = ""
    var stratTime = ""
    var endTime = ""
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_appiontment_details
    override val viewModel: FragmentNurseAppointmentDetailsViewModel
        get() {
            fragmentAppiontmentDetailsViewModel =
                ViewModelProviders.of(this)
                    .get(FragmentNurseAppointmentDetailsViewModel::class.java)
            return fragmentAppiontmentDetailsViewModel as FragmentNurseAppointmentDetailsViewModel
        }

    companion object {
        fun newInstance(id: String): FragmentNurseAppointmentDetails {
            val args = Bundle()
            args.putString("appointmentid", id)
            val fragment = FragmentNurseAppointmentDetails()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentAppiontmentDetailsViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentAppiontmentDetailsBinding = viewDataBinding
        if (arguments != null && arguments?.getString("appointmentid") != null) {
            appointmentId = arguments?.getString("appointmentid")!!
            Log.d("AppointmentId Id", ": $appointmentId")
        }

        if (isNetworkConnected) {
            baseActivity?.showLoading()
            var appointmentDetailsRequest = AppointmentDetailsRequest()
            appointmentDetailsRequest.id = appointmentId
            appointmentDetailsRequest.serviceType = "nurse"
            fragmentAppiontmentDetailsViewModel?.apiappointmentdetails(appointmentDetailsRequest)

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun successDoctorAppointmentResponse(surseAppointmentDetailsResponse: NurseAppointmentDetailsResponse?) {
        baseActivity?.hideLoading()
        if (surseAppointmentDetailsResponse?.code.equals("200")) {
            if (surseAppointmentDetailsResponse?.result != null) {

                if (!surseAppointmentDetailsResponse.result.nurseImage.equals("") && surseAppointmentDetailsResponse.result.nurseImage != null) {
                    Glide.with(requireActivity())
                        .load(BaseMediaUrls.USERIMAGE.url + (surseAppointmentDetailsResponse.result.nurseImage))
                        .into(fragmentAppiontmentDetailsBinding?.imgAppointmentDetailsProfile!!)
                }
                if (surseAppointmentDetailsResponse.result.nurseName != null && surseAppointmentDetailsResponse.result.nurseName != ""
                ) {
                    fragmentAppiontmentDetailsBinding?.txtDoctorName?.text =
                        surseAppointmentDetailsResponse.result.nurseName
                } else {
                    fragmentAppiontmentDetailsBinding?.txtDoctorName?.text = ""
                }
                if (surseAppointmentDetailsResponse.result.patientName != null && surseAppointmentDetailsResponse.result.patientName != ""
                ) {
                    fragmentAppiontmentDetailsBinding?.txtPatientName?.text =
                        surseAppointmentDetailsResponse.result.patientName
                } else {
                    fragmentAppiontmentDetailsBinding?.txtPatientName?.text = ""
                }

                if (surseAppointmentDetailsResponse.result.appointmentDate != null && !surseAppointmentDetailsResponse.result.appointmentDate.equals(
                        ""
                    )
                ) {
                    fragmentAppiontmentDetailsBinding?.txtAppointmentDate?.text =
                        surseAppointmentDetailsResponse.result.appointmentDate
                } else {
                    fragmentAppiontmentDetailsBinding?.txtAppointmentDate?.text = ""
                }
                if (surseAppointmentDetailsResponse.result.appointmentStatus != null && !surseAppointmentDetailsResponse.result.appointmentStatus.equals(
                        ""
                    )
                ) {
                    fragmentAppiontmentDetailsBinding?.txtPatientAddress?.text =
                        surseAppointmentDetailsResponse.result.appointmentStatus
                } else {
                    fragmentAppiontmentDetailsBinding?.txtPatientAddress?.text = ""
                }

                if (surseAppointmentDetailsResponse.result.price != null && !surseAppointmentDetailsResponse.result.price.equals(
                        ""
                    )
                ) {
                    fragmentAppiontmentDetailsBinding?.txtAmount?.text =
                        surseAppointmentDetailsResponse.result.price
                } else {
                    fragmentAppiontmentDetailsBinding?.txtAmount?.text = ""
                }

                if (surseAppointmentDetailsResponse.result.fromTime != null && surseAppointmentDetailsResponse.result.fromTime != ""
                ) {
                    stratTime = surseAppointmentDetailsResponse.result.fromTime
                } else {
                    stratTime = ""
                }

                if (surseAppointmentDetailsResponse.result.toTime != null && surseAppointmentDetailsResponse.result.toTime != ""
                ) {
                    endTime = surseAppointmentDetailsResponse.result.toTime
                } else {
                    endTime = ""
                }

                fragmentAppiontmentDetailsBinding?.txtBookingSlot?.text = "$stratTime-$endTime"

                if (surseAppointmentDetailsResponse.result.nurseExperience != null && !surseAppointmentDetailsResponse.result.nurseExperience.equals(
                        ""
                    )
                ) {
                    fragmentAppiontmentDetailsBinding?.txtDoctorExperience?.text =
                        "Work Experience " + " " + surseAppointmentDetailsResponse.result.nurseExperience + " " + "years"
                } else {
                    fragmentAppiontmentDetailsBinding?.txtDoctorExperience?.text = ""
                }

                if (surseAppointmentDetailsResponse.result.patientContact != null && !surseAppointmentDetailsResponse.result.patientContact.equals(
                        ""
                    )
                ) {
                    fragmentAppiontmentDetailsBinding?.txtPatientContactnumber?.text =
                        surseAppointmentDetailsResponse.result.patientContact
                } else {
                    fragmentAppiontmentDetailsBinding?.txtPatientContactnumber?.text = ""
                }
            }

        } else {
            Toast.makeText(activity, surseAppointmentDetailsResponse?.message, Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun errorDoctorAppointmentResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(HomeFragment.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }
}