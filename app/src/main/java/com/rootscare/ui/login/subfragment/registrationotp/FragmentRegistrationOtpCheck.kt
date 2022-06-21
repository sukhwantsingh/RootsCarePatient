package com.rootscare.ui.login.subfragment.registrationotp

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.dialog.CommonDialog
import com.interfaces.DialogClickCallback
import com.latikaseafood.ui.base.AppData
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.registrationrequest.registrationotpcheckrequest.RegistrationCheckOTPRequest
import com.rootscare.data.model.api.response.registrationresponse.registrationcheckotpresponse.RegistrationCheckOTPResponse
import com.rootscare.databinding.FragmentRegistrationOtpBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.login.LoginActivity
import com.rootscare.ui.login.subfragment.registrationfragment.FragmentRegistration
import com.rootscare.utilitycommon.SUCCESS_CODE
import com.rootscare.utils.PreferenceUtility

class FragmentRegistrationOtpCheck: BaseFragment<FragmentRegistrationOtpBinding, FragmentRegistrationOtpCheckViewModel>(), FragmentRegistrationOtpCheckNavigator {

    private var fragmentRegistrationOtpBinding: FragmentRegistrationOtpBinding? = null
    private var fragmentRegistrationOtpCheckViewModel: FragmentRegistrationOtpCheckViewModel? = null

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_registration_otp
    override val viewModel: FragmentRegistrationOtpCheckViewModel
        get() {
            fragmentRegistrationOtpCheckViewModel = ViewModelProviders.of(this).get(FragmentRegistrationOtpCheckViewModel::class.java!!)
            return fragmentRegistrationOtpCheckViewModel as FragmentRegistrationOtpCheckViewModel
        }

    companion object {
        val TAG = FragmentRegistrationOtpCheck::class.java.simpleName
        fun newInstance(): FragmentRegistrationOtpCheck {
            val args = Bundle()
            val fragment = FragmentRegistrationOtpCheck()
            fragment.arguments = args
            return fragment
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentRegistrationOtpCheckViewModel?.navigator = this

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentRegistrationOtpBinding = viewDataBinding
        fragmentRegistrationOtpBinding?.btnSubmit?.setOnClickListener {  apiHitSubmitOtp() }
    }

    private fun apiHitSubmitOtp() {
        if(fragmentRegistrationOtpBinding?.firstPinView?.text.isNullOrBlank().not()){
            if(isNetworkConnected) {
                val registrationCheckOTPRequest = RegistrationCheckOTPRequest().apply {
                    work_area = AppData.registrationRequest?.work_area
                    firstName = AppData.registrationRequest?.firstName
                    lastName = AppData.registrationRequest?.lastName
                    phoneNumber = AppData.registrationRequest?.phoneNumber
                    email= AppData.registrationRequest?.email
                    idNumber = AppData.registrationRequest?.idNumber
                    password = AppData.registrationRequest?.password
                    confirmPassword = AppData.registrationRequest?.confirmPassword
                    code = fragmentRegistrationOtpBinding?.firstPinView?.text?.toString()
                    fcmToken = fragmentRegistrationOtpCheckViewModel?.appSharedPref?.accessToken
                    deviceType = "android"
                    }

                baseActivity?.hideKeyboard()
                baseActivity?.showLoading()
                fragmentRegistrationOtpCheckViewModel?.apipatientregistrationotpcheck(registrationCheckOTPRequest)

            }else{
                showToast(getString(R.string.network_unavailable))
            }

        } else{
            showToast(getString(R.string.plz_enter_otp))
        }
    }

    override fun successRegistrationCheckOTPResponse(response: RegistrationCheckOTPResponse?) {
        baseActivity?.hideLoading()
        if(response?.code.equals(SUCCESS_CODE)){
            CommonDialog.showDialogForSingleButton(requireActivity(), object : DialogClickCallback {
                override fun onConfirm() {
                    (activity as? LoginActivity)?.setCurrentItem(0, true)
                }
            }, getString(R.string.registration), response?.message?:"")
        } else {
          showToast(response?.message?:"")
        }
    }

    override fun errorRegistrationCheckOTPResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            showToast(getString(R.string.something_went_wrong))
        }
    }

}