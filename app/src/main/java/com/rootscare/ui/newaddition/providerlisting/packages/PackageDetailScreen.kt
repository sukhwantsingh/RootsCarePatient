package com.rootscare.serviceprovider.ui.pricelistss

import android.os.Bundle
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.ActivityDetailPackageBinding
import com.rootscare.ui.base.BaseActivity
import com.rootscare.ui.newaddition.providerlisting.packages.adapter.AdapterLabTestsIncluded
import com.rootscare.ui.newaddition.providerlisting.packages.models.ModelPackageDetails
import com.rootscare.utilitycommon.SUCCESS_CODE
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


class PackageDetailScreen : BaseActivity<ActivityDetailPackageBinding, ManagePriceViewModel>(),
    ManagePriceNavigator {
    private var binding: ActivityDetailPackageBinding? = null
    private var mViewModel: ManagePriceViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_detail_package
    override val viewModel: ManagePriceViewModel
        get() {
            mViewModel = ViewModelProviders.of(this).get(ManagePriceViewModel::class.java)
            return mViewModel as ManagePriceViewModel
        }
    private var mTestsAdapter: AdapterLabTestsIncluded? = null
    var minPrice = 0
    var maxPrice = 0

    companion object {
        var packId = ""
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        mViewModel?.navigator = this
        mTestsAdapter = AdapterLabTestsIncluded(this)
        binding?.topToolbar?.run {
            tvHeader.text = getString(R.string.package_detail)
            btnBack.setOnClickListener { finish() }
        }

        initViews()
        fetchPackageDetail()
    }

    private fun initViews() {
        binding?.rvTestsIncluded?.adapter = mTestsAdapter

    }

    private fun fetchPackageDetail() {
        if (isNetworkConnected) {
            showLoading()
            val jsonObject = JsonObject().apply {
                addProperty("package_id", packId)
                addProperty("provider_id", PackageListScreen.providerId)
                addProperty("lgoin_user_id", mViewModel?.appSharedPref?.userId)
            }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            mViewModel?.getLabPackagesDetails(body)
        } else {
            showToast(getString(R.string.check_network_connection))
        }

    }


    override fun onSuccessPackageDetails(response: ModelPackageDetails?) {
        try {
            hideLoading()
            if (response?.code.equals(SUCCESS_CODE)) {
                response?.result?.let {
                    binding?.run {
                        minPrice = it.minrp?.toInt() ?: 0
                        maxPrice = it.maxrp?.toInt() ?: 0
                        setVariable(BR.node, it)
                        mTestsAdapter?.submitList(it.task_name?.toMutableList())
                    }

                }
            }

        } catch (e: Exception) {
            hideLoading()
            println(e)
        }
    }

    override fun onThrowable(throwable: Throwable) {
        hideLoading()
        showToast(throwable.message ?: getString(R.string.something_went_wrong))
    }

    override fun reloadPageAfterConnectedToInternet() {

    }
}