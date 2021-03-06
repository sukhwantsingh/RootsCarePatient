package com.rootscare.ui.newaddition.providerlisting

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.google.gson.JsonObject
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentProviderListingBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.newaddition.providerlisting.adapter.AdapterProviderListing
import com.rootscare.ui.newaddition.providerlisting.adapter.OnProviderListingCallback
import com.rootscare.ui.newaddition.providerlisting.models.ModelProviderListing
import com.rootscare.ui.nurses.nursesbookingappointment.FragmentNursesBookingAppointment
import com.rootscare.ui.supportmore.bottomsheet.OnBottomSheetCallback
import com.rootscare.utilitycommon.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

const val ARG_PROVIDER_TYPE = "ARG_PROVIDER_TYPE"
const val ARG_PROVIDER_LOOKING_FOR = "ARG_PROVIDER_LOOKING_FOR"
class FragmentProviderListing : BaseFragment<FragmentProviderListingBinding, ProviderListingViewModel>(),
    ProviderListingNavigator {
    private var binding: FragmentProviderListingBinding? = null
    private var mViewModel: ProviderListingViewModel? = null

    private var userType: String? = null
    private var docEnableFor: String? = null

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_provider_listing
    override val viewModel: ProviderListingViewModel
        get() {
            mViewModel = ViewModelProviders.of(this).get(ProviderListingViewModel::class.java)
            return mViewModel as ProviderListingViewModel
        }

    private var isApiHit = false
    private var searchText = ""
    private var pageCount = 1
    private var taskBEnable = ""
    private var hourBEnable = ""

    private var eof = true

    companion object {
        fun newInstance(userType: String, lookingFor: String): FragmentProviderListing {
            val args = Bundle()
            args.putString(ARG_PROVIDER_TYPE, userType)
            args.putString(ARG_PROVIDER_LOOKING_FOR, lookingFor)
            val fragment = FragmentProviderListing()
            fragment.arguments = args
            return fragment
        }
    }

    private var mProviderAdapter: AdapterProviderListing? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel?.navigator = this
        mProviderAdapter = activity?.let { AdapterProviderListing(it) }
        userType = arguments?.getString(ARG_PROVIDER_TYPE) ?: ""
        docEnableFor = arguments?.getString(ARG_PROVIDER_LOOKING_FOR) ?: ""
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        initViews()
        lifecycleScope.launchWhenResumed {
            if(isApiHit.not()) {
                isApiHit = true
               fetchProviderListing()
            }
        }
    }

    private fun initViews() {
        binding?.rvProviders?.adapter = mProviderAdapter
        binding?.inclSearch?.edtSearch?.hint = "Search ${getProvderFromType(userType)} near your address"
        mProviderAdapter?.mCallback = object : OnProviderListingCallback {
        override fun onBookAppointment(pos: Int, node: ModelProviderListing.Result?) {
          when {
                node?.user_type?.equals(ProviderTypes.DOCTOR.getType(), ignoreCase = true) == true -> {
                    (activity as? HomeActivity)?.checkInBackstack(FragmentProvderBookingForDoctor.newInstance(
                        node.user_id?:"", getBookingTypeForDoctor(node),
                        node.user_type ?: "",
                        node.online_enable ?: "1",node.home_visit_enable ?: "1"))
                }
                else -> {
                    (activity as? HomeActivity)?.checkInBackstack(
                        FragmentProvderBooking.newInstance(node?.user_id?:"",
                        getBookingType(node),
                        node?.user_type ?: "",node?.task_base_enable ?: "1",node?.hour_base_enable ?: "1"))
                }
            }
        }
       override fun onItemClick(pos: Int, id: String?,usType:String) {
            (activity as? HomeActivity)?.checkInBackstack(FragmentProviderListingDetails.newInstance(id ?: "", usType.trim()))
       }
       override fun onLoadMore(pos: Int, lastuserId: String) {
                if(eof.not()) {
                  binding?.tvBottomLoadMore?.visibility = View.VISIBLE
                  pageCount++
                 fetchProviderListing(false)
                }
            }
        }

        binding?.inclSearch?.edtSearch?.addTextChangedListener { searchText = it.toString() }
        binding?.inclSearch?.imgSearch?.setOnClickListener {
            pageCount = 1
            fetchProviderListing()
        }

    }

    private fun getBookingTypeForDoctor(node: ModelProviderListing.Result?) = when {
         node?.online_enable.equals("0",ignoreCase = true) &&
         node?.home_visit_enable.equals("0",ignoreCase = true) -> BookingTypes.ONLINE_CONS.get()
         node?.online_enable.equals("0",ignoreCase = true) -> BookingTypes.ONLINE_CONS.get()
         node?.home_visit_enable.equals("0",ignoreCase = true) -> BookingTypes.HOME_VISIT.get()
         else -> BookingTypes.ONLINE_CONS.get()
   }

   private fun getBookingType(node: ModelProviderListing.Result?) = when {
         node?.task_base_enable.equals("0",ignoreCase = true) &&
         node?.hour_base_enable.equals("0",ignoreCase = true) -> BookingTypes.TASK_BASED.get()
         node?.task_base_enable.equals("0",ignoreCase = true) -> BookingTypes.TASK_BASED.get()
         node?.hour_base_enable.equals("0",ignoreCase = true) -> BookingTypes.HOURLY_BASED.get()
         else -> BookingTypes.TASK_BASED.get()
   }




    override fun onSuccessProviderListing(response: ModelProviderListing?) {
        try {
            baseActivity?.hideLoading()
            val isEof = response?.message ?: "Y|Message"
            eof = isEof.split("|")[0].equals("N",ignoreCase = true).not()

            binding?.tvBottomLoadMore?.visibility = View.GONE
            if (response?.code.equals(SUCCESS_CODE)) {
                response?.result?.let {
                    if (it.isNullOrEmpty().not()) {
                        binding?.run {
                            tvNoDate.visibility = View.GONE
                            rvProviders.visibility = View.VISIBLE
                        }
                        if(pageCount == 1) {
                            mProviderAdapter?.updatedArrayList?.clear()
                        }
                        mProviderAdapter?.loadDataIntoList(it)
                    } else noData(response.message?.split("|")?.getOrNull(1) ?: "${response.message}")
                } ?: run { noData(response?.message) }
            } else {
               noData(response?.message)
            }
        } catch (e: Exception) {
            baseActivity?.hideLoading()
            println(e)
        }
    }

    private val btsCallback = object : OnBottomSheetCallback {
        override fun onGoBack() { (activity as? HomeActivity)?.onBackPressed() }
        override fun onRetry() { fetchProviderListing(true) }
    }
    private fun fetchProviderListing(showLoading: Boolean = true) {
        if (isNetworkConnected) {
            val jsonObject = JsonObject().apply {
                addProperty("service_type", userType)
                addProperty("provider_name", searchText)
                addProperty("page_count", pageCount.toString())
                addProperty("login_user_id", mViewModel?.appSharedPref?.userId)
                addProperty("docEnableFor", docEnableFor)
                addProperty("work_area",  mViewModel?.appSharedPref?.workArea)
            }

            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
             baseActivity?.hideKeyboard()
             if(showLoading) baseActivity?.showLoading()
             mViewModel?.apiProviderList(body)
        } else {
            noData(getString(R.string.check_network_connection))
        }
    }

    private fun noData(message: String?) {
        if(pageCount == 1) {
            binding?.run {
                tvNoDate.visibility = View.VISIBLE
                rvProviders.visibility = View.GONE
                tvNoDate.text = message ?: getString(R.string.something_went_wrong)
            }
        } else showToast(message ?: getString(R.string.something_went_wrong))
    }

    override fun errorInAPi(throwable: Throwable?) {
        binding?.tvBottomLoadMore?.visibility = View.GONE
        baseActivity?.hideLoading()
        if(pageCount == 1) {
            val badgateWay = DialogBadGateway.newInstance(btsCallback, "show")
            badgateWay.show(childFragmentManager, "BadgatewayDialog")
        } else showToast(throwable?.message ?: getString(R.string.something_went_wrong))
    }


}