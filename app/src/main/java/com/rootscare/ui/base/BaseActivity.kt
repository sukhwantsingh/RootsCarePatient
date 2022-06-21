package com.rootscare.ui.base

import android.annotation.TargetApi
import android.app.Dialog
import android.content.*
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.rootscare.ui.nointernet.NoInternetActivity
import com.rootscare.ui.nointernet.NoInternetViewModel
import com.rootscare.utilitycommon.getAppLocale
import com.rootscare.utils.CommonUtils
import com.rootscare.utils.NetworkUtils
import com.facebook.appevents.AppEventsLogger
import java.util.*


abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel<*>> : AppCompatActivity(),
    BaseFragment.Callback {

    // TODO
    // this can probably depend on isLoading variable of BaseViewModel,
    // since its going to be common for all the activities
    private var mProgressDialog: Dialog? = null
    var viewDataBinding: T? = null
        private set
    private var mViewModel: V? = null
    private var mWifiReceiver: BroadcastReceiver? = null
    private var closeOnInternetActivity: CloseOnInternetActivity? = null

    /**
     * Override for set binding variable
     *
     * @return variable id
     */
    abstract val bindingVariable: Int

    /**
     * @return layout resource id
     */
    @get:LayoutRes
    abstract val layoutId: Int

    /**
     * Override for set view model
     *
     * @return view model instance
     */
    abstract val viewModel: V

    val isNetworkConnected: Boolean
        get() = NetworkUtils.isNetworkConnected(applicationContext)

    override fun onFragmentAttached() {

    }

    override fun onFragmentDetached(tag: String) {

    }

    lateinit var logger: AppEventsLogger

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        logger = AppEventsLogger.Companion.newLogger(this)

        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        performDataBinding()

        mWifiReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (isNetworkConnected) {
                    closeOnInternetActivity?.onCloseActivity()
                    reloadPageAfterConnectedToInternet()
                    Log.d("check_receiver", ": conencted")
                } else {
                    if (mViewModel is NoInternetViewModel) {

                    } else {
                        startActivity(NoInternetActivity.newIntent(context))
                    }
                    Log.d("check_receiver", ": not_connected")
                }
            }
        }
        registerWifiReceiver()


    }

    @TargetApi(Build.VERSION_CODES.M)
    fun hasPermission(permission: String): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterWifiReceiver()

    }

    // BroadCastReceiver for network connectivity
    /* Start */
    private fun unregisterWifiReceiver() {
        unregisterReceiver(mWifiReceiver)
    }

    private fun registerWifiReceiver() {
        val filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(mWifiReceiver, filter)
    }

    /* End */
    /* No internet activity */
    fun setCloseOnInternetActivity(closeOnInternetActivity: CloseOnInternetActivity) {
        this.closeOnInternetActivity = closeOnInternetActivity
    }

    interface CloseOnInternetActivity {

        fun onCloseActivity()
    }

    /* End */
    // To reload page after internet connected
    protected abstract fun reloadPageAfterConnectedToInternet()


    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun hideLoading() {
        if (null != mProgressDialog  && mProgressDialog?.isShowing == true) {
            mProgressDialog?.cancel()
        }
    }


    @TargetApi(Build.VERSION_CODES.M)
    fun requestPermissionsSafely(permissions: Array<String>, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode)
        }
    }

    fun showLoading(loadingForText:String? = null) {
        hideLoading()
        mProgressDialog = CommonUtils.showLoadingDialog(this, loadingForText)
    }

    private fun performDataBinding() {
        viewDataBinding = DataBindingUtil.setContentView(this, layoutId)
        this.mViewModel = if (mViewModel == null) viewModel else mViewModel
        viewDataBinding!!.setVariable(bindingVariable, mViewModel)
        viewDataBinding!!.executePendingBindings()
    }
    fun showToast(msg:String){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
    }

    override fun attachBaseContext(newBase: Context) {
        Log.e("langMode", ": ${getAppLocale()}")
        super.attachBaseContext(LangContextWrapper.wrap(newBase, getAppLocale()))
    }
}

class LangContextWrapper private constructor(base: Context) : ContextWrapper(base) {

    companion object {

        private val enLocale = Locale("en","US")
        private val arLocale = Locale("ar","SA")

        fun wrap(baseContext: Context, language: String): ContextWrapper {
            var wrappedContext = baseContext
            val config = Configuration(baseContext.resources.configuration)

            if (language.isNotBlank()) {
                val locale = returnOrCreateLocale(language)
                Locale.setDefault(locale)
                config.setLocale(locale)
                config.setLayoutDirection(locale)
                wrappedContext = baseContext.createConfigurationContext(config)
            }
            return LangContextWrapper(wrappedContext)
        }
     private fun
             returnOrCreateLocale(language: String): Locale {
            return when (language) {
                "en" -> enLocale
                "ar" -> arLocale
                else -> Locale(language)
            }
        }
    }
}