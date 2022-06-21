package com.rootscare.ui.supportmore

import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.dialog.CommonDialog
import com.google.gson.JsonObject
import com.interfaces.DialogClickCallback
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.appointmentrequest.AppointmentRequest
import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.databinding.ActivitySupportAndMoreBinding

import com.rootscare.ui.base.BaseActivity
import com.rootscare.ui.splash.SplashActivity
import com.rootscare.ui.supportmore.bottomsheet.DialogThankyou
import com.rootscare.utilitycommon.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class SupportAndMore : BaseActivity<ActivitySupportAndMoreBinding, CommonViewModel>(),
    CommonActivityNavigator {
    private var binding: ActivitySupportAndMoreBinding? = null
    private var mViewModel: CommonViewModel? = null

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_support_and_more
    override val viewModel: CommonViewModel
        get() {
            mViewModel = ViewModelProviders.of(this).get(CommonViewModel::class.java)
            return mViewModel as CommonViewModel
        }

    private var langToChange = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel?.navigator = this
        binding = viewDataBinding
        binding?.topToolbar?.tvHeader?.text = getString(R.string.support_amp_more)
        binding?.topToolbar?.btnBack?.setOnClickListener { finish() }

        initViews()
    }

    private fun initViews() {
        setLanguagePrefernce()
        binding?.run {
            tvVersion.text = getAppVersionText()

            tvhEng.setOnClickListener {
                try {
                    showWarnMessage(LanguageModes.ENG.get())
                } catch (e:Exception){
                    println(e)
                }

            }
            tvhAr.setOnClickListener {
                try {
                    showWarnMessage(LanguageModes.AR.get())
                } catch (e:Exception){
                    println(e)
                }
            }

            tvhTermsnConditions.setOnClickListener {
                navigate<CommonWebviewScreen>(listOf(IntentParams(OPEN_FOR, OPEN_FOR_TERMS)))
            }
            tvhAboutRoot.setOnClickListener {
                navigate<CommonWebviewScreen>(listOf(IntentParams(OPEN_FOR, OPEN_FOR_ABOUT)))
            }
            tvhPrivacyp.setOnClickListener {
                 navigate<CommonWebviewScreen>(listOf(IntentParams(OPEN_FOR, OPEN_FOR_PRIVACY)))
            }

            tvhNeedsupport.setOnClickListener {
             navigate<NeedSupportScreen>()
            }
        }
    }

    private fun showWarnMessage(lgchg: String){
        langToChange = lgchg
       /*       binding?.run {
            when(lgToChange){
                LanguageModes.AR.get() -> {
                    try {
                        mViewModel?.appSharedPref?.languagePref = LanguageModes.AR.get()
                        tvhAr.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.lang_pref_select))
                        tvhEng.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.white))
                    } catch (e:Exception){
                        println(e)
                    }
                }
                LanguageModes.ENG.get() -> {
                    try {
                        mViewModel?.appSharedPref?.languagePref = LanguageModes.ENG.get()
                        tvhEng.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.lang_pref_select))
                        tvhAr.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.white))
                    }catch (e:Exception){
                        println(e)
                    }
                }
            }
        }*/

        CommonDialog.showDialog(this@SupportAndMore, object : DialogClickCallback {
            override fun onConfirm() {
                apiChangeLocal()
            }
        }, getString(R.string.change_language), getString(R.string.to_change_app_lannguage),getString(R.string.restart))

    }

   private fun setLanguagePrefernce() {
        if (mViewModel?.appSharedPref?.languagePref.equals(LanguageModes.ENG.get(), ignoreCase = true)) {
            binding?.tvhEng?.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.lang_pref_select))
            binding?.tvhAr?.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.white))
        } else {
            binding?.tvhAr?.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.lang_pref_select))
            binding?.tvhEng?.setBackgroundColor(ContextCompat.getColor(baseContext, R.color.white))
        }
     }

    private fun apiChangeLocal(){
        if (isNetworkConnected) {
            val jsonObject = JsonObject().apply {
                addProperty("login_user_id", mViewModel?.appSharedPref?.userId)
                addProperty("device_lang", langToChange)
            }

            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
             hideKeyboard()
            showLoading()
            mViewModel?.changeLanguage(body)
        } else {
            showToast(getString(R.string.check_network_connection))
        }
    }

    override fun onSuccessSubmitSupport(response: CommonResponse) {
        hideLoading()
        if (response.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            mViewModel?.appSharedPref?.languagePref = langToChange
            navigate<SplashActivity>()
            finishAffinity()
        } else {
            showToast(response.message ?: "")
        }

    }
    override fun errorInApi(throwable: Throwable?) {
        showToast(throwable?.message ?: "")
    }
    override fun reloadPageAfterConnectedToInternet() {


    }

}