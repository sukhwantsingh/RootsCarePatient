package com.rootscare.ui.login.subfragment.registrationfragment

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.TextPaint
import android.text.TextUtils
import android.text.style.ClickableSpan
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.core.text.parseAsHtml
import androidx.lifecycle.ViewModelProviders
import com.dialog.CommonDialog
import com.interfaces.DialogClickCallback
import com.interfaces.DropDownDialogCallBack
import com.latikaseafood.ui.base.AppData
import com.model.ModelServiceFor
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.registrationrequest.registrationotpsendrequest.RegistrationSendOTPRequest
import com.rootscare.data.model.api.response.registrationresponse.registrationsendotpresponse.RegistrationSendOTPResponse
import com.rootscare.databinding.FragmentRegistrationBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.login.LoginActivity
import com.rootscare.ui.supportmore.CommonWebviewScreen
import com.rootscare.ui.supportmore.OPEN_FOR
import com.rootscare.ui.supportmore.OPEN_FOR_PRIVACY
import com.rootscare.ui.supportmore.OPEN_FOR_TERMS
import com.rootscare.utilitycommon.*
import com.rootscare.utils.PreferenceUtility
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class FragmentRegistration : BaseFragment<FragmentRegistrationBinding, FragmentRegistrationViewModel>(),
    FragmentRegistrationNavigator {
    private var binding: FragmentRegistrationBinding? = null
    private var mViewModel: FragmentRegistrationViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_registration
    override val viewModel: FragmentRegistrationViewModel
        get() {
            mViewModel =
                ViewModelProviders.of(this).get(FragmentRegistrationViewModel::class.java)
            return mViewModel as FragmentRegistrationViewModel
        }

    companion object {
        val TAG = FragmentRegistration::class.java.simpleName
        fun newInstance(): FragmentRegistration {
            val args = Bundle()
            val fragment = FragmentRegistration()
            fragment.arguments = args
            return fragment
        }
    }
    private val workFromList : ArrayList<String?> by lazy { ArrayList() }
    private val workFromListMap : HashMap<String?, String?> by lazy { HashMap() }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel?.navigator = this

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        binding?.btnCreateAcc?.setOnClickListener {
            if (checkFieldValidation()) {
                if (isNetworkConnected) {

                    var mobile =  binding?.edtRegPhonenumber?.text?.toString()?.trim().orEmpty()
                    val cc_ =  binding?.edtCc?.text?.toString()?.trim().orEmpty()
                    mobile = cc_ + mobile

                    val registrationSendOTPRequest = RegistrationSendOTPRequest()
                    registrationSendOTPRequest.firstName =  binding?.edtRegFirstname?.text?.toString()?.trim()
                    registrationSendOTPRequest.lastName = ""
                    registrationSendOTPRequest.phoneNumber =  mobile
                    registrationSendOTPRequest.email = binding?.edtRegEmailaddress?.text?.toString()?.trim()
                    registrationSendOTPRequest.idNumber =  binding?.edtNationalId?.text?.toString()?.trim()
                    registrationSendOTPRequest.password = binding?.edtRegPassword?.text?.toString()?.trim()
                    registrationSendOTPRequest.confirmPassword = binding?.edtRegConfirmPassword?.text?.toString()?.trim()
                    registrationSendOTPRequest.fcmToken =  mViewModel?.appSharedPref?.accessToken
                    registrationSendOTPRequest.deviceType = "android"
                    registrationSendOTPRequest.work_area = binding?.tvWorkFrom?.text?.toString() ?: ""


                    AppData.registrationRequest?.work_area = binding?.tvWorkFrom?.text?.toString() ?: ""
                    AppData.registrationRequest?.firstName = binding?.edtRegFirstname?.text?.toString()?.trim()
                    AppData.registrationRequest?.lastName = ""
                    AppData.registrationRequest?.phoneNumber =  mobile
                    AppData.registrationRequest?.email =  binding?.edtRegEmailaddress?.text?.toString()?.trim()
                    AppData.registrationRequest?.idNumber =  binding?.edtNationalId?.text?.toString()?.trim()
                    AppData.registrationRequest?.password =  binding?.edtRegPassword?.text?.toString()?.trim()
                    AppData.registrationRequest?.confirmPassword = binding?.edtRegConfirmPassword?.text?.toString()?.trim()

                   baseActivity?.hideKeyboard()
                   baseActivity?.showLoading()
                   mViewModel?.apipatientregistrationotpsend(registrationSendOTPRequest)

                } else {
                  showToast(getString(R.string.network_unavailable))
                }
            }
        }

        initViews()
        fetchServiceForApi()
    }

    fun initViews() {
        val termsPolicyText = "${getString(R.string.by_creating_acc_you_agree)}<font color='#17181A'><br><b>${getString(R.string.terms_of_service)}</b></font> ${getString(R.string.and)} <font color='#17181A'><b>${getString(R.string.privacy_policy)}</b></font>"
        binding?.tvTermsPolicy?.text = termsPolicyText.parseAsHtml()

        setClickableString(termsPolicyText.parseAsHtml().toString(), binding?.tvTermsPolicy,
        arrayOf(getString(R.string.terms_of_service), getString(R.string.privacy_policy)), arrayOf(termsCallback, privacyCallback))

        binding?.imageViewBack?.setOnClickListener { (activity as LoginActivity).onBackPressed() }
        binding?.tvhLoginHere?.setOnClickListener { (activity as LoginActivity).onBackPressed() }

        binding?.tvWorkFrom?.setOnClickListener {
            CommonDialog.showDialogForDropDownList(this.requireActivity(), getString(R.string.select),
                workFromList, object : DropDownDialogCallBack {
                    override fun onConfirm(text: String) {
                        binding?.run {
                            if(tvWorkFrom.text.toString().equals(text,true).not()){
                                tvWorkFrom.text = text
                            }
                            val mCode = workFromListMap[text].orEmpty()
                            edtCc.setText(mCode)
                        }
                    }
                })
        }

    }

    private var termsCallback: ClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            // open terms and conditions
            try {
                 navigate<CommonWebviewScreen>(listOf(IntentParams(OPEN_FOR, OPEN_FOR_TERMS)))
            } catch (e: Exception) {
                println(e)
            }
        }

        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
          //  ds.color = Color.parseColor("#000000")
            ds.typeface = Typeface.create("font/rubik_medium.ttf", Typeface.BOLD)
            ds.isUnderlineText = false
        }
    }
    private var privacyCallback: ClickableSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            // open PP
            try {
             navigate<CommonWebviewScreen>(listOf(IntentParams(OPEN_FOR, OPEN_FOR_PRIVACY)))
            } catch (e: Exception) {
                println(e)
            }
        }
        override fun updateDrawState(ds: TextPaint) {
            super.updateDrawState(ds)
           // ds.color = Color.parseColor("#000000");
            ds.typeface = Typeface.create("font/rubik_medium.ttf", Typeface.BOLD)
            ds.isUnderlineText = false
        }
    }

    private fun checkFieldValidation(): Boolean {
        Log.wtf("vll", "checkFieldValidation: ${isStartWithIdExact()}")

        return when {
            binding?.edtRegFirstname?.text.isNullOrBlank() -> {
                binding?.edtRegFirstname?.error = getString(R.string.plz_enter_your_name)
                binding?.edtRegFirstname?.requestFocus()
                false
            }
            binding?.edtRegEmailaddress?.text.isNullOrBlank() -> {
                binding?.edtRegEmailaddress?.error = getString(R.string.email_canot_empty)
                binding?.edtRegEmailaddress?.requestFocus()
                false
            }
            !binding?.edtRegEmailaddress?.text?.toString()!!.matches(emailPattern.toRegex()) -> {
                binding?.edtRegEmailaddress?.error = getString(R.string.enter_valid_email)
                binding?.edtRegEmailaddress?.requestFocus()
                false
            }
            binding?.tvWorkFrom?.text.isNullOrBlank() -> {
                showToast(getString(R.string.required_service_area))
                false
            }
            !isValidPassword(binding?.edtRegPhonenumber?.text?.toString()!!) -> {
                binding?.edtRegPhonenumber?.error = getString(R.string.plz_enter_mobile)
                binding?.edtRegPhonenumber?.requestFocus()
                false
            }
           !isValidMobile(binding?.edtRegPhonenumber?.text.toString().trim())-> {
                binding?.edtRegPhonenumber?.error = getString(R.string.invalid_mobile_number)
                binding?.edtRegPhonenumber?.requestFocus()
                 false
            }

            (binding?.edtRegPhonenumber?.toString()?.length ?: 0) < 9 -> {
                binding?.edtRegPhonenumber?.error = getString(R.string.use_mobile_number_note)
                binding?.edtRegPhonenumber?.requestFocus()
                false
            }


//           binding?.txtRegDob?.text.isNullOrBlank() -> {
//                showToast("Please select your dob!")
//                false
//            }
            binding?.edtNationalId?.text.isNullOrBlank() -> {
                binding?.edtNationalId?.error = getString(R.string.plz_provide_idnumber)
                binding?.edtNationalId?.requestFocus()
                false
            }

            isStartWithIdExact().not() -> {
                binding?.edtNationalId?.error = getString(R.string.id_must_start_from_1_or_2)
                binding?.edtNationalId?.requestFocus()
                false
            }

            ((binding?.edtNationalId?.text?.toString()?.length ?: 0) < 10) || ((binding?.edtNationalId?.text?.toString()?.length ?: 0) > 15) -> {
             binding?.edtNationalId?.error = getString(R.string.id_number_must_be_10)
             binding?.edtNationalId?.requestFocus()
               false
            }

       /*    selectedGender.isBlank() -> {
                showToast("Please select gender!")
                false
            }
            */
            binding?.edtRegPassword?.text.isNullOrBlank() -> {
                binding?.edtRegPassword?.error =getString(R.string.plz_enter_pwd)
                binding?.edtRegPassword?.requestFocus()
                false
            }
            (binding?.edtRegPassword?.text?.toString()?.length ?: 0) < 8 -> {
                binding?.edtRegPassword?.error = getString(R.string.must_be_at_least_8_characters)
                binding?.edtRegPassword?.requestFocus()
                false
            }
            binding?.edtRegConfirmPassword?.text.isNullOrBlank() -> {
                binding?.edtRegConfirmPassword?.error = getString(R.string.plz_enter_cnf_pwd)
                binding?.edtRegConfirmPassword?.requestFocus()
                false
            }
            (binding?.edtRegConfirmPassword?.text?.toString()?.length ?: 0) < 8 -> {
                binding?.edtRegConfirmPassword?.error = getString(R.string.must_be_at_least_8_characters)
                binding?.edtRegConfirmPassword?.requestFocus()
                false
            }
            !(binding?.edtRegPassword?.text?.toString().equals(binding?.edtRegConfirmPassword?.text?.toString())) -> {
                binding?.edtRegConfirmPassword?.error =getString(R.string.both_password_must_matched)
                binding?.edtRegConfirmPassword?.requestFocus()
                false
            }

            else -> true
        }
    }

    private fun isStartWithIdExact() = binding?.edtNationalId?.text.toString().startsWith("1") || binding?.edtNationalId?.text.toString().startsWith("2")

    private fun fetchServiceForApi() {
        if (isNetworkConnected) {
            mViewModel?.apiServiceFor()
        } else {
            showToast(getString(R.string.check_network_connection))
        }
    }
    override fun onSuccessServiceFor(specialityList: ModelServiceFor?) {
        baseActivity?.hideLoading()
        if (specialityList?.code.equals(SUCCESS_CODE)) {
            CoroutineScope(Dispatchers.IO).launch {
                specialityList?.result?.let {
                    if (it.isNotEmpty()) {
                        workFromList.clear(); workFromListMap.clear()
                        it.forEach { lst ->
                            workFromList.add(lst?.name)
                            workFromListMap[lst?.name] = lst?.country_code
                        }
                    }
                }
            }
        } else {
            showToast(specialityList?.message ?: "")
        }
    }


    // Mobile number validation method
    override fun successRegistrationSendOTPResponse(registrationResponse: RegistrationSendOTPResponse?) {
        baseActivity?.hideLoading()
        if (registrationResponse?.code.equals(SUCCESS_CODE)) {

            CommonDialog.showDialogForSingleButton(requireActivity(), object : DialogClickCallback {
                override fun onConfirm() {
                    restForm()
                    (activity as? LoginActivity)?.setCurrentItem(2, true)
                }
            }, getString(R.string.registration), registrationResponse?.message?:"")
        } else {
            CommonDialog.showDialogForSingleButton(requireActivity(), object : DialogClickCallback {
            }, getString(R.string.registration), registrationResponse?.message?:"")
        }
    }
    private fun restForm() {
       // imgIdentity = null
      //  selectedGender = ""
        binding?.apply {
            edtRegFirstname.setText("")
            edtRegEmailaddress.setText("")
            edtRegPhonenumber.setText("")
            edtNationalId.setText("")
            edtRegPassword.setText("")
            edtRegConfirmPassword.setText("")
//            txtRegDob.setText("")
        }


    }
    override fun errorRegistrationSendOTPResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
          showToast(getString(R.string.something_went_wrong))
        }
    }


}