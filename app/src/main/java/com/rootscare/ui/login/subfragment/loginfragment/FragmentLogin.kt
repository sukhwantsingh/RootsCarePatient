package com.rootscare.ui.login.subfragment.loginfragment

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.dialog.CommonDialog
import com.interfaces.DialogClickCallback
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.loginrequest.LoginRequest
import com.rootscare.databinding.FragmentLoginBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.login.LoginActivity
import com.rootscare.ui.login.ModelLoginResponse
import com.rootscare.utilitycommon.LanguageModes
import com.rootscare.utilitycommon.SUCCESS_CODE
import com.rootscare.utilitycommon.navigate
import java.util.regex.Pattern

class FragmentLogin : BaseFragment<FragmentLoginBinding, FragmentLoginViewModel>(),
    FragmentLoginNavigator {
    private var fragmentLoginBinding: FragmentLoginBinding? = null
    private var fragmentLoginViewModel: FragmentLoginViewModel? = null

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_login
    override val viewModel: FragmentLoginViewModel
        get() {
            fragmentLoginViewModel =
                ViewModelProviders.of(this).get(FragmentLoginViewModel::class.java)
            return fragmentLoginViewModel as FragmentLoginViewModel
        }

    companion object {
        val TAG: String = FragmentLogin::class.java.simpleName
        fun newInstance(): FragmentLogin {
            val args = Bundle()
            val fragment = FragmentLogin()
            fragment.arguments = args
            return fragment
        }
    }
    private var isRemChecked = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentLoginViewModel?.navigator = this
        Log.e(TAG, "fcmToken: ${fragmentLoginViewModel?.appSharedPref?.accessToken}")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentLoginBinding = viewDataBinding
        setLanguagePrefernce()
        setupRemember()
        fragmentLoginBinding?.checkboxLoginremember?.setOnClickListener {
            isRemChecked = fragmentLoginBinding?.checkboxLoginremember?.isChecked ?: false
        }
        fragmentLoginBinding?.btnRootscareLogin?.setOnClickListener {
            val emailOrPassword = fragmentLoginBinding?.edtEmailOrPhone?.text?.toString()
            val password = fragmentLoginBinding?.edtPassword?.text?.toString()
            val lgPref = fragmentLoginViewModel?.appSharedPref?.languagePref  // AR/ENG
            if (!(emailOrPassword?.isEmpty()!!)) {
                hideKeyboard()
                val requestUserLogin = LoginRequest()
                requestUserLogin.device_lang = lgPref
                requestUserLogin.device_type = "android"
                requestUserLogin.fcm_token = fragmentLoginViewModel?.appSharedPref?.accessToken

                val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
                if (emailOrPassword.matches(emailPattern.toRegex())) {
                    if (checkEmailAndPasswordValidation(emailOrPassword, password!!)) {
                        requestUserLogin.emailPhone = emailOrPassword
                        requestUserLogin.password = password
                     }
                } else {
                    if (checkPhoneAndPasswordValidation(emailOrPassword, password!!)) {
                        requestUserLogin.emailPhone = emailOrPassword
                        requestUserLogin.password = password
                      }
                }

                // fire api
                if (isNetworkConnected) {
                    baseActivity?.showLoading()
                    fragmentLoginViewModel?.apiPatientLogin(requestUserLogin)
                } else {
                    showToast(getString(R.string.check_network_connection))
                }

            } else {
                showToast(getString(R.string.emal_pwd_cannot_empty))

            }

        }
        fragmentLoginBinding?.txtForgotPassword?.setOnClickListener {
            (activity as? LoginActivity)?.setCurrentItem(3, true)
        }
        fragmentLoginBinding?.tvbtnCreteAc?.setOnClickListener {
            (activity as? LoginActivity)?.setCurrentItem(1, true)
        }

    }

    private fun setupRemember() {
        isRemChecked = fragmentLoginViewModel?.appSharedPref?.isloginremember ?: false
        fragmentLoginBinding?.checkboxLoginremember?.isChecked = isRemChecked

        if(isRemChecked) {
            fragmentLoginBinding?.edtEmailOrPhone?.setText(fragmentLoginViewModel?.appSharedPref?.remUsername)
            fragmentLoginBinding?.edtPassword?.setText(fragmentLoginViewModel?.appSharedPref?.remPwd)
        } else {
            fragmentLoginBinding?.edtEmailOrPhone?.setText("")
            fragmentLoginBinding?.edtPassword?.setText("")
        }

    }

    private fun setLanguagePrefernce() {
        fragmentLoginBinding?.run {
        if (fragmentLoginViewModel?.appSharedPref?.languagePref.equals(LanguageModes.ENG.get(), ignoreCase = true)) {
            tvhEng.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lang_pref_select))
            tvhAr.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        } else {
            tvhAr.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.lang_pref_select))
            tvhEng.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
       tvhEng.setOnClickListener {
                try {
                    changeLabgPreference(LanguageModes.ENG.get())
                } catch (e:Exception){
                    println(e)
                }

            }
       tvhAr.setOnClickListener {
                try {
                    changeLabgPreference(LanguageModes.AR.get())
                } catch (e:Exception){
                    println(e)
                }
            }
       }
    }

    private fun changeLabgPreference(lgchg: String){
        fragmentLoginViewModel?.appSharedPref?.languagePref = lgchg
        navigate<LoginActivity>()
        activity?.finish()

    }

    //Validation checking for email and password
    private fun checkEmailAndPasswordValidation(email: String, password: String): Boolean {

        val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"
        if (!email.matches(emailPattern.toRegex())) {
            showToast(getString(R.string.enter_valid_email))
            return false
        }
        if (!isValidPassword(password)) {
            showToast( getString(R.string.plz_enter_pwd))
            return false
        }

        return true
    }

    private fun isValidEmailId(email: String): Boolean {
        return Pattern.compile(
            "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                    + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                    + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                    + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                    + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
        ).matcher(email).matches()
    }

    //Validation checking for phone number and password
    private fun checkPhoneAndPasswordValidation(phone: String, password: String): Boolean {


        if (!isValidMobile(phone)) {
            //  activityLoginBinding?.edtEmailOrPhone?.setError("Invalid mobile number")
            showToast(getString(R.string.enter_valid_email_mobile_no))
            return false
        }
        if (!isValidPassword(password)) {
            showToast(getString(R.string.plz_enter_pwd))
            return false
        }

        return true
    }


    //Mobile number validation method
    private fun isValidMobile(phone: String): Boolean {
        return !TextUtils.isEmpty(phone) && Patterns.PHONE.matcher(phone).matches()
    }

    //Password validation method
    private fun isValidPassword(s: String): Boolean {
        val PASSWORD_PATTERN = Pattern.compile(
            "[a-zA-Z0-9!@#$]{8,24}"
        )

//        return !TextUtils.isEmpty(s) && PASSWORD_PATTERN.matcher(s).matches()

        return !TextUtils.isEmpty(s)
    }

    override fun successLoginResponse(loginResponse: ModelLoginResponse?) {
        baseActivity?.hideLoading()
        if (loginResponse?.code.equals(SUCCESS_CODE,ignoreCase = true)) {
          fragmentLoginViewModel?.run {
                appSharedPref?.deleteUserId()
              fragmentLoginViewModel?.appSharedPref?.isLoggedIn = true
              fragmentLoginViewModel?.appSharedPref?.isloginremember = isRemChecked
              fragmentLoginViewModel?.appSharedPref?.plcKey = loginResponse?.result?.place_key.orEmpty()

              if(isRemChecked){
                  fragmentLoginViewModel?.appSharedPref?.remUsername = fragmentLoginBinding?.edtEmailOrPhone?.text?.toString()
                  fragmentLoginViewModel?.appSharedPref?.remPwd = fragmentLoginBinding?.edtPassword?.text?.toString()
              }

                appSharedPref?.userCurrentLocation = (loginResponse?.result?.current_address ?: "NA").trim()
                appSharedPref?.userId = (loginResponse?.result?.user_id?:"").trim()
                appSharedPref?.userName = (loginResponse?.result?.first_name + " " + loginResponse?.result?.last_name).trim()
                appSharedPref?.userEmail = loginResponse?.result?.email
                appSharedPref?.userImage = loginResponse?.result?.image
                appSharedPref?.workArea = loginResponse?.result?.work_area
                appSharedPref?.currencySymbol = loginResponse?.result?.currency_symbol
            }

            startActivity(activity?.let { HomeActivity.newIntent(it) })
            activity?.finish()
        } else {
              CommonDialog.showDialogForSingleButton(requireActivity(), object : DialogClickCallback {
            }, null, loginResponse?.message?:"")
        }

    }

    override fun errorLoginResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            showToast(getString(R.string.something_went_wrong))
        }
    }
}