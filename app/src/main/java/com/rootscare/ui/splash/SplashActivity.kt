package com.rootscare.ui.splash

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.text.parseAsHtml
import androidx.lifecycle.ViewModelProviders
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.ActivitySplashBinding
import com.rootscare.ui.base.BaseActivity
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.login.LoginActivity
import com.rootscare.ui.splash.model.NetworkAppCheck
import com.rootscare.utilitycommon.LanguageModes
import com.rootscare.utilitycommon.SUCCESS_CODE
import com.rootscare.utilitycommon.getAppVersionNumber
import com.rootscare.utilitycommon.openWebLink
import com.rootscare.utils.firebase.NotificationUtils
import io.branch.referral.Branch
import io.branch.referral.BranchError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.json.JSONObject


class SplashActivity : BaseActivity<ActivitySplashBinding, SplashViewModel>(), SplashNavigator {
    private var activitySplashBinding: ActivitySplashBinding? = null
    private var splashViewModel: SplashViewModel? = null
    private var notificationUtils: NotificationUtils? = null

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.activity_splash

    override val viewModel: SplashViewModel
        get() {
            splashViewModel = ViewModelProviders.of(this).get(SplashViewModel::class.java)
            return splashViewModel as SplashViewModel
        }

    companion object {
        val TAG = SplashActivity::class.java.simpleName

        fun newIntent(activity: Activity): Intent {
            return Intent(activity, SplashActivity::class.java)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashViewModel?.navigator = this
        activitySplashBinding = viewDataBinding
        notificationUtils = NotificationUtils(this@SplashActivity)
        setLanguagePrefernce()

          apiVersionCheck()
     //     Handler(Looper.getMainLooper()).postDelayed({ redirectToLogin() }, 1000L)
    }

    private fun setLanguagePrefernce() {
        if (splashViewModel?.appSharedPref?.languagePref.equals(LanguageModes.ENG.get(), ignoreCase = true)) {
            activitySplashBinding?.ivSplash?.setImageResource(R.drawable.ic_splash_)
           // changeDeviceLanguage(LanguageModes.ENG.getLangLocale())
        } else {
            activitySplashBinding?.ivSplash?.setImageResource(R.drawable.splash_arabic)
         //   changeDeviceLanguage(LanguageModes.AR.getLangLocale())
        }

    }

    override fun onResume() {
        super.onResume()
        // clear the notification area when the app is opened
        notificationUtils?.clearNotifications(applicationContext)
    }

    // Fetches reg id from shared preferences and displays on the screen
    private fun redirectToLogin() {
        if (splashViewModel?.appSharedPref?.isLoggedIn == true) {
            startActivity(HomeActivity.newIntent(this@SplashActivity))
            finish()
        } else {
            startActivity(LoginActivity.newIntent(this@SplashActivity))
            finish()
        }
    }

    override fun reloadPageAfterConnectedToInternet() {
    }

    override fun onStart() {
        super.onStart()
        // Branch init
        Branch.sessionBuilder(this).withCallback(branchListener).withData(this.intent?.data).init()
    }
    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        this.intent = intent

        // if activity is in foreground (or in backstack but partially visible) launch the same
        // activity will skip onStart, handle this case with reInit
        if (intent != null && intent.hasExtra("branch_force_new_session") && intent.getBooleanExtra("branch_force_new_session", false)) {
            Branch.sessionBuilder(this).withCallback(branchListener).reInit()
        }

    }

   private object branchListener : Branch.BranchReferralInitListener {
        override fun onInitFinished(referringParams: JSONObject?, error: BranchError?) {
            if (error == null) {
                Log.wtf("BRANCH SDK", referringParams.toString())
                // Retrieve deeplink keys from 'referringParams' and evaluate the values to determine where to route the user
                // Check '+clicked_branch_link' before deciding whether to use your Branch routing logic
            } else {
                Log.wtf("BRANCH SDK", error.message)
            }
        }
    }

    override fun onSuccessVersion(response: NetworkAppCheck?) {
        try {
            if (response?.code.equals(SUCCESS_CODE)) {
                response?.result?.let {
                    try {
                        if (getAppVersionNumber() >= it.getServerVersion()) {
                            redirectToLogin()
                        } else {
                            showUpdateDialogBeforeLogin(response)
                        }

                    } catch (e: java.lang.Exception) {

                    }

                }
            }
        } catch (e: Exception) {
        }
    }

    override fun errorInApi(throwable: Throwable?) {
        hideLoading()
        redirectToLogin()
    }

    private fun apiVersionCheck() {
        if (isNetworkConnected) { splashViewModel?.apiVersionCheck() }
    }

    private fun showUpdateDialogBeforeLogin(mNode: NetworkAppCheck?) {
        val view = layoutInflater.inflate(R.layout.layout_dialog_version_check, null, false)

        val mTxtTopHeader = view.findViewById<AppCompatTextView>(R.id.txt_welcome)
        val mTxtContent = view.findViewById<AppCompatTextView>(R.id.txt_update_content)

        val mTxtHelp = view.findViewById<AppCompatTextView>(R.id.tv_help)
        val mViewLine = view.findViewById<View>(R.id.view_line)

        val mBtnNoThankx = view.findViewById<AppCompatTextView>(R.id.btn_no_thanks)
        val mBtnUpdate = view.findViewById<AppCompatTextView>(R.id.btn_update)

        GlobalScope.launch(Dispatchers.Main + SupervisorJob()) {
            mNode?.result?.let {
                mTxtTopHeader.text = it.updTitle?.parseAsHtml()
                mTxtContent.text = it.updText?.parseAsHtml()
                mBtnNoThankx.text = it.skipText?.parseAsHtml()

                mBtnNoThankx.visibility = if (it.skipFlag == true) View.VISIBLE else View.INVISIBLE
                if (it.showHelp == true) {
                    mViewLine.visibility = View.VISIBLE; mTxtHelp.visibility = View.VISIBLE
                    mTxtHelp.text = "${it.helpTitle}: ${it.helpUrl}"
                } else {
                    mViewLine.visibility = View.GONE; mTxtHelp.visibility = View.GONE
                }
            }
        }

        val dialog: AlertDialog = AlertDialog.Builder(this)
            .setView(view).setCancelable(false).create()
        dialog.show()
        mBtnUpdate.setOnClickListener {
            openWebLink(mNode?.result?.rdrUrl)
            dialog.cancel()
            finish()

        }
        mBtnNoThankx.setOnClickListener {
            redirectToLogin()
            dialog.dismiss()
        }


    }
}