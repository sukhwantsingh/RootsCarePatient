package com.rootscare.ui.newaddition.providerlisting.packages.fragments

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentLabPackagesBinding
import com.rootscare.serviceprovider.ui.pricelistss.PackageDetailScreen
import com.rootscare.serviceprovider.ui.pricelistss.ManagePriceNavigator
import com.rootscare.serviceprovider.ui.pricelistss.ManagePriceViewModel
import com.rootscare.serviceprovider.ui.pricelistss.PackageListScreen
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.newaddition.providerlisting.packages.adapter.AdapterLabPackages
import com.rootscare.ui.newaddition.providerlisting.packages.adapter.OnLabPackageCallback
import com.rootscare.ui.newaddition.providerlisting.packages.models.ModelPackages
import com.rootscare.utilitycommon.SUCCESS_CODE
import com.rootscare.utilitycommon.navigate
import com.rootscare.utilitycommon.setCircularRemoteImageWithNoImage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class FragmentPackages : BaseFragment<FragmentLabPackagesBinding, ManagePriceViewModel>(),
    ManagePriceNavigator {

    private var binding: FragmentLabPackagesBinding? = null

    private var mViewModel: ManagePriceViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_lab_packages

    override val viewModel: ManagePriceViewModel
        get() {
            mViewModel = ViewModelProviders.of(this).get(ManagePriceViewModel::class.java)
            return mViewModel as ManagePriceViewModel
        }
    private var mPriceAdapter: AdapterLabPackages? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mPriceAdapter = activity?.let { AdapterLabPackages(it) }
        mViewModel?.navigator = this
    }

    companion object {

        @JvmStatic
        fun newInstance() = FragmentPackages()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        initViews()
    }

    private fun initViews() {
        binding?.rvPackages?.adapter = mPriceAdapter
        mPriceAdapter?.mCallback = object : OnLabPackageCallback {
            override fun onLabPackage(packId: String?) {
                PackageDetailScreen.packId = packId.orEmpty()
                navigate<PackageDetailScreen>()
            }
        }
        fetchPackages()
    }

    private fun fetchPackages() {
        if (isNetworkConnected) {
            val jsonObject = JsonObject().apply {
                addProperty("lgoin_user_id", mViewModel?.appSharedPref?.userId)
                addProperty("provider_id", PackageListScreen.providerId)
            }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            baseActivity?.showLoading()
            mViewModel?.getLabPackagesList(body)
        } else {
            noData(getString(R.string.check_network_connection))
        }

    }

    override fun onSuccessAfterGetTaskPrice(taskList: ModelPackages) {
        try {
            baseActivity?.hideLoading()
            if (taskList.code.equals(SUCCESS_CODE)) {
                binding?.run {
                    taskList.result.let { res ->
                        imgProfile.setCircularRemoteImageWithNoImage(res?.image)
                        tvUsername.text = res?.provider_name
                        tvhIsoCertificate.text = res?.iso_text.orEmpty()

                        res?.package_base_task.let {
                            if (it.isNullOrEmpty().not()) {
                                tvNoDate.visibility = View.GONE
                                rvPackages.visibility = View.VISIBLE

                                mPriceAdapter?.submitList(it?.toMutableList())
                            } else noData(taskList.message)
                        } ?: run { noData(taskList.message) }
                    }
                }
            }
        } catch (e: Exception) {
            baseActivity?.hideLoading()
            println(e)
        }
    }

    private fun noData(message: String?) {
        binding?.run {
            tvNoDate.visibility = View.VISIBLE
            rvPackages.visibility = View.GONE
            tvNoDate.text = message ?: getString(R.string.something_went_wrong)
        }
    }


    override fun onThrowable(throwable: Throwable) {
        baseActivity?.hideLoading()
        noData(throwable.message)
    }

}