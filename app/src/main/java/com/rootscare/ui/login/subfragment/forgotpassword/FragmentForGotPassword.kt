package com.rootscare.ui.login.subfragment.forgotpassword

import android.app.Activity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.forgotpassword.forgotpasswordchangerequest.ForgotPasswordChangeRequest
import com.rootscare.data.model.api.request.forgotpassword.forgotpasswordemailrequest.ForgotPasswordSendEmailRequest
import com.rootscare.data.model.api.response.forgotpasswordresponse.forgotpasswordchangepassword.ForgotPasswordChangePasswordResponse
import com.rootscare.data.model.api.response.forgotpasswordresponse.forgotpasswordsendmailresponse.ForgotPasswordSendMailResponse
import com.rootscare.databinding.FragmentForgotPasswordBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.login.LoginActivity
import com.rootscare.ui.login.subfragment.registrationfragment.FragmentRegistration
import com.rootscare.utilitycommon.SUCCESS_CODE

class FragmentForGotPassword: BaseFragment<FragmentForgotPasswordBinding, FragmentForGotPasswordViewModel>(), FragmentForGotPasswordNavigator {
    private var binding: FragmentForgotPasswordBinding? = null
    private var fragmentForGotPasswordViewModel: FragmentForGotPasswordViewModel? = null
    private var registerEmailId=""
    private var sendOTP=""
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_forgot_password
    override val viewModel: FragmentForGotPasswordViewModel
        get() {
            fragmentForGotPasswordViewModel = ViewModelProviders.of(this).get(FragmentForGotPasswordViewModel::class.java!!)
            return fragmentForGotPasswordViewModel as FragmentForGotPasswordViewModel
        }

    companion object {
        val TAG = FragmentForGotPassword::class.java.simpleName
        fun newInstance(): FragmentForGotPassword {
            val args = Bundle()
            val fragment = FragmentForGotPassword()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentForGotPasswordViewModel!!.navigator = this
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        binding?.imageViewBack?.setOnClickListener {
            resetFields()
            (activity as LoginActivity).onBackPressed()
        }
        binding?.btnForgotpasswordSendMail?.setOnClickListener{ sendMailAPi()}
        binding?.txtSendAgain?.setOnClickListener { sendAgainMailAPi() }
        binding?.btnForgotpasswordSubmit?.setOnClickListener {
            if (checkFieldValidationForChangePassword()){
                if(isNetworkConnected){
                    baseActivity?.showLoading()
                    val reqBody = ForgotPasswordChangeRequest()
                    reqBody.emailId = registerEmailId
                    reqBody.code = binding?.firstPinView?.text?.toString()
                    reqBody.password = binding?.edtRootscareForgotPassword?.text?.toString()
                    fragmentForGotPasswordViewModel?.apiforgotchangepassword(reqBody)
                }else{
                    showToast(getString(R.string.check_network_connection))
                }
            }
        }
    }

    override fun successForgotPasswordSendMailResponse(forgotPasswordSendMailResponse: ForgotPasswordSendMailResponse?) {
        baseActivity?.hideLoading()
        if(forgotPasswordSendMailResponse?.code.equals(SUCCESS_CODE,ignoreCase = true)){
            Toast.makeText(activity, forgotPasswordSendMailResponse?.message, Toast.LENGTH_SHORT).show()
            sendOTP= forgotPasswordSendMailResponse?.result!!
            binding?.llEmailContent?.visibility=View.GONE
            binding?.llForgotOtpContain?.visibility=View.VISIBLE
        }else{
            Toast.makeText(activity, forgotPasswordSendMailResponse?.message, Toast.LENGTH_SHORT).show()
            binding?.llEmailContent?.visibility=View.VISIBLE
            binding?.llForgotOtpContain?.visibility=View.GONE
        }
        resetFields()
    }


    private fun sendMailAPi() {
        val emailOrPassword = binding?.edtEmail?.text?.toString() ?: ""
        if (emailOrPassword.isNotBlank()) {
            if (checkFieldValidation(emailOrPassword)) {
                if (isNetworkConnected) {
                    hideKeyboard()
                    baseActivity?.showLoading()
                    registerEmailId = binding?.edtEmail?.text?.toString()?.trim()?:""
                    val req = ForgotPasswordSendEmailRequest()
                    req.emailId = registerEmailId
                    fragmentForGotPasswordViewModel?.apiforgotpasswordemail(req)
                } else {
                    showToast(getString(R.string.check_network_connection))
                }
            }
        } else {
            binding?.edtEmail?.error = getString(R.string.email_canot_empty)
            binding?.edtEmail?.requestFocus()
        }


    }
    private fun sendAgainMailAPi() {
        if (isNetworkConnected) {
            hideKeyboard()
            baseActivity?.showLoading()
            val req = ForgotPasswordSendEmailRequest()
            req.emailId = registerEmailId
            fragmentForGotPasswordViewModel?.apiforgotpasswordemail(req)
        } else {
            showToast(getString(R.string.check_network_connection))
        }
    }


    override fun successForgotPasswordChangePasswordResponse(forgotPasswordChangePasswordResponse: ForgotPasswordChangePasswordResponse?) {
        baseActivity?.hideLoading()
        if (forgotPasswordChangePasswordResponse?.code.equals(SUCCESS_CODE,ignoreCase = true)){
            Toast.makeText(activity, forgotPasswordChangePasswordResponse?.message, Toast.LENGTH_SHORT).show()
            (activity as? LoginActivity)?.setCurrentItem(0, true)
        }else{
            Toast.makeText(activity, forgotPasswordChangePasswordResponse?.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun errorForgotPasswordSendMailResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            showToast(getString(R.string.something_went_wrong))
        }
    }

    private fun checkFieldValidation(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!email.matches(emailPattern.toRegex())) {
            binding?.edtEmail?.error = getString(R.string.enter_valid_email)
            binding?.edtEmail?.requestFocus()
            return false
        }
        return true
    }
    private fun checkFieldValidationForChangePassword(): Boolean {

        if (binding?.edtRootscareForgotPassword?.text.isNullOrBlank()) {
            binding?.edtRootscareForgotPassword?.error = getString(R.string.pwd_cannot_empty)
            binding?.edtRootscareForgotPassword?.requestFocus()
            return false
        }

        if((binding?.edtRootscareForgotPassword?.text?.toString()?.length ?: 0) < 8) {
            binding?.edtRootscareForgotPassword?.error = getString(R.string.must_be_at_least_8_characters)
            binding?.edtRootscareForgotPassword?.requestFocus()
            return false
        }

        if (binding?.firstPinView?.text.toString().trim() != sendOTP) {
          showToast(getString(R.string.enter_correct_otp))
            return false
        }
        return true
    }
    fun resetFields() {
        binding?.edtEmail?.setText("")
        binding?.firstPinView?.setText("")
        binding?.edtRootscareForgotPassword?.setText("")
    }
}