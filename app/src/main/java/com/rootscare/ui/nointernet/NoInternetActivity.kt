package com.rootscare.ui.nointernet

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.ActivityNoInternetBinding
import com.rootscare.ui.base.BaseActivity

class NoInternetActivity : BaseActivity<ActivityNoInternetBinding, NoInternetViewModel>(),
    NoInternetNavigator, BaseActivity.CloseOnInternetActivity {

    companion object {
        private val TAG = NoInternetActivity::class.java.simpleName

        fun newIntent(activity: Activity): Intent {
            return Intent(activity, NoInternetActivity::class.java)
        }

        fun newIntent(activity: Context): Intent {
            return Intent(activity, NoInternetActivity::class.java).putExtra("boolean", true)
        }
    }

    private var activityNoInternetBinding: ActivityNoInternetBinding? = null

    private var noInternetViewModel: NoInternetViewModel? = null

    override val bindingVariable: Int = BR.viewModel

    override val layoutId: Int = R.layout.activity_no_internet

    override val viewModel: NoInternetViewModel
        get() {
            noInternetViewModel = ViewModelProviders.of(this).get(NoInternetViewModel::class.java)
            return noInternetViewModel as NoInternetViewModel
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //        getWindow().setBackgroundDrawableResource(R.raw.no_internet_conenction_image);

        activityNoInternetBinding = viewDataBinding
        noInternetViewModel!!.navigator = this

        // setStatusBarHidden(this@NoInternetActivity)

        Glide.with(this@NoInternetActivity).load(R.raw.no_internet_conenction_image)
            .into(activityNoInternetBinding!!.imageNoInternet)

        setCloseOnInternetActivity(this)

        activityNoInternetBinding?.imgClose?.setOnClickListener {
        //    finishAffinity()
            finish()
        }
    }


    override fun reloadPageAfterConnectedToInternet() {
    }


    override fun onCloseActivity() {
      this.finish()
    }


}