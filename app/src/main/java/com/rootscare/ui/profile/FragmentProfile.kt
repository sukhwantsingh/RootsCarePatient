package com.rootscare.ui.profile

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import com.dialog.CommonDialog
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.gson.JsonObject
import com.interfaces.DialogClickCallback
import com.interfaces.DropDownDialogCallBack
import com.model.ModelServiceFor
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.patientprofilerequest.PatientProfileRequest
import com.rootscare.data.model.api.request.patientprofilerequest.updateprofilelifestylerequest.ProfileLifestyleUpdateRequest
import com.rootscare.data.model.api.request.patientprofilerequest.updateprofilemedicalrequest.ProfileMedicalUpdateRequest
import com.rootscare.data.model.api.response.nationalityresponse.NationalityResponse
import com.rootscare.data.model.api.response.patientprofileresponse.PatientProfileResponse
import com.rootscare.databinding.FragmentProfileBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.login.LoginActivity
import com.rootscare.ui.login.subfragment.registrationfragment.FragmentRegistration
import com.rootscare.utilitycommon.*
import com.rootscare.utils.ManagePermissions
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.util.*
import kotlin.collections.ArrayList


class FragmentProfile : BaseFragment<FragmentProfileBinding, FragmentProfileViewModel>(),
    FragmentProfileNavigator, PlacesAutoCompleteAdapter.ClickListener {
    private var fragmentProfileBinding: FragmentProfileBinding? = null
    private var fragmentProfileViewModel: FragmentProfileViewModel? = null

    var nationalityDropdownlist = ArrayList<String?>()

    var monthstr: String = ""
    var dayStr: String = ""
    var imagefilename = ""
    var selectGender = "Female"

    var latitude: String = ""
    var longitude: String = ""


    var imageFile: File? = null


    private val PICK_IMAGE_REQUEST = 1

    var thumbnail: Bitmap? = null
    var bytes: ByteArrayOutputStream? = null

    private val REQUEST_CAMERA = 0
    private var SELECT_FILE: Int = 1
    private var REQUEST_ID_MULTIPLE_PERMISSIONS = 123
    private val ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 0
    var requested = false
    private lateinit var managePermissions: ManagePermissions
    private val PermissionsRequestCode = 123
  //  private var mAutoCompleteAdapter: PlacesAutoCompleteAdapter? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_profile
    override val viewModel: FragmentProfileViewModel
        get() {
            fragmentProfileViewModel =
                ViewModelProviders.of(this).get(FragmentProfileViewModel::class.java)
            return fragmentProfileViewModel as FragmentProfileViewModel
        }

    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
        val TAG = FragmentRegistration::class.java.simpleName
        fun newInstance(): FragmentProfile {
            val args = Bundle()
            val fragment = FragmentProfile()
            fragment.arguments = args
            return fragment
        }
    }
    private val workFromList : ArrayList<String?> by lazy { ArrayList() }
    private val workFromListMap : HashMap<String?, String?> by lazy { HashMap() }
    private var currency = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentProfileViewModel?.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentProfileBinding = viewDataBinding
        setUpTabLayout()

        val list = listOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)
        managePermissions = ManagePermissions(this.requireActivity(), list, PermissionsRequestCode)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            managePermissions.checkPermissions()

        fetchProfileDetail()
        initViews()
    }

    private fun initViews(){

        fragmentProfileBinding?.layoutProfilePersonal?.txtRegDob?.setOnClickListener {
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)

           val dpd = DatePickerDialog(this.requireActivity(), { _, year1, monthOfYear, dayOfMonth ->
              monthstr = if ((monthOfYear + 1) < 10) { "0" + (monthOfYear + 1) } else { (monthOfYear + 1).toString()  }
              dayStr = if (dayOfMonth < 10) { "0$dayOfMonth"  } else { dayOfMonth.toString()  }
              fragmentProfileBinding?.layoutProfilePersonal?.txtRegDob?.setText("$year1-$monthstr-$dayStr")
                }, year, month, day
            )

            var yearForReopen: Int? = null
            var monthForReopen: Int? = null
            var dayForReopen: Int? = null
            if (!TextUtils.isDigitsOnly(fragmentProfileBinding?.layoutProfilePersonal?.txtRegDob?.text.toString().trim())) {
                val strings = fragmentProfileBinding?.layoutProfilePersonal?.txtRegDob?.text.toString().split("-")
                yearForReopen = strings.getOrNull(0)?.toInt()
                monthForReopen = (strings.getOrNull(1)?.toInt() ?: 1) - 1
                dayForReopen = strings.getOrNull(2)?.toInt()
            }
            if (yearForReopen != null && monthForReopen != null && dayForReopen != null) {
                dpd.updateDate(yearForReopen, monthForReopen, dayForReopen)
            }

            val date = Date()
            date.year = Date().year - 5
            dpd.show()
            dpd.datePicker.maxDate = System.currentTimeMillis()
        }

        radioButtonClickEvenOfMedical()
        fragmentProfileBinding?.layoutProfilePersonal?.imgProfile?.setOnClickListener {
            if (Build.VERSION.SDK_INT >= 23) {
                val granted = checkAndRequestPermissionsTest()
                println("granted===>$granted")
                if (granted) {
                    if (checkAndRequestPermissionsTest()) {
                        captureImage()
                    }
                }
            } else {
                if (checkAndRequestPermissionsTest()) {
                    captureImage()
                }

            }
        }
        fragmentProfileBinding?.layoutProfilePersonal?.radioBtnRegMale?.setOnClickListener(View.OnClickListener {
            selectGender = GenderType.MALE.get()
        })
        fragmentProfileBinding?.layoutProfilePersonal?.radioBtnRegFemale?.setOnClickListener(View.OnClickListener {
            selectGender = GenderType.FEMALE.get()
        })
        fragmentProfileBinding?.layoutProfilePersonal?.edtNationality?.setOnClickListener {
            CommonDialog.showDialogForDropDownList(this.requireActivity(),getString(R.string.nationality), nationalityDropdownlist,
                object : DropDownDialogCallBack {
                    override fun onConfirm(text: String) {
                        fragmentProfileBinding?.layoutProfilePersonal?.edtNationality?.setText(text)
                    }
                })
        }
        fragmentProfileBinding?.layoutProfileMedical?.btnPatientProfileMedical?.setOnClickListener {
            if (isNetworkConnected) {
                baseActivity?.showLoading()
                val profileMedicalUpdateRequest = ProfileMedicalUpdateRequest()
                if (fragmentProfileBinding?.layoutProfileMedical?.radioAllergiesYes?.isChecked!!) {
                    profileMedicalUpdateRequest.allergies = "yes"
                } else if (fragmentProfileBinding?.layoutProfileMedical?.radioAllergiesNo?.isChecked!!) {
                    profileMedicalUpdateRequest.allergies = "no"
                    fragmentProfileBinding?.layoutProfileMedical?.edtAllergiesData?.setText("")
                }

                profileMedicalUpdateRequest.allergiesData =
                    fragmentProfileBinding?.layoutProfileMedical?.edtAllergiesData?.text?.toString()

                if (fragmentProfileBinding?.layoutProfileMedical?.radioCurrentMedicationYes?.isChecked!!) {
                    profileMedicalUpdateRequest.currentMedication = "yes"
                } else if (fragmentProfileBinding?.layoutProfileMedical?.radioCurrentMedicationNo?.isChecked!!) {
                    profileMedicalUpdateRequest.currentMedication = "no"
                    fragmentProfileBinding?.layoutProfileMedical?.edtCurrentMedicationData?.setText(
                        ""
                    )
                }
                profileMedicalUpdateRequest.currentMedicationData =
                    fragmentProfileBinding?.layoutProfileMedical?.edtCurrentMedicationData?.text?.toString()

                if (fragmentProfileBinding?.layoutProfileMedical?.radioPastMedicationYes?.isChecked!!) {
                    profileMedicalUpdateRequest.pastMedication = "yes"
                } else if (fragmentProfileBinding?.layoutProfileMedical?.radioPastMedicationNo?.isChecked!!) {
                    profileMedicalUpdateRequest.pastMedication = "no"
                    fragmentProfileBinding?.layoutProfileMedical?.edtPastmedicationData?.setText(
                        ""
                    )
                }
                profileMedicalUpdateRequest.pastMedicationData =
                    fragmentProfileBinding?.layoutProfileMedical?.edtPastmedicationData?.text?.toString()

                if (fragmentProfileBinding?.layoutProfileMedical?.radioInjuriesYes?.isChecked!!) {
                    profileMedicalUpdateRequest.injuries = "yes"
                } else if (fragmentProfileBinding?.layoutProfileMedical?.radioInjuriesNo?.isChecked!!) {
                    profileMedicalUpdateRequest.injuries = "no"
                    fragmentProfileBinding?.layoutProfileMedical?.edtInjuriesData?.setText("")
                }

                profileMedicalUpdateRequest.injuriesData =
                    fragmentProfileBinding?.layoutProfileMedical?.edtInjuriesData?.text?.toString()

                if (fragmentProfileBinding?.layoutProfileMedical?.radioSurgeriesYes?.isChecked!!) {
                    profileMedicalUpdateRequest.surgeries = "yes"
                } else if (fragmentProfileBinding?.layoutProfileMedical?.radioSurgeriesNo?.isChecked!!) {
                    profileMedicalUpdateRequest.surgeries = "no"
                    fragmentProfileBinding?.layoutProfileMedical?.edtSurgeriesData?.setText("")
                }
                profileMedicalUpdateRequest.surgeriesData =
                    fragmentProfileBinding?.layoutProfileMedical?.edtSurgeriesData?.text?.toString()

                if (fragmentProfileBinding?.layoutProfileMedical?.radioChronicdisessesYes?.isChecked!!) {
                    profileMedicalUpdateRequest.chronicDiseases = "yes"
                } else if (fragmentProfileBinding?.layoutProfileMedical?.radioChronicdisessesNo?.isChecked!!) {
                    profileMedicalUpdateRequest.chronicDiseases = "no"
                    fragmentProfileBinding?.layoutProfileMedical?.edtChronicdisessesData?.setText(
                        ""
                    )
                }
                profileMedicalUpdateRequest.chronicDiseasesData =
                    fragmentProfileBinding?.layoutProfileMedical?.edtChronicdisessesData?.text?.toString()
                profileMedicalUpdateRequest.userId =
                    fragmentProfileViewModel?.appSharedPref?.userId

                fragmentProfileViewModel?.apieditpatientprofilemedical(
                    profileMedicalUpdateRequest
                )

            } else {
             showToast(getString(R.string.check_network_connection))
            }

        }
        fragmentProfileBinding?.layoutProfileLifestyle?.btnPatientProfileLifestyle?.setOnClickListener {
            if (checkFieldValidation()) {
                if (isNetworkConnected) {
                    baseActivity?.showLoading()
                    val profileLifestyleUpdateRequest = ProfileLifestyleUpdateRequest()
                    profileLifestyleUpdateRequest.smoking =
                        fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleSmokinghabits?.text?.toString()
                    profileLifestyleUpdateRequest.alcohol =
                        fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleAlcoholHabits?.text?.toString()
                    profileLifestyleUpdateRequest.bloodGroup =
                        fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleBloodGroup?.text?.toString()
                    profileLifestyleUpdateRequest.activityLevel =
                        fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleActivityLevel?.text?.toString()
                    profileLifestyleUpdateRequest.foodPreference =
                        fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleFoodPreference?.text?.toString()
                    profileLifestyleUpdateRequest.occupation =
                        fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleOccupation?.text?.toString()
                    profileLifestyleUpdateRequest.userId =
                        fragmentProfileViewModel?.appSharedPref?.userId
                    fragmentProfileViewModel?.apieditpatientprofilestyle(
                        profileLifestyleUpdateRequest
                    )
                } else {
                    showToast(getString(R.string.check_network_connection))
                }
            }

        }
        fragmentProfileBinding?.layoutProfilePersonal?.btnSubmit?.setOnClickListener {
            if (checkFieldValidationForPersonalDetails()) {
                if (isNetworkConnected) {
                    baseActivity?.showLoading()
                    apieditpatientprofilepersonalApiCall()
                } else {
                    showToast(getString(R.string.check_network_connection))
                }
            }
        }

//        fragmentProfileBinding?.layoutProfilePersonal?.tvWorkFrom?.setOnClickListener {
//            CommonDialog.showDialogForDropDownList(this.requireActivity(), getString(R.string.select),
//                workFromList, object : DropDownDialogCallBack {
//                    override fun onConfirm(text: String) {
//                        fragmentProfileBinding?.layoutProfilePersonal?.run {
//                            if(tvWorkFrom.text.toString().equals(text,true).not()){
//                                tvWorkFrom.text = text
//                            }
//                            val mCode = workFromListMap[text].orEmpty()
//                            edtCc.setText(mCode)
//                        }
//                    }
//                })
//        }

        lifeStyleDropdownClick()
    }
    private fun fetchProfileDetail(){
        lifecycleScope.launchWhenResumed {
            if (isNetworkConnected) {
                baseActivity?.showLoading()
                val patientProfileRequest = PatientProfileRequest()
                patientProfileRequest.userId = fragmentProfileViewModel?.appSharedPref?.userId
                fragmentProfileViewModel?.apipatientprofile(patientProfileRequest)

            } else {
                showToast(getString(R.string.check_network_connection))
            }
        }
    }
    private fun setUpTabLayout() {
        tweakTabsLoading()
        fragmentProfileBinding?.tablayout?.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                when (tab.position) {
                    0 -> {
                        fragmentProfileBinding?.llProfilePersonal?.visibility = View.VISIBLE
                        fragmentProfileBinding?.llProfileMedical?.visibility = View.GONE
                        fragmentProfileBinding?.llProfileLifestyle?.visibility = View.GONE

                    }
                    1 -> {
                        fragmentProfileBinding?.llProfilePersonal?.visibility = View.GONE
                        fragmentProfileBinding?.llProfileMedical?.visibility = View.VISIBLE
                        fragmentProfileBinding?.llProfileLifestyle?.visibility = View.GONE

                    }
                    2 -> {
                        fragmentProfileBinding?.llProfilePersonal?.visibility = View.GONE
                        fragmentProfileBinding?.llProfileMedical?.visibility = View.GONE
                        fragmentProfileBinding?.llProfileLifestyle?.visibility = View.VISIBLE

                    }
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
        fragmentProfileBinding?.llProfilePersonal?.visibility = View.VISIBLE
        fragmentProfileBinding?.llProfileMedical?.visibility = View.GONE
        fragmentProfileBinding?.llProfileLifestyle?.visibility = View.GONE
    }

   private fun tweakTabsLoading() {
        val tabTitles: MutableList<String> = arrayListOf(getString(R.string.personal),
            getString(R.string.medical), getString(R.string.lifestyle))

        for (i in tabTitles.indices) {
            fragmentProfileBinding?.tablayout?.newTab()?.setText(tabTitles[i])?.let {
                fragmentProfileBinding?.tablayout?.addTab(it, i)
            }
        }
        fragmentProfileBinding?.tablayout?.setTabTextColors(
            ContextCompat.getColor(requireActivity(), R.color.color_tab_text_normal),
            ContextCompat.getColor(requireActivity(), R.color.color_tab_text_selected)
        )
        fragmentProfileBinding?.tablayout?.setSelectedTabIndicatorColor(Color.parseColor("#0888D1"))
    }

    private fun fetchServiceForApi() {
        if (isNetworkConnected) {
            fragmentProfileViewModel?.apiServiceFor()
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


    override fun successPatientProfileResponse(response: PatientProfileResponse?) {
        baseActivity?.hideLoading()
        if (response?.code.equals(SUCCESS_CODE)) {
          //  fetchServiceForApi()
            if(nationalityDropdownlist.isNullOrEmpty()) {
                try {
                    val jsonObject = JsonObject().apply {
                        addProperty("login_user_id", fragmentProfileViewModel?.appSharedPref?.userId)
                    }
                    val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                    fragmentProfileViewModel?.apinationality(body)
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            fragmentProfileViewModel?.appSharedPref?.userName = (response?.result?.firstName + " " + response?.result?.lastName).trim()
            fragmentProfileViewModel?.appSharedPref?.userEmail = response?.result?.email
            fragmentProfileViewModel?.appSharedPref?.userImage = response?.result?.image
            fragmentProfileViewModel?.appSharedPref?.workArea = response?.result?.work_area
            fragmentProfileViewModel?.appSharedPref?.currencySymbol = response?.result?.currency_symbol
            currency = response?.result?.currency_symbol.orEmpty()

            HomeActivity.isProfileUpdated.value = true

            defaultPersonalDetailsSetup(response)
            defaultMedicalDetailsSetup(response)
            defaultRadioButtonSetup()
            defaultLifeStylelDetailsSetup(response)

        } else {
            CommonDialog.showDialogForSingleButton(requireActivity(), object : DialogClickCallback {
             }, null, response?.message?:"")
        }

    }

    override fun successNationalityResponse(response: NationalityResponse?) {
        baseActivity?.hideLoading()
        if (response?.code.equals(SUCCESS_CODE)) {
            if (response?.result != null && response.result.size > 0) {
                for (i in 0 until response.result.size) {
                    nationalityDropdownlist.add(response.result[i]?.name)
                }
            }
        }
    }

    override fun errorPatientProfileResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            showToast(getString(R.string.something_went_wrong))
        }
    }

    private fun defaultPersonalDetailsSetup(patientProfileResponse: PatientProfileResponse?) {
        patientProfileResponse?.result?.let {

            fragmentProfileBinding?.layoutProfilePersonal?.run {
                tvUsername.text = it.firstName ?: ""
                tvhEmailId.text = it.email ?: ""

                tvWorkFrom.text = it.work_area.orEmpty()
                edtRegFirstname.setText(it.firstName.orEmpty())
                edtRegPhonenumber.setText(it.phoneNumber.orEmpty())
                edtCc.setText(it.countryCode.orEmpty())

                edtRegEmailaddress.setText(it.email.orEmpty())
                txtRegDob.setText(it.dob.orEmpty())
                edtNationality.setText(it.nationality.orEmpty())
                edtAddress.setText(it.address.orEmpty())

                edtIdentityNumber.setText(it.idNumber.orEmpty())

                imgProfile.setCircularRemoteImage(it.image.orEmpty())

                selectGender = it.gender ?: GenderType.FEMALE.get()
                if (it.gender.isNullOrBlank().not()) {
                    if (it.gender?.trim() == GenderType.FEMALE.get()) {
                        radioBtnRegFemale.isChecked = true
                    } else if (it.gender?.trim() == GenderType.MALE.get()) {
                        radioBtnRegMale.isChecked = true
                    }
                }
            }
        }

    }

    private fun defaultMedicalDetailsSetup(patientProfileResponse: PatientProfileResponse?) {
        if (patientProfileResponse?.result?.allergies.equals("") || patientProfileResponse?.result?.allergies != null) {
            if (patientProfileResponse?.result?.allergies.equals("yes")) {
                fragmentProfileBinding?.layoutProfileMedical?.radioAllergiesYes?.isChecked = true
            } else if (patientProfileResponse?.result?.allergies.equals("no")) {
                fragmentProfileBinding?.layoutProfileMedical?.radioAllergiesNo?.isChecked = true
            }
        }

        if (patientProfileResponse?.result?.allergiesData != null && patientProfileResponse.result.allergiesData != ""
        ) {
            fragmentProfileBinding?.layoutProfileMedical?.edtAllergiesData?.setText(
                patientProfileResponse.result.allergiesData
            )
        }

        if (patientProfileResponse?.result?.currentMedication.equals("") || patientProfileResponse?.result?.currentMedication != null) {
            if (patientProfileResponse?.result?.currentMedication.equals("yes")) {
                fragmentProfileBinding?.layoutProfileMedical?.radioCurrentMedicationYes?.isChecked =
                    true
            } else if (patientProfileResponse?.result?.currentMedication.equals("no")) {
                fragmentProfileBinding?.layoutProfileMedical?.radioCurrentMedicationNo?.isChecked =
                    true
            }
        }
        if (patientProfileResponse?.result?.currentMedicationData != null && patientProfileResponse.result.currentMedicationData != ""
        ) {
            fragmentProfileBinding?.layoutProfileMedical?.edtCurrentMedicationData?.setText(
                patientProfileResponse.result.currentMedicationData
            )
        }


        if (patientProfileResponse?.result?.pastMedication.equals("") || patientProfileResponse?.result?.pastMedication != null) {
            if (patientProfileResponse?.result?.pastMedication.equals("yes")) {
                fragmentProfileBinding?.layoutProfileMedical?.radioPastMedicationYes?.isChecked =
                    true
            } else if (patientProfileResponse?.result?.pastMedication.equals("no")) {
                fragmentProfileBinding?.layoutProfileMedical?.radioPastMedicationNo?.isChecked =
                    true
            }
        }

        if (patientProfileResponse?.result?.pastMedicationData != null && patientProfileResponse.result.pastMedicationData != ""
        ) {
            fragmentProfileBinding?.layoutProfileMedical?.edtPastmedicationData?.setText(
                patientProfileResponse.result.pastMedicationData
            )
        }


        if (patientProfileResponse?.result?.injuries.equals("") || patientProfileResponse?.result?.injuries != null) {
            if (patientProfileResponse?.result?.injuries.equals("yes")) {
                fragmentProfileBinding?.layoutProfileMedical?.radioInjuriesYes?.isChecked = true
            } else if (patientProfileResponse?.result?.injuries.equals("no")) {
                fragmentProfileBinding?.layoutProfileMedical?.radioInjuriesNo?.isChecked = true
            }
        }

        if (patientProfileResponse?.result?.injuriesData != null && patientProfileResponse.result.injuriesData != ""
        ) {
            fragmentProfileBinding?.layoutProfileMedical?.edtInjuriesData?.setText(
                patientProfileResponse.result.injuriesData
            )
        }



        if (patientProfileResponse?.result?.surgeries.equals("") || patientProfileResponse?.result?.surgeries != null) {
            if (patientProfileResponse?.result?.surgeries.equals("yes")) {
                fragmentProfileBinding?.layoutProfileMedical?.radioSurgeriesYes?.isChecked = true
            } else if (patientProfileResponse?.result?.surgeries.equals("no")) {
                fragmentProfileBinding?.layoutProfileMedical?.radioSurgeriesNo?.isChecked = true
            }
        }

        if (patientProfileResponse?.result?.surgeriesData != null && patientProfileResponse.result.surgeriesData != ""
        ) {
            fragmentProfileBinding?.layoutProfileMedical?.edtSurgeriesData?.setText(
                patientProfileResponse.result.surgeriesData
            )
        }

        if (patientProfileResponse?.result?.chronicDiseases.equals("") || patientProfileResponse?.result?.chronicDiseases != null) {
            if (patientProfileResponse?.result?.chronicDiseases.equals("yes")) {
                fragmentProfileBinding?.layoutProfileMedical?.radioChronicdisessesYes?.isChecked =
                    true
            } else if (patientProfileResponse?.result?.chronicDiseases.equals("no")) {
                fragmentProfileBinding?.layoutProfileMedical?.radioChronicdisessesNo?.isChecked =
                    true
            }
        }

        if (patientProfileResponse?.result?.chronicDiseasesData != null && patientProfileResponse.result.chronicDiseasesData != ""
        ) {
            fragmentProfileBinding?.layoutProfileMedical?.edtChronicdisessesData?.setText(
                patientProfileResponse.result.chronicDiseasesData
            )
        }

    }

    private fun defaultRadioButtonSetup() {
        fragmentProfileBinding?.layoutProfileMedical?.run {
            if (radioAllergiesYes.isChecked) {
                tilAllergiesData.visibility = View.VISIBLE
            }
            if (radioAllergiesNo.isChecked) {
                tilAllergiesData.visibility = View.GONE
            }

            if (radioCurrentMedicationYes.isChecked) {
                tilCurrentMedicationData.visibility = View.VISIBLE
            }
            if (radioCurrentMedicationNo.isChecked) {
                tilCurrentMedicationData.visibility = View.GONE
            }
            if (radioPastMedicationYes.isChecked) {
                tilPastmedicationData.visibility = View.VISIBLE
            }
            if (radioPastMedicationNo.isChecked) {
                tilPastmedicationData.visibility = View.GONE
            }
            if (radioInjuriesYes.isChecked) {
                tilInjuriesData.visibility = View.VISIBLE
            }
            if (radioInjuriesNo.isChecked) {
                tilInjuriesData.visibility = View.GONE
            }
            if (radioSurgeriesYes.isChecked) {
                tilSurgeriesData.visibility = View.VISIBLE
            }
            if (radioSurgeriesNo.isChecked) {
                tilSurgeriesData.visibility = View.GONE
            }
            if (radioChronicdisessesYes.isChecked) {
                tilChronicdisessesData.visibility = View.VISIBLE
            }
            if (radioChronicdisessesNo.isChecked) {
                tilChronicdisessesData.visibility = View.GONE
            }
        }

    }

    private fun radioButtonClickEvenOfMedical() {
        fragmentProfileBinding?.layoutProfileMedical?.radioAllergiesYes?.setOnClickListener {
            fragmentProfileBinding?.layoutProfileMedical?.tilAllergiesData?.visibility =
                View.VISIBLE
        }
        fragmentProfileBinding?.layoutProfileMedical?.radioAllergiesNo?.setOnClickListener {
            fragmentProfileBinding?.layoutProfileMedical?.tilAllergiesData?.visibility = View.GONE
        }
        fragmentProfileBinding?.layoutProfileMedical?.radioCurrentMedicationYes?.setOnClickListener {
            fragmentProfileBinding?.layoutProfileMedical?.tilCurrentMedicationData?.visibility =
                View.VISIBLE
        }
        fragmentProfileBinding?.layoutProfileMedical?.radioCurrentMedicationNo?.setOnClickListener {
            fragmentProfileBinding?.layoutProfileMedical?.tilCurrentMedicationData?.visibility =
                View.GONE
        }

        fragmentProfileBinding?.layoutProfileMedical?.radioPastMedicationYes?.setOnClickListener {
            fragmentProfileBinding?.layoutProfileMedical?.tilPastmedicationData?.visibility =
                View.VISIBLE
        }
        fragmentProfileBinding?.layoutProfileMedical?.radioPastMedicationNo?.setOnClickListener {
            fragmentProfileBinding?.layoutProfileMedical?.tilPastmedicationData?.visibility =
                View.GONE
        }

        fragmentProfileBinding?.layoutProfileMedical?.radioInjuriesYes?.setOnClickListener {
            fragmentProfileBinding?.layoutProfileMedical?.tilInjuriesData?.visibility = View.VISIBLE
        }
        fragmentProfileBinding?.layoutProfileMedical?.radioInjuriesNo?.setOnClickListener {
            fragmentProfileBinding?.layoutProfileMedical?.tilInjuriesData?.visibility = View.GONE
        }

        fragmentProfileBinding?.layoutProfileMedical?.radioSurgeriesYes?.setOnClickListener {
            fragmentProfileBinding?.layoutProfileMedical?.tilSurgeriesData?.visibility =
                View.VISIBLE
        }
        fragmentProfileBinding?.layoutProfileMedical?.radioSurgeriesNo?.setOnClickListener {
            fragmentProfileBinding?.layoutProfileMedical?.tilSurgeriesData?.visibility = View.GONE
        }

        fragmentProfileBinding?.layoutProfileMedical?.radioChronicdisessesYes?.setOnClickListener {
            fragmentProfileBinding?.layoutProfileMedical?.tilChronicdisessesData?.visibility =
                View.VISIBLE
        }
        fragmentProfileBinding?.layoutProfileMedical?.radioChronicdisessesNo?.setOnClickListener {
            fragmentProfileBinding?.layoutProfileMedical?.tilChronicdisessesData?.visibility =
                View.GONE
        }
    }

    private fun defaultLifeStylelDetailsSetup(patientProfileResponse: PatientProfileResponse?) {
        if (!patientProfileResponse?.result?.smoking.equals("") || patientProfileResponse?.result?.smoking != null) {
            fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleSmokinghabits?.text =
                patientProfileResponse?.result?.smoking
        }
        if (!patientProfileResponse?.result?.alcohol.equals("") || patientProfileResponse?.result?.alcohol != null) {
            fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleAlcoholHabits?.text =
                patientProfileResponse?.result?.alcohol
        }
        if (!patientProfileResponse?.result?.bloodGroup.equals("") || patientProfileResponse?.result?.bloodGroup != null) {
            fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleBloodGroup?.text =
                patientProfileResponse?.result?.bloodGroup
        }
        if (!patientProfileResponse?.result?.activityLevel.equals("") || patientProfileResponse?.result?.activityLevel != null) {
            fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleActivityLevel?.text =
                patientProfileResponse?.result?.activityLevel
        }
        if (!patientProfileResponse?.result?.foodPreference.equals("") || patientProfileResponse?.result?.foodPreference != null) {
            fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleFoodPreference?.text =
                patientProfileResponse?.result?.foodPreference
        }
        if (!patientProfileResponse?.result?.occupation.equals("") || patientProfileResponse?.result?.occupation != null) {
            fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleOccupation?.text =
                patientProfileResponse?.result?.occupation
        }

    }


    // Receive the permissions request result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,  grantResults: IntArray) {
        when (requestCode) {
            PermissionsRequestCode -> {
                val isPermissionsGranted = managePermissions
                    .processPermissionsResult(grantResults)

                if (isPermissionsGranted) {
                    // Do the task now
                    goToImageIntent()
                    Toast.makeText(activity, "Permissions granted.", Toast.LENGTH_SHORT).show()
                    //  toast("Permissions granted.")
                } else {
                    Toast.makeText(activity, "Permissions denied.", Toast.LENGTH_SHORT).show()
                    // toast("Permissions denied.")
                }
                return
            }
        }
    }


    private fun checkFieldValidation(): Boolean {
        if (TextUtils.isEmpty(fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleSmokinghabits?.text?.toString())) {
            Toast.makeText(activity, "Please enter your smoking habits", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleAlcoholHabits?.text?.toString())) {
            Toast.makeText(activity, "Please enter your alcohol habits", Toast.LENGTH_SHORT).show()
            return false
        }

        if (TextUtils.isEmpty(fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleBloodGroup?.text?.toString())) {
            Toast.makeText(activity, "Please enter your blood group", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleActivityLevel?.text?.toString())) {
            Toast.makeText(activity, "Please enter your activity level", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleFoodPreference?.text?.toString())) {
            Toast.makeText(activity, "Please enter your food Preference", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleOccupation?.text?.toString())) {
            Toast.makeText(activity, "Please enter your occupation", Toast.LENGTH_SHORT).show()
            return false
        }
//


        return true
    }

    private fun checkFieldValidationForPersonalDetails(): Boolean {
        if (TextUtils.isEmpty(fragmentProfileBinding?.layoutProfilePersonal?.edtRegFirstname?.text?.toString())) {
            showToast(getString(R.string.plz_enter_your_name))
            return false
        }

        if(fragmentProfileBinding?.layoutProfilePersonal?.tvWorkFrom?.text.isNullOrBlank()) {
            showToast(getString(R.string.required_service_area))
          return false
        }

        if (TextUtils.isEmpty(fragmentProfileBinding?.layoutProfilePersonal?.edtIdentityNumber?.text?.toString())) {
            showToast(getString(R.string.provide_id_number))
            return false
        }

        if(currency.equals(CurrencyTypes.AED.get(), ignoreCase = true) && isStartWithIdExact7().not()) {
            fragmentProfileBinding?.layoutProfilePersonal?.edtIdentityNumber?.error = getString(R.string.id_must_start_from_7)
            fragmentProfileBinding?.layoutProfilePersonal?.edtIdentityNumber?.requestFocus()
            return false
        }

        if(currency.equals(CurrencyTypes.SAR.get(), ignoreCase = true) && isStartWithIdExact().not()) {
            fragmentProfileBinding?.layoutProfilePersonal?.edtIdentityNumber?.error = getString(R.string.id_must_start_from_1_or_2)
            fragmentProfileBinding?.layoutProfilePersonal?.edtIdentityNumber?.requestFocus()
            return false
        }


        if(((fragmentProfileBinding?.layoutProfilePersonal?.edtIdentityNumber?.text?.toString()?.length ?: 0) < 10) ||
                ((fragmentProfileBinding?.layoutProfilePersonal?.edtIdentityNumber?.text?.toString()?.length ?: 0) > 15)) {
            fragmentProfileBinding?.layoutProfilePersonal?.edtIdentityNumber?.error = getString(R.string.id_number_must_be_10)
            fragmentProfileBinding?.layoutProfilePersonal?.edtIdentityNumber?.requestFocus()
            return false
        }

        return true
    }


    private fun isStartWithIdExact() = fragmentProfileBinding?.layoutProfilePersonal?.edtIdentityNumber?.text.toString().trim().startsWith("1") ||
                                       fragmentProfileBinding?.layoutProfilePersonal?.edtIdentityNumber?.text.toString().trim().startsWith("2")

    private fun isStartWithIdExact7() = fragmentProfileBinding?.layoutProfilePersonal?.edtIdentityNumber?.text.toString().trim().startsWith("7")



    private fun apieditpatientprofilepersonalApiCall() {
        baseActivity?.showLoading()
        val userId = fragmentProfileViewModel?.appSharedPref?.userId?.asReqBody() ?: "".asReqBody()
        val first_name = fragmentProfileBinding?.layoutProfilePersonal?.edtRegFirstname?.text?.toString()?.asReqBody() ?: "".asReqBody()

        var mobile = fragmentProfileBinding?.layoutProfilePersonal?.edtRegPhonenumber?.text?.toString()?.trim()
        val id_number = fragmentProfileBinding?.layoutProfilePersonal?.edtIdentityNumber?.text?.toString()?.asReqBody() ?: "".asReqBody()
        val address = fragmentProfileBinding?.layoutProfilePersonal?.edtAddress?.text?.toString()?.asReqBody()?: "".asReqBody()
        val mDOb = fragmentProfileBinding?.layoutProfilePersonal?.txtRegDob?.text?.toString()?.asReqBody()?: "".asReqBody()
        val gender = selectGender.asReqBody()
        val nationality = fragmentProfileBinding?.layoutProfilePersonal?.edtNationality?.text?.toString()?.asReqBody() ?: "".asReqBody()
        val work_area = fragmentProfileBinding?.layoutProfilePersonal?.tvWorkFrom?.text?.toString()?.asReqBody() ?: "".asReqBody()

        val cc_ =  fragmentProfileBinding?.layoutProfilePersonal?.edtCc?.text?.toString().orEmpty()
        mobile = cc_ + mobile

        if (imageFile != null) {
            val image = imageFile!!.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("image", imageFile?.name, image)

            fragmentProfileViewModel?.apiEditPatientProfilePersonal(
                userId, first_name, mobile.asReqBody(), id_number, multipartBody, mDOb, address, gender,
                nationality,work_area)

        } else {
            val image = "".asReqBody()
            val multipartBody = MultipartBody.Part.createFormData("image", imagefilename, image)

            fragmentProfileViewModel?.apiEditPatientProfilePersonal(
                userId, first_name, mobile.asReqBody(), id_number, multipartBody, mDOb, address, gender,
                nationality,work_area)
        }
    }

    private fun lifeStyleDropdownClick() {
        fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleSmokinghabits?.setOnClickListener {
            dialogDropdown(
                getString(R.string.smoking_habits), yesNoLs(),
                fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleSmokinghabits
            )
        }

        fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleAlcoholHabits?.setOnClickListener {
            dialogDropdown(
                getString(R.string.alcohol_habits), yesNoLs(),
                fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleAlcoholHabits
            )
        }

        fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleBloodGroup?.setOnClickListener {
            dialogDropdown(
                getString(R.string.blood_group), bloodGroupsLs(),
                fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleBloodGroup
            )
        }
        fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleActivityLevel?.setOnClickListener {
            dialogDropdown(
                getString(R.string.activity_level), activityLevelLs(),
                fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleActivityLevel
            )
        }
        fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleFoodPreference?.setOnClickListener {
            dialogDropdown(
                getString(R.string.food_preference), foodPrefLs(),
                fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleFoodPreference
            )
        }
        fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleOccupation?.setOnClickListener {
            dialogDropdown(
                getString(R.string.occupation), occupationLs(),
                fragmentProfileBinding?.layoutProfileLifestyle?.edtLifestyleOccupation
            )
        }
    }

    //image upload*********************************************************************************************************************************************
    private fun checkAndRequestPermissionsTest(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            val permissionText = " "
            val permissionCamera = ContextCompat.checkSelfPermission(
                this.requireActivity(),
                Manifest.permission.CAMERA
            )
            val permissionWriteExternalStorage = ContextCompat.checkSelfPermission(
                this.requireActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val listPermissionsNeeded: MutableList<String> =
                ArrayList()
            if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA)
            }
            if (permissionWriteExternalStorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (listPermissionsNeeded.isNotEmpty()) {
                requested = true
                /*ActivityCompat.requestPermissions(getActivity(),
                                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                                    REQUEST_ID_MULTIPLE_PERMISSIONS);*/requestPermissions(
                    listPermissionsNeeded.toTypedArray(),
                    REQUEST_ID_MULTIPLE_PERMISSIONS
                )
                false
            } else {
                true
            }
        } else {
            requested = false
            true
        }
    }


    private fun captureImage() {
        val options =
            arrayOf<CharSequence>("Take Photo", "Choose from Gallery", "Cancel")
        val builder =
            AlertDialog.Builder(context)
        builder.setTitle("Add Photo!")
        builder.setItems(options) { dialog, item ->
            when {
                options[item] == "Take Photo" -> {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, REQUEST_CAMERA)
                }
                options[item] == "Choose from Gallery" -> {
                    val intent = Intent()
                    intent.type = "image/*"
                    intent.action = Intent.ACTION_GET_CONTENT //
                    startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE)
                }
                options[item] == "Cancel" -> {
                    dialog.dismiss()
                }
            }
        }
        builder.show()
    }


    private fun openPictureEditActivity() {
        if (!TextUtils.isEmpty(imageFile?.path) && File(imageFile?.path).exists()) {
            CropImage.activity(Uri.fromFile(File(imageFile?.path)))
                .setCropShape(CropImageView.CropShape.OVAL)
                .setInitialCropWindowPaddingRatio(0F)
                .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this.requireActivity())
        }
    }

    private fun onCaptureImageResult(data: Intent) {
        thumbnail = data.extras!!["data"] as Bitmap?
        bytes = ByteArrayOutputStream()

        thumbnail?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        imageFile = File(
            activity?.externalCacheDir!!.absolutePath,
            System.currentTimeMillis().toString() + ".jpg"
        )
        val fo: FileOutputStream
        try {
            imageFile?.createNewFile()
            fo = FileOutputStream(imageFile)
            fo.write(bytes?.toByteArray())
            fo.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        openPictureEditActivity()


        /*      im_upload.setImageBitmap(thumbnail);
        im_editbutton.setVisibility(View.GONE);
        im_holder.setVisibility(View.GONE);*/
    }

    private fun onSelectFromGalleryResult(data: Intent?) {
        if (data != null) {
            try {
                thumbnail = MediaStore.Images.Media.getBitmap(activity?.contentResolver, data.data)
                bytes = ByteArrayOutputStream()
                thumbnail?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        imageFile = File(
            activity?.externalCacheDir!!.absolutePath,
            System.currentTimeMillis().toString() + ".jpg"
        )
        val fo: FileOutputStream
        try {
            imageFile?.createNewFile()
            fo = FileOutputStream(imageFile)
            fo.write(bytes?.toByteArray())
            fo.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        openPictureEditActivity()
        /*     im_upload.setImageBitmap(thumbnail);
        im_editbutton.setVisibility(View.GONE);
        im_holder.setVisibility(View.GONE);*/
    }


    private fun goToImageIntent() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (resultCode != Activity.RESULT_CANCELED) {


            if (requestCode == REQUEST_CAMERA) {

                try {
                    onCaptureImageResult(data!!)
                } catch (e: Exception) {
                    println("Exception===>$e")
                }

            } else if (requestCode == SELECT_FILE) {
                if (data != null) {
                    onSelectFromGalleryResult(data)
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (data != null) {
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri
                    //   Picasso.get().load(resultUri).into(fragmentProfileBinding?.imgRootscareProfileImage)
                    imageFile = File(result.uri.path)

                    baseActivity?.showLoading()
                    val userId = fragmentProfileViewModel!!.appSharedPref!!.userId!!
                        .toRequestBody("multipart/form-data".toMediaTypeOrNull())

//        val status = RequestBody.create(MediaType.parse("multipart/form-data"), fragmentProfileBinding?.layoutProfilePersonal?.txtProfilePersonalStatus?.text?.toString())
//        val status = RequestBody.create(MediaType.parse("multipart/form-data"),"1")

                    if (imageFile != null) {
                        val image =
                            imageFile!!
                                .asRequestBody("multipart/form-data".toMediaTypeOrNull())
                        val multipartBody =
                            MultipartBody.Part.createFormData("image", imageFile?.name, image)
//            fragmentProfileViewModel?.apieditpatientprofilepersonal(userId,first_name,last_name,id_number,status,multipartBody)
                        fragmentProfileViewModel?.apieditpatientprofileimage(userId, multipartBody)

                    } else {
                        val image =
                            "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                        val multipartBody =
                            MultipartBody.Part.createFormData("image", imagefilename, image)
//                          fragmentProfileViewModel?.apieditpatientprofilepersonal(userId,first_name,last_name,id_number,status,multipartBody)
                        fragmentProfileViewModel?.apieditpatientprofileimage(userId, multipartBody)
                        //Toast.makeText(activity, "Image can not be blank", Toast.LENGTH_SHORT).show()
                    }
                    println("resultUri===>$resultUri")
                }

            }
        }

    }





/*

    private val filterTextWatcher: TextWatcher = object : TextWatcher {
        override fun afterTextChanged(s: Editable) {
            if (s.toString() != "") {
                if (fragmentProfileBinding?.layoutProfilePersonal?.edtProfilePersonalAddress!!.isFocused) {
                    mAutoCompleteAdapter?.filter?.filter(s.toString())
                    if (fragmentProfileBinding?.layoutProfilePersonal?.placesRecyclerView!!
                            .visibility == View.GONE
                    ) {
                        fragmentProfileBinding?.layoutProfilePersonal?.placesRecyclerView!!
                            .visibility = View.VISIBLE
//                        fragmentProfileBinding?.layoutProfilePersonal?.edtProfilePersonalAddress!!
//                            .removeTextChangedListener(this)
                        s.replace(0, s.length, s.toString())
//                        fragmentProfileBinding?.layoutProfilePersonal?.edtProfilePersonalAddress?.clearFocus()
//                        fragmentProfileBinding?.layoutProfilePersonal?.edtProfilePersonalAddress!!
//                            .addTextChangedListener(this)
                    }
                }
            } else {
                if (fragmentProfileBinding?.layoutProfilePersonal?.placesRecyclerView!!
                        .visibility == View.VISIBLE
                ) {
                    fragmentProfileBinding?.layoutProfilePersonal?.placesRecyclerView!!.visibility =
                        View.GONE
                }
            }
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    }
*/

    override fun click(place: Place?) {
        latitude = place?.latLng?.latitude.toString()
        longitude = place?.latLng?.longitude.toString()
//        fragmentProfileBinding?.layoutProfilePersonal?.placesRecyclerView!!.visibility =  View.GONE
//        fragmentProfileBinding?.layoutProfilePersonal?.edtProfilePersonalAddress?.clearFocus()
//        fragmentProfileBinding?.layoutProfilePersonal?.edtProfilePersonalAddress?.setText(place.name)
//        fragmentProfileBinding?.layoutProfilePersonal?.edtProfilePersonalAddress?.setSelection(
//            fragmentProfileBinding?.layoutProfilePersonal?.edtProfilePersonalAddress!!.length()
//        )

//        baseActivity?.hideKeyboard()
//        baseActivity?.hideSoftKeyboard(fragmentProfileBinding?.layoutProfilePersonal?.edtProfilePersonalAddress!!)

    }

    // extension function to hide soft keyboard programmatically
    private fun Activity.hideSoftKeyboard(editText: EditText) {
        (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).apply {
            hideSoftInputFromWindow(editText.windowToken, 0)
        }
    }


}
//End image upload*********************************************************************************************************************************************


//
//






