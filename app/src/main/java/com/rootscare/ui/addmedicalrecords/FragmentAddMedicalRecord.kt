package com.rootscare.ui.addmedicalrecords

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.DatePickerDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.dialog.inputfilename.FileNameInputDialog
import com.interfaces.RecyclerViewItemClick
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.response.addmedicallrecords.AddlabTestImageSelectionModel
import com.rootscare.data.model.api.response.medicalrecordresponse.MedicalRecordListResponse
import com.rootscare.databinding.FragmentAddMedicalRecordsBinding
import com.rootscare.ui.addmedicalrecords.adapter.FilesListingAdapter
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment
import com.rootscare.utils.*
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.*


class FragmentAddMedicalRecord :
    BaseFragment<FragmentAddMedicalRecordsBinding, FragmentAddMedicalRecordViewModel>(),
    FragmentAddMedicalRecordNavigator, PermissionUtils.PermissionResultCallback {
    private var fragmentAddMedicalRecordsBinding: FragmentAddMedicalRecordsBinding? = null
    private var fragmentAddMedicalRecordViewModel: FragmentAddMedicalRecordViewModel? = null

    private var permissionUtils: PermissionUtils? = null
    private var permissions = ArrayList<String>()
    private var isImageCaptureFlow = false
    private var screeningDate: String? = null
    private var dataFileList: LinkedList<AddlabTestImageSelectionModel> = LinkedList()

    //    private var filePickerDialog: FilePickerDialog? = null
    private var booleanClickCheck = true
    var monthstr: String = ""
    var dayStr: String = ""
    private var filesListingAdapter: FilesListingAdapter? = null
    var selectedYear = 0
    var selectedmonth = 0
    var selectedday = 0
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_add_medical_records
    override val viewModel: FragmentAddMedicalRecordViewModel
        get() {
            fragmentAddMedicalRecordViewModel =
                ViewModelProviders.of(this).get(FragmentAddMedicalRecordViewModel::class.java)
            return fragmentAddMedicalRecordViewModel as FragmentAddMedicalRecordViewModel
        }

    companion object {
        fun newInstance(): FragmentAddMedicalRecord {
            val args = Bundle()
            val fragment = FragmentAddMedicalRecord()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentAddMedicalRecordViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentAddMedicalRecordsBinding = viewDataBinding
        permissionUtils = PermissionUtils(requireActivity())
        fragmentAddMedicalRecordsBinding?.tvSelectTestDate?.setOnClickListener {
            // TODO Auto-generated method stub
            val c = Calendar.getInstance()
            val year = c.get(Calendar.YEAR)
            val month = c.get(Calendar.MONTH)
            val day = c.get(Calendar.DAY_OF_MONTH)
//            datePicker.setMinDate(System.currentTimeMillis() - 1000)


            val dpd = DatePickerDialog(
                requireActivity(),
                DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->

                    // Display Selected date in textbox
                    // fragmentAdmissionFormBinding?.txtDob?.setText("" + dayOfMonth + "-" + (monthOfYear+1) + "-" + year)
                    if ((monthOfYear + 1) < 10) {
                        monthstr = "0" + (monthOfYear + 1)
                    } else {
                        monthstr = (monthOfYear + 1).toString()
                    }

                    if (dayOfMonth < 10) {
                        dayStr = "0$dayOfMonth"

                    } else {
                        dayStr = dayOfMonth.toString()
                    }
                    selectedYear = year
                    selectedmonth = monthOfYear
                    selectedday = dayOfMonth
//                fragmentSeedStockedBinding?.txtLsfSeedstockDateofStocking?.setText("" + year + "-" + monthstr + "-" + dayStr)
                    fragmentAddMedicalRecordsBinding?.tvSelectTestDate?.text =
                        "$year-$monthstr-$dayStr"
                    screeningDate =
                        fragmentAddMedicalRecordsBinding?.tvSelectTestDate?.text?.toString()


                },
                year,
                month,
                day
            )

            dpd.show()
            //Get the DatePicker instance from DatePickerDialog
            //Get the DatePicker instance from DatePickerDialog
            val dp = dpd.datePicker
            if (selectedYear != 0 && selectedmonth != 0 && selectedday != 0) {
                dp.updateDate(selectedYear, selectedmonth, selectedday)
            } else {
                dp.updateDate(year, c.get(Calendar.MONTH), c.get(Calendar.DATE))
//                c.set(year, c.get(Calendar.MONTH), c.get(Calendar.DATE))
            }

            //dp.minDate=System.currentTimeMillis() - 1000
        }
        fragmentAddMedicalRecordsBinding?.imageviewUploadSign?.setOnClickListener {
            selectEitherPDFOrImage()
        }
        fragmentAddMedicalRecordsBinding?.tvUploadDocumentTitle?.setOnClickListener {
            selectEitherPDFOrImage()
        }
        fragmentAddMedicalRecordsBinding?.imgAddTestResult?.setOnClickListener {
            selectEitherPDFOrImage()
        }
        fragmentAddMedicalRecordsBinding?.tvUploadTestResult?.setOnClickListener {
            selectEitherPDFOrImage()
        }

        fragmentAddMedicalRecordsBinding?.tvCancel?.setOnClickListener {
            activity?.onBackPressed()
        }
        fragmentAddMedicalRecordsBinding?.tvSave?.setOnClickListener {
            if (checkValidation() && booleanClickCheck) {
                booleanClickCheck = false
                apiCallToInserMedicalRecordData(filesListingAdapter?.data)
                //     saveDataWithoutImage()
            }
        }
    }

    override fun permissionGranted(request_code: Int) {
        Log.i("request_code", "" + request_code)
        if (!isImageCaptureFlow) {
            openFileMangerToPickUpFile()
        } else {
            openCamera()
        }
    }

    override fun partialPermissionGranted(
        request_code: Int,
        granted_permissions: ArrayList<String>
    ) {
        permissions = ArrayList()
        if (!isImageCaptureFlow) {
            for (i in granted_permissions.indices) {
                when (granted_permissions[i]) {
                    Manifest.permission.READ_EXTERNAL_STORAGE -> permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
            permissionUtils?.check_permission(
                permissions,
                resources.getString(R.string.need_these_permission),
                AppConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            for (i in granted_permissions.indices) {
                when (granted_permissions[i]) {
                    Manifest.permission.WRITE_EXTERNAL_STORAGE -> permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    Manifest.permission.CAMERA -> permissions.add(Manifest.permission.CAMERA)
                }
            }
            permissionUtils?.check_permission(
                permissions,
                resources.getString(R.string.need_these_permission),
                AppConstants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_AND_CAMERA
            )
        }
    }

    override fun permissionDenied(request_code: Int) {
    }

    override fun neverAskAgain(request_code: Int) {
    }

    private fun selectEitherPDFOrImage() {
//        isImageCaptureFlow = true
//        checkPermissionAndProceedForImageCapture()
        val items = arrayOf(
            resources.getString(R.string.select_pdf),
            resources.getString(R.string.capture_or_select_image)
        )
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireActivity())
        builder.setItems(items) { _, which ->
            if (which == 0) {
                isImageCaptureFlow = false
                checkPermissionAndProceedForFilePickUp()
            } else if (which == 1) {
                isImageCaptureFlow = true
                checkPermissionAndProceedForImageCapture()
            }
        }
        builder.show()
    }

    private fun checkPermissionAndProceedForFilePickUp() {
        permissions.clear()
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (hasPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            openFileMangerToPickUpFile()
        } else {
            getPermissionTo()
        }
    }

    private fun checkPermissionAndProceedForImageCapture() {
        permissions.clear()
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        permissions.add(Manifest.permission.CAMERA)
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && hasPermission(Manifest.permission.CAMERA)) {
            openCamera()
        } else {
            getPermissionTo()
        }
    }

    /* Getting file from external storage */
    /* Start */
    private fun openFileMangerToPickUpFile() {

        val intent = Intent()
            .setType("*/*")
            .setAction(Intent.ACTION_GET_CONTENT)

        startActivityForResult(
            Intent.createChooser(intent, "Select a file"),
            AppConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
        )
    }

    /* Permissions for Camera and accessing external storage */
    /* Start */
    @TargetApi(Build.VERSION_CODES.M)
    fun getPermissionTo() {
        if (!isImageCaptureFlow) {
            permissionUtils?.check_permission(
                permissions,
                resources.getString(R.string.need_these_permission),
                AppConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        } else {
            permissionUtils?.check_permission(
                permissions,
                resources.getString(R.string.need_these_permission),
                AppConstants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_AND_CAMERA
            )
        }
    }
/* Open camera and get image */
    /* Start */

    private fun openCamera() {
//        mCameraIntentHelper!!.startCameraIntent()
        CropImage.activity()
            .setCropShape(
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) CropImageView.CropShape.RECTANGLE
                else (CropImageView.CropShape.OVAL)
            )
            .setInitialCropWindowPaddingRatio(0F)
            .setAspectRatio(1, 1)
            .setGuidelines(CropImageView.Guidelines.ON)
            .setActivityTitle("Crop")
            .setOutputCompressQuality(50)
            .start(requireActivity())
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            AppConstants.MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE_AND_CAMERA -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty()) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    if (grantResults.size == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        openCamera()
                    } else if (grantResults.size == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                    ) {
                        openCamera()
                    }
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return
            }
            AppConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openFileMangerToPickUpFile()
                } else {

                }
                return
            }
        }
    }
    /* End */


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != Activity.RESULT_CANCELED) {
            if (!isImageCaptureFlow) {
                if (requestCode == AppConstants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
                    if (resultCode == RESULT_OK) {
                        val resultUri = data!!.data
                        var file: File? = File(resultUri?.path)
                        if (resultUri != null) {
                            FileNameInputDialog(
                                requireActivity(),
                                object : FileNameInputDialog.CallbackAfterDateTimeSelect {
                                    override fun selectDateTime(dateTime: String) {
                                        val imageSelectionModel = AddlabTestImageSelectionModel()
                                        val file =
                                            FileUtil.from(context as HomeActivity, resultUri)
                                        val fileCacheDir =
                                            File(activity?.cacheDir, resultUri.lastPathSegment)
                                        if (file.exists()) {
                                            imageSelectionModel.file = file
                                            imageSelectionModel.filePath = file.absolutePath
                                            imageSelectionModel.rawFileName = file.name
                                            imageSelectionModel.fileName = "${
                                                DateTimeUtils.getFormattedDate(
                                                    Date(),
                                                    "dd/MM/yyyy_HH:mm:ss"
                                                )
                                            }_${file.name}"
                                            imageSelectionModel.fileNameAsOriginal = dateTime

                                            if (resultUri.path?.contains("pdf")!!) {
                                                imageSelectionModel.type = "pdf"
                                            } else if (resultUri.path?.contains("png")!! || resultUri.path?.contains(
                                                    "jpg"
                                                )!!
                                            ) {
                                                imageSelectionModel.type = "image"
                                            } else if (resultUri.path?.contains("PDF")!!) {
                                                imageSelectionModel.type = "pdf"
                                            }
                                            dataFileList.add(imageSelectionModel)
                                            setUpRecyclerview()
                                            imageListingReflectData()
                                            Log.d("check_path", ": ${imageSelectionModel.filePath}")
                                            Log.d("check_file_get", ": $file")
                                            hideKeyboard()
                                        } else {
                                            Log.d("file_does_not_exists", ": " + true)
                                        }

                                    }
                                }).show(activity?.supportFragmentManager!!)
                        }
//                    if (file != null && file.exists()) {
//
//
//                    }
                    }
                }

            } else {
//            mCameraIntentHelper!!.onActivityResult(requestCode, resultCode, intent)
                if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                    val imageSelectionModel = AddlabTestImageSelectionModel()
                    val result = CropImage.getActivityResult(data)
                    if (resultCode == RESULT_OK) {
                        imageSelectionModel.imageDataFromCropLibrary = result
                        val resultUri = result.uri
                        if (resultUri != null) { // Get file from cache directory
                            FileNameInputDialog(
                                requireActivity(),
                                object : FileNameInputDialog.CallbackAfterDateTimeSelect {
                                    override fun selectDateTime(dateTime: String) {
                                        val fileCacheDir =
                                            File(activity?.cacheDir, resultUri.lastPathSegment)
                                        if (fileCacheDir.exists()) {
//                                    imageSelectionModel.file = fileCacheDir
                                            imageSelectionModel.file =
                                                MyImageCompress.compressImageFilGottenFromCache(
                                                    activity,
                                                    resultUri,
                                                    10
                                                )
                                            imageSelectionModel.filePath = resultUri.toString()
                                            imageSelectionModel.rawFileName =
                                                resultUri.lastPathSegment
                                            var tempNameExtension = ""
                                            if (resultUri.lastPathSegment?.contains(".jpg")!!) {
                                                tempNameExtension = ".jpg"
                                            } else if (resultUri.lastPathSegment?.contains(".png")!!) {
                                                tempNameExtension = ".png"
                                            }
                                            imageSelectionModel.fileName =
                                                "${dateTime}_${
                                                    DateTimeUtils.getFormattedDate(
                                                        Date(),
                                                        "dd/MM/yyyy_HH:mm:ss"
                                                    )
                                                }${tempNameExtension}"
//                                    imageSelectionModel.fileNameAsOriginal = "${dateTime}${tempNameExtension}"
                                            imageSelectionModel.fileNameAsOriginal = dateTime
                                            if (activity?.contentResolver?.getType(resultUri) == null) {
                                                imageSelectionModel.type = "image"
                                            } else {
                                                imageSelectionModel.type =
                                                    activity?.contentResolver?.getType(resultUri)
                                            }
                                            dataFileList.add(imageSelectionModel)
//                                    dataFileList.add(modifyImageSize(imageSelectionModel))
                                            setUpRecyclerview()
//                            filesListingAdapter?.data?.add(imageSelectionModel)
//                            fileListing.add(imageSelectionModel)
                                            imageListingReflectData()
                                            Log.d("check_path", ": $resultUri")
                                            Log.d("check_file_get", ": $fileCacheDir")
                                        } else {
                                            Log.d("file_does_not_exists", ": " + true)
                                        }
                                        hideKeyboard()
                                    }
                                }).show(activity?.supportFragmentManager!!)
                        }
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        val error = result.error
                        Log.d("check_error", ": " + error.message)
                    }
                }
            }
        }


    }


    /* Image Listing Set up */
    private fun setUpRecyclerview() {
        with(fragmentAddMedicalRecordsBinding!!) {
            filesListingAdapter = FilesListingAdapter(context as HomeActivity)
//            val layoutManager = GridLayoutManager(context, 1, GridLayoutManager.HORIZONTAL, false)
            val layoutManager = GridLayoutManager(context, 2)
            fragmentAddMedicalRecordsBinding?.recyclerviewUploadFileList?.layoutManager =
                layoutManager
            fragmentAddMedicalRecordsBinding?.recyclerviewUploadFileList?.setHasFixedSize(true)
            fragmentAddMedicalRecordsBinding?.recyclerviewUploadFileList?.addItemDecoration(
                EqualSpacingItemDecoration(10)
            ) // 10px. In practice, you'll want to use getDimensionPixelSize
            fragmentAddMedicalRecordsBinding?.recyclerviewUploadFileList?.adapter =
                filesListingAdapter
            filesListingAdapter?.data = dataFileList
            filesListingAdapter?.recyclerViewItemClick = object : RecyclerViewItemClick {
                override fun onItemClick(position: Int) {
                    // for delete
//                    dataFileList.removeAt(position)
                    filesListingAdapter?.data?.removeAt(position)
                    filesListingAdapter?.notifyItemRemoved(position)
                    filesListingAdapter?.notifyItemRangeChanged(
                        position,
                        filesListingAdapter?.data?.size!!
                    )
                    imageListingReflectData()
                }
            }
        }
        imageListingReflectData()
    }

    private fun imageListingReflectData() {
        with(fragmentAddMedicalRecordsBinding!!) {
            if (dataFileList.size > 0) {
                fragmentAddMedicalRecordsBinding?.recyclerviewUploadFileList?.visibility =
                    View.VISIBLE
            } else {
                fragmentAddMedicalRecordsBinding?.recyclerviewUploadFileList?.visibility = View.GONE
                filesListingAdapter?.data?.clear()
            }
        }
        showHideUploadDocumentTitle()
    }

    private fun showHideUploadDocumentTitle() {
        with(fragmentAddMedicalRecordsBinding!!) {
            if (dataFileList.size > 0) {
                fragmentAddMedicalRecordsBinding?.tvUploadDocumentTitle?.visibility = View.GONE
                fragmentAddMedicalRecordsBinding?.imageviewUploadSign?.visibility = View.GONE
                fragmentAddMedicalRecordsBinding?.imgAddTestResult?.visibility = View.VISIBLE
                fragmentAddMedicalRecordsBinding?.tvUploadTestResult?.visibility = View.VISIBLE
            } else {
                fragmentAddMedicalRecordsBinding?.tvUploadDocumentTitle?.visibility = View.VISIBLE
                fragmentAddMedicalRecordsBinding?.imageviewUploadSign?.visibility = View.VISIBLE
                fragmentAddMedicalRecordsBinding?.imgAddTestResult?.visibility = View.GONE
                fragmentAddMedicalRecordsBinding?.tvUploadTestResult?.visibility = View.GONE
            }
        }
    }

    override fun successMedicalFileDeleteResponse(medicalFileDeleteResponse: MedicalRecordListResponse?) {
        baseActivity?.hideLoading()
        if (medicalFileDeleteResponse?.code.equals("200")) {
          //  (activity as HomeActivity).checkInBackstack(FragmentMedicalRecords.newInstance())
        } else {
            Toast.makeText(activity, medicalFileDeleteResponse?.message, Toast.LENGTH_SHORT).show()
         //   (activity as HomeActivity).checkInBackstack(FragmentMedicalRecords.newInstance())
        }
    }

    override fun errorMedicalFileDeleteResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(HomeFragment.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
          //  (activity as HomeActivity).checkInBackstack(FragmentMedicalRecords.newInstance())
        }
    }


    private fun apiCallToInserMedicalRecordData(data: LinkedList<AddlabTestImageSelectionModel>?) {

        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val fileList = ArrayList<MultipartBody.Part>()

            val user_id = fragmentAddMedicalRecordViewModel?.appSharedPref?.userId?.let {
                it
                    .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            }
            val date = fragmentAddMedicalRecordsBinding?.tvSelectTestDate?.text?.toString()?.let {
                it
                    .toRequestBody("multipart/form-data".toMediaTypeOrNull())
            }


            var titleString = ""
            var p = 0
            for (i in data?.indices!!) {
                val title: String = data[i].fileNameAsOriginal!!
                if (p == 0) {
                    titleString = title
                    p++
                } else {
                    titleString = "$titleString,$title"
                }
            }

            val title = titleString.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            for (i in data.indices) {
                val file: File = data[i].file!!
                var requestFile: RequestBody?
                /*if ((data[i].fileName.toLowerCase().contains("pdf"))) {
                    requestFile = RequestBody.create(MediaType.parse(contentResolver.getType(data[i].filePath)), file)
                } else {
                    requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
                }*/
//                requestFile = RequestBody.create(MediaType.parse("*/*"), file)
////                val body = MultipartBody.Part.createFormData(AppConstants.UPLOAD_IMAGE_KEY, file.name, requestFile!!)
                requestFile = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//                val body = MultipartBody.Part.createFormData(data[i].fileNameAsOriginal, data[i].fileName, requestFile!!)
                val body = MultipartBody.Part.createFormData(
                    data[i].fileNameAsOriginal.toString(),
                    data[i].rawFileName,
                    requestFile
                )

//                val body = MultipartBody.Part.createFormData("file", data[i].rawFileName, requestFile!!)

                fileList.add(body)
            }

            if (user_id != null) {
                if (date != null) {
                    fragmentAddMedicalRecordViewModel?.apiinsertmedicalrecord(
                        user_id,
                        date,
                        title,
                        fileList
                    )
                }
            }
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }

    }


    private fun checkValidation(): Boolean {
        with(fragmentAddMedicalRecordsBinding!!) {
            if (screeningDate == null) {
                Toast.makeText(activity, "Please select report date", Toast.LENGTH_SHORT).show()
                return false
            }

            if (dataFileList.size == 0) {
                Toast.makeText(
                    activity,
                    "Please select at least one file to upload",
                    Toast.LENGTH_SHORT
                ).show()
//                CommonDialog.showDialogWithOnlyOneButton(activity as HomeActivity, object : DialogClickCallback {
//                    override fun onConfirm() {
//
//                    }
//
//                    override fun onDismiss() {
//
//                    }
//
//                }, resources.getString(R.string.warning), resources.getString(R.string.please_select_at_least_one_file_to_upload), resources.getString(R.string.ok))

                return false
            }
            if (dataFileList.size > 4) {


                Toast.makeText(
                    activity,
                    "You have selected maximum number of files",
                    Toast.LENGTH_SHORT
                ).show()
//                CommonDialog.showDialogWithOnlyOneButton(activity as HomeActivity, object :
//                    DialogClickCallback {
//                    override fun onConfirm() {
//
//                    }
//
//                    override fun onDismiss() {
//
//                    }
//
//                }, resources.getString(R.string.warning), resources.getString(R.string.you_have_selected_maximum_number_of_files), resources.getString(R.string.ok))

                return false
            }

        }
        return true
    }

}