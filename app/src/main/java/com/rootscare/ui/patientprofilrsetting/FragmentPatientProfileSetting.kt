package com.rootscare.ui.patientprofilrsetting

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.dialog.CommonDialog
import com.interfaces.DialogClickCallback
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.appointmentrequest.AppointmentRequest
import com.rootscare.data.model.api.response.deactivateaccountresponse.DeactivateAccountResponse
import com.rootscare.databinding.FragmentPatientProfileSettingBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.login.LoginActivity
import com.rootscare.ui.profile.FragmentProfile
import com.rootscare.utilitycommon.BaseMediaUrls


class FragmentPatientProfileSetting :
    BaseFragment<FragmentPatientProfileSettingBinding, FragmentPatientProfileSettingViewModel>(),
    FragmentPatientProfileSettingNavigator {
    private var fragmentPatientProfileSettingBinding: FragmentPatientProfileSettingBinding? = null
    private var fragmentPatientProfileSettingViewModel: FragmentPatientProfileSettingViewModel? =
        null
    private var useremail: String = ""
    private var userName: String = ""
    private var userimage: String = ""
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_patient_profile_setting
    override val viewModel: FragmentPatientProfileSettingViewModel
        get() {
            fragmentPatientProfileSettingViewModel = ViewModelProviders.of(this)
                .get(FragmentPatientProfileSettingViewModel::class.java)
            return fragmentPatientProfileSettingViewModel as FragmentPatientProfileSettingViewModel
        }

    companion object {
        fun newInstance(): FragmentPatientProfileSetting {
            val args = Bundle()
            val fragment = FragmentPatientProfileSetting()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentPatientProfileSettingViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentPatientProfileSettingBinding = viewDataBinding
        // setUpAppointmentlistingRecyclerview()

        userimage =
            if (fragmentPatientProfileSettingViewModel?.appSharedPref?.userImage != null && !fragmentPatientProfileSettingViewModel?.appSharedPref?.userImage.equals(
                    ""
                )
            ) {
                fragmentPatientProfileSettingViewModel?.appSharedPref?.userImage!!
            } else {
                ""
            }

        useremail =
            if (fragmentPatientProfileSettingViewModel?.appSharedPref?.userEmail != null && !fragmentPatientProfileSettingViewModel?.appSharedPref?.userEmail.equals(
                    ""
                )
            ) {
                fragmentPatientProfileSettingViewModel?.appSharedPref?.userEmail!!
            } else {
                ""
            }
        userName =
            if (fragmentPatientProfileSettingViewModel?.appSharedPref?.userName != null && !fragmentPatientProfileSettingViewModel?.appSharedPref?.userName.equals(
                    ""
                )
            ) {
                fragmentPatientProfileSettingViewModel?.appSharedPref?.userName!!
            } else {
                ""
            }
        if (userName != "") {
            fragmentPatientProfileSettingBinding?.txtUserName?.text = userName
        }
        if (useremail != "") {
            fragmentPatientProfileSettingBinding?.txtUserEmail?.text = useremail
        }
        if (userimage != "") {
            Glide.with(this.requireActivity())
                .load(BaseMediaUrls.USERIMAGE.url + (userimage))
                .into(fragmentPatientProfileSettingBinding?.imgRootscareProfileImage!!)
        } else {
            Glide.with(this.requireActivity())
                .load(resources.getDrawable(R.drawable.profile_no_image))
                .into(fragmentPatientProfileSettingBinding?.imgRootscareProfileImage!!)
        }

        fragmentPatientProfileSettingBinding?.imgEditProfilr?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentProfile.newInstance()
            )

        }
        fragmentPatientProfileSettingBinding?.txtPatientsettingProfile?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentProfile.newInstance()
            )

        }

        fragmentPatientProfileSettingBinding?.txtPatientsettingDeactivateAccount?.setOnClickListener {

            CommonDialog.showDialog(requireContext(), object :
                DialogClickCallback {
                override fun onDismiss() {
                }

                override fun onConfirm() {
                    if (isNetworkConnected) {
                        baseActivity?.showLoading()
                        val appointmentRequest = AppointmentRequest()
                        appointmentRequest.userId =
                            fragmentPatientProfileSettingViewModel?.appSharedPref?.userId
//                          appointmentRequest?.userId="11"

                        fragmentPatientProfileSettingViewModel?.apideactivateuser(
                            appointmentRequest
                        )

                    } else {
                        Toast.makeText(
                            activity,
                            "Please check your network connection.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

            }, "Inactivate Account", "Do you really want to inactivate your account?")
        }


    }

    override fun successDeactivateAccountResponse(deactivateAccountResponse: DeactivateAccountResponse?) {
        baseActivity?.hideLoading()
        if (deactivateAccountResponse?.code.equals("200")) {
            logout()
        } else {
            Toast.makeText(activity, deactivateAccountResponse?.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun errorDeactivateAccountResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }


    private fun logout() {
        fragmentPatientProfileSettingViewModel?.appSharedPref?.deleteUserId()
        fragmentPatientProfileSettingViewModel?.appSharedPref?.deleteIsAppointmentType()
        moveToNewActivity()
   }

    private fun moveToNewActivity() {
        val i = Intent(activity, LoginActivity::class.java)
        startActivity(i)
        (activity as Activity?)!!.overridePendingTransition(0, 0)
        activity?.finishAffinity()
    }
}