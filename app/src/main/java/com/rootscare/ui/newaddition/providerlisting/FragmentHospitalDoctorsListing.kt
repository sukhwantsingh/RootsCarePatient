package com.rootscare.ui.newaddition.providerlisting

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.dialog.CommonDialog
import com.google.gson.JsonObject
import com.interfaces.DropDownDialogCallBack
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.FragmentHospitalDoctorsListingBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.newaddition.appointments.FragNewAppointmentListing.Companion.showSearch
import com.rootscare.ui.newaddition.providerlisting.adapter.AdapterProviderListing
import com.rootscare.ui.newaddition.providerlisting.adapter.OnProviderListingCallback
import com.rootscare.ui.newaddition.providerlisting.models.ModelProviderListing
import com.rootscare.ui.supportmore.bottomsheet.OnBottomSheetCallback
import com.rootscare.ui.supportmore.models.ModelIssueTypes
import com.rootscare.utilitycommon.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

val ARG_HOSPITAL_NAME : String by lazy { "ARG_HOSPITAL_NAME"}
val ARG_HOSPITAL_ID : String by lazy { "ARG_HOSPITAL_ID"}
class FragmentHospitalDoctorsListing : BaseFragment<FragmentHospitalDoctorsListingBinding, ProviderListingViewModel>(),
    ProviderListingNavigator {
    private var binding: FragmentHospitalDoctorsListingBinding? = null
    private var mViewModel: ProviderListingViewModel? = null

    private var hospitalId: String? = null
    private var hospitalName: String? = null
    private var userType: String? = null
    private var docEnableFor: String? = null

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_hospital_doctors_listing
    override val viewModel: ProviderListingViewModel
        get() {
            mViewModel = ViewModelProviders.of(this).get(ProviderListingViewModel::class.java)
            return mViewModel as ProviderListingViewModel
        }

    private var isApiHit = false
    private var searchText = ""
    private var specialitySelected = ""
    private var pageCount = 1

    private var eof = true

    companion object {
        val showSearch = MutableLiveData(false)

        fun newInstance(hospId: String, hospName: String, userType: String, lookingFor: String): FragmentHospitalDoctorsListing {
            val args = Bundle()
            args.putString(ARG_HOSPITAL_NAME, hospName)
            args.putString(ARG_HOSPITAL_ID, hospId)
            args.putString(ARG_PROVIDER_TYPE, userType)
            args.putString(ARG_PROVIDER_LOOKING_FOR, lookingFor)
            val fragment = FragmentHospitalDoctorsListing()
            fragment.arguments = args
            return fragment
        }
    }

    private var mProviderAdapter: AdapterProviderListing? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel?.navigator = this
        mProviderAdapter = activity?.let { AdapterProviderListing(it) }
        hospitalName = arguments?.getString(ARG_HOSPITAL_NAME).orEmpty()
        hospitalId = arguments?.getString(ARG_HOSPITAL_ID).orEmpty()
        userType = arguments?.getString(ARG_PROVIDER_TYPE).orEmpty()
        docEnableFor = arguments?.getString(ARG_PROVIDER_LOOKING_FOR).orEmpty()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = viewDataBinding
        initViews()
        observers()
        lifecycleScope.launchWhenResumed {
            if(isApiHit.not()) {
                isApiHit = true
               fetchProviderListing()
            }
        }
    }

    private fun observers() {
        showSearch.observe(viewLifecycleOwner)  {
            if(it) {
                binding?.inclSearch?.root?.visibility = View.VISIBLE
            } else {
                binding?.inclSearch?.edtSearch?.setText("")
                binding?.inclSearch?.root?.visibility = View.GONE
            }
        }
    }

    private fun initViews() {
        binding?.rvProviders?.adapter = mProviderAdapter
        binding?.tvttle?.text = hospitalName
        binding?.inclSearch?.edtSearch?.hint = getString(R.string.search_by_name)
        binding?.inclSearch?.imgCross?.setOnClickListener {
            hideKeyboard(); showSearch.value = false
            searchCalled()
        }

        mProviderAdapter?.mCallback = object : OnProviderListingCallback {
        override fun onBookAppointment(pos: Int, node: ModelProviderListing.Result?) {
              (activity as? HomeActivity)?.checkInBackstack(FragmentProvderBookingForDoctor.newInstance(
                  node?.user_id.orEmpty(), BookingTypes.ONLINE_CONS.get(), node?.user_type.orEmpty(),
                  node?.online_enable ?: "1","1"))
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

        binding?.inclSearch?.edtSearch?.addTextChangedListener {
            searchText = it.toString()
            binding?.inclSearch?.imgSearch?.visibility =  if(it.isNullOrBlank()) View.GONE else View.VISIBLE
        }

        binding?.inclSearch?.imgSearch?.setOnClickListener { searchCalled(textToSearch = searchText) }
   //     binding?.tvMoreFilter?.setOnClickListener { showToast("Under working") }

        binding?.tvSelectDepart?.setOnClickListener {
            if(listSpeciality.isEmpty()) return@setOnClickListener
            CommonDialog.showDialogForDropDownList(this.requireActivity(),getString(R.string.select_speciality), listSpeciality,
                object : DropDownDialogCallBack {
                    override fun onConfirm(text: String) {
                        if (specialitySelected.equals(text, ignoreCase = true).not()) {
                            specialitySelected = text
                            binding?.tvSelectDepart?.text = text

                            // call the api on the department selected
                            searchCalled(textToSearch = searchText)
                        }
                    }
                })
        }

        if(listSpeciality.isEmpty()) fetchSpecialityApi()

    }
    private fun searchCalled(textToSearch: String = "") {
        searchText = textToSearch
        pageCount = 1
        fetchProviderListing()
    }

    private fun fetchSpecialityApi() {
        if (isNetworkConnected) {
            val jsonObject = JsonObject().apply {
                addProperty("hospital_id", hospitalId)
                addProperty("service_type", ProviderTypes.DOCTOR.getType())
            }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            //    baseActivity?.showLoading()
            mViewModel?.apiSpecialityList(body)
        } else {
            showToast(getString(R.string.check_network_connection))
        }
    }


    var listSpeciality = ArrayList<String?>()
    override fun onSuccessSpeciality(specialityList: ModelIssueTypes?) {
        baseActivity?.hideLoading()
        listSpeciality.clear()
        if (specialityList?.code.equals(SUCCESS_CODE)) {
            CoroutineScope(Dispatchers.IO).launch {
                specialityList?.result?.let {
                    if (it.isNotEmpty()) {
                        listSpeciality.clear()
                        it.forEach { lst -> listSpeciality.add(lst?.name) }
                    }
                }
            }
        } else {
            showToast(specialityList?.message ?: "")
        }
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
                addProperty("work_area",  mViewModel?.appSharedPref?.workArea)
                addProperty("hospital_id",  hospitalId)
                addProperty("specility", specialitySelected)
            }

            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
             baseActivity?.hideKeyboard()
             if(showLoading) baseActivity?.showLoading()
             mViewModel?.apiProviderListHospDoctor(body)
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