package com.rootscare.ui.newaddition.providerlisting

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.google.gson.JsonObject
import com.rootscare.ApplicationClass
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.LayoutNewProvidersDetailsBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.newaddition.providerlisting.adapter.AdapterAvailableProviderListing
import com.rootscare.ui.newaddition.providerlisting.adapter.OnProviderAvailableListingCallback
import com.rootscare.ui.newaddition.providerlisting.models.ModelProviderDetails
import com.rootscare.ui.nurses.nursesbookingappointment.FragmentNursesBookingAppointment
import com.rootscare.utilitycommon.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import kotlin.collections.ArrayList

const val PROVIDER_ID = "PROVIDER_ID"
class FragmentProviderListingDetails : BaseFragment<LayoutNewProvidersDetailsBinding, ProviderListingViewModel>(),
    ProviderListingNavigator {
    private var binding: LayoutNewProvidersDetailsBinding? = null
    private var mViewModel: ProviderListingViewModel? = null
    var providerId = ""
    var providerType = ""
    var howItWorksValue = ""

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.layout_new_providers_details
    override val viewModel: ProviderListingViewModel
        get() {
            mViewModel =
                ViewModelProviders.of(this).get(ProviderListingViewModel::class.java)
            return mViewModel as ProviderListingViewModel
        }

    var avProvidersAdapter :AdapterAvailableProviderListing? = null
     companion object {
        fun newInstance(provderId: String, userType:String ? =""): FragmentProviderListingDetails {
            val args = Bundle()
            args.putString(PROVIDER_ID, provderId)
            args.putString(ARG_PROVIDER_TYPE, userType)
            val fragment = FragmentProviderListingDetails()
            fragment.arguments = args
            return fragment
        }
    }

    private var taskBooking = ""
    private var hourlyBooking = ""

    private var onlineBooking = ""
    private var homeVisitBooking = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel?.navigator = this
        avProvidersAdapter = activity?.let { AdapterAvailableProviderListing(it) }
        providerId = arguments?.getString(PROVIDER_ID) ?: ""
        providerType = arguments?.getString(ARG_PROVIDER_TYPE) ?: ""
      }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding

        initViews()
        fetchProviderDetail()
    }

    private fun initViews() {
        binding?.run {
            imgIco.setImageResource(getProvderEmojiFromType(providerType))
            rvProviders.adapter = avProvidersAdapter
            avProvidersAdapter?.mCallback = object : OnProviderAvailableListingCallback {
                override fun onItemClick(pos: Int, id: String?) {
                    providerId = id?:""
                    fetchProviderDetail()
                }
            }

            // online /taskbase booking click
            btnBookByTaskbase.setOnClickListener {
                when {
                    providerType.equals(ProviderTypes.DOCTOR.getType(), ignoreCase = true) -> {
                        moveToBookingAppointment(BookingTypes.ONLINE_CONS.get())
                    } else -> { moveToBookingAppointment(BookingTypes.TASK_BASED.get()) }
                }
            }

            // hourly and home visit booking
            btnBookByHourbase.setOnClickListener {
                when {
                    providerType.equals(ProviderTypes.DOCTOR.getType(), ignoreCase = true) -> {
                        moveToBookingAppointment(BookingTypes.HOME_VISIT.get())
                    } else -> { moveToBookingAppointment(BookingTypes.HOURLY_BASED.get()) }
                }
            }

            tvhProvidersSeeAll.setOnClickListener { (activity as? HomeActivity)?.onBackPressed() }
            tvh3.setOnClickListener {
                if(howItWorksValue.isBlank().not()) {
                    val mDialog = DialogHowItWorks.newInstance(howItWorksValue)
                    mDialog.show(childFragmentManager, "HowItWorksDialog")
                }
            }
        }
    }

    private fun moveToBookingAppointment(bookType: String) {
        when {
            providerType.equals(ProviderTypes.DOCTOR.getType(), ignoreCase = true) -> {
                (activity as? HomeActivity)?.checkInBackstack(FragmentProvderBookingForDoctor.newInstance(providerId,
                    bookType, providerType, onlineBooking, homeVisitBooking))
            }
            else -> {
                (activity as? HomeActivity)?.checkInBackstack(FragmentProvderBooking.newInstance(providerId,
                    bookType, providerType, taskBooking, hourlyBooking))
            }
        }

    }

    override fun onSuccessProviderDetails(response: ModelProviderDetails?) {
        baseActivity?.hideLoading()
        if (response?.code.equals(SUCCESS_CODE)) {
           bindNewLayout(response?.result)
       } else {
            Toast.makeText(activity, response?.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun errorInAPi(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
          getString(R.string.something_went_wrong)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun bindNewLayout(response: ModelProviderDetails.Result?) {
        binding?.run {
            response?.let {
                setVariable(BR.data,it)
                taskBooking = it.task_base_enable ?: "1"    // 1- false 0- true
                hourlyBooking = it.hour_base_enable ?: "1"  // 1- false 0- true
                howItWorksValue = it.how_work_value ?: ""

                onlineBooking = it.online_enable ?: "1"  // 1- false 0- true
                homeVisitBooking = it.home_visit_enable ?: ""

                // set button text according to provider
                if(it.user_type?.trim().equals(ProviderTypes.DOCTOR.getType(), ignoreCase = true)){
                    btnBookByTaskbase.text = getString(R.string.book_online_appoint)
                    btnBookByHourbase.text = getString(R.string.book_home_visit_appoint)
                } else {
                    btnBookByTaskbase.text = getString(R.string.book_task_based_appointment)
                    btnBookByHourbase.text = getString(R.string.book_hourly_appointment)
                }

                if (mViewModel?.appSharedPref?.languagePref.equals(LanguageModes.ENG.get(), ignoreCase = true)) {
                    tvh1.text = "${HomeActivity.providerName} ${getString(R.string.booking)}"  // yellow heading provider booking
                    tvhProviders.text = "${getString(R.string.available)} ${HomeActivity.providerName}'s"
                } else {
                tvh1.text = "${getString(R.string.booking)} ${HomeActivity.providerName}"  // yellow heading provider booking
                tvhProviders.text = "${HomeActivity.providerName} ${getString(R.string.available)}"
                }

                setAvailableProviders(it.available_provider)
                true
            }
        }

    }

    // Set up recycler view for service listing if available
    private fun setAvailableProviders(arrList: ArrayList<ModelProviderDetails.Result.AvailableProviders?>?) {
        binding?.run {
            // clear list to avoid duplicate list
            avProvidersAdapter?.updatedArrayList?.clear()
            if (arrList.isNullOrEmpty().not()) {
                rvProviders.visibility = View.VISIBLE
                tvNoFound.visibility = View.GONE
                avProvidersAdapter?.loadDataIntoList(arrList)
            } else {
                rvProviders.visibility = View.GONE
                tvNoFound.visibility = View.VISIBLE
           }

        }
    }

    fun fetchProviderDetail() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
          val jsonObject = JsonObject().apply {
              addProperty("login_user_id",  mViewModel?.appSharedPref?.userId)
              addProperty("id",  providerId )
              addProperty("service_type", providerType)
              addProperty("work_area",  mViewModel?.appSharedPref?.workArea)
            }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())

            mViewModel?.apiProviderDetail(body)

        } else {
              showToast(getString(R.string.check_network_connection))

        }
    }

}