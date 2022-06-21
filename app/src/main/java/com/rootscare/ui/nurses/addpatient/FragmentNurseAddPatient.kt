package com.rootscare.ui.nurses.addpatient

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProviders
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.addpatientresponse.AddPatientResponse
import com.rootscare.databinding.FragmentAddPatientForDoctorBookingBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment
import com.rootscare.ui.newaddition.providerlisting.FragmentProvderBooking
import com.rootscare.ui.nurses.nursesbookingappointment.FragmentNursesBookingAppointment
import com.rootscare.utilitycommon.SUCCESS_CODE
import com.rootscare.utils.ManagePermissions
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.*
import java.util.*

class FragmentNurseAddPatient :
    BaseFragment<FragmentAddPatientForDoctorBookingBinding, FragmentNurseAddPatientViewModel>(),
    FragmentNurseAddPatientNavigator {
    private var fragmentAddPatientForDoctorBookingBinding: FragmentAddPatientForDoctorBookingBinding? =
        null
    private var fragmentAddPatientForDoctorBookingViewModel: FragmentNurseAddPatientViewModel? =
        null
    private val GALLERY = 1
    private val CAMERA = 2
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 3
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    private var selectedGender = "Female"
    var imageFile: File? = null
    private var nurseId: String = ""


    private val PICK_IMAGE_REQUEST = 1

    var thumbnail: Bitmap? = null
    var bytes: ByteArrayOutputStream? = null

    private val REQUEST_CAMERA = 3
    var SELECT_FILE: Int = 4
    var REQUEST_ID_MULTIPLE_PERMISSIONS = 123
    var requested = false


    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_add_patient_for_doctor_booking
    override val viewModel: FragmentNurseAddPatientViewModel
        get() {
            fragmentAddPatientForDoctorBookingViewModel =
                ViewModelProviders.of(this).get(FragmentNurseAddPatientViewModel::class.java)
            return fragmentAddPatientForDoctorBookingViewModel as FragmentNurseAddPatientViewModel
        }

    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
        fun newInstance(nurseid: String): FragmentNurseAddPatient {
            val args = Bundle()
            args.putString("nurseid", nurseid)
            val fragment = FragmentNurseAddPatient()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentAddPatientForDoctorBookingViewModel?.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentAddPatientForDoctorBookingBinding = viewDataBinding
        val list = listOf(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(requireActivity(), list, PermissionsRequestCode)

        //check permissions states

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            managePermissions.checkPermissions()


        if (arguments != null && arguments?.getString("nurseid") != null) {
            nurseId = arguments?.getString("nurseid")!!
            Log.d("nurse Id", ": $nurseId")
        }
        fragmentAddPatientForDoctorBookingBinding?.imgRootscarePatientProfileImage?.setOnClickListener {
            fragmentAddPatientForDoctorBookingBinding?.edtPatientProfileImage?.performClick()
        }
        fragmentAddPatientForDoctorBookingBinding?.edtPatientProfileImage?.setOnClickListener {
            //  showPictureDialog()
            if (checkAndRequestPermissionsTest()) {
                captureImage()
            }
//            captureImage()
        }
        fragmentAddPatientForDoctorBookingBinding?.radioPatientGenderFemale?.setOnClickListener {
            selectedGender = "Female"
        }

        fragmentAddPatientForDoctorBookingBinding?.radioPatientGenderMale?.setOnClickListener {
            selectedGender = "Male"
        }

        fragmentAddPatientForDoctorBookingBinding?.btnRootscareAddpatientForDoctorBooking?.setOnClickListener {
            if (checkFieldValidationForPersonalDetails()) {
                apiInsertPatientFamilyApiCall()
            }

        }


    }


    //IMAGE SELECTION AND GET IMAGE PATH

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(activity)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { _, which ->
            when (which) {
                0 -> choosePhotoFromGallery()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    private fun choosePhotoFromGallery() {
        val galleryIntent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )

        startActivityForResult(galleryIntent, GALLERY)
    }

    private fun takePhotoFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, CAMERA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data.data
                    try {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(activity?.contentResolver, contentURI)
                        val path = saveImage(bitmap)
                        bitmapToFile(bitmap)
                        Toast.makeText(activity, "Image Saved!", Toast.LENGTH_SHORT).show()

                        fragmentAddPatientForDoctorBookingBinding?.imgRootscarePatientProfileImage?.setImageBitmap(
                            bitmap
                        )

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show()
                    }

                }

            } else if (requestCode == CAMERA) {


                val thumbnail = data!!.extras!!.get("data") as Bitmap
                fragmentAddPatientForDoctorBookingBinding?.imgRootscarePatientProfileImage?.setImageBitmap(
                    thumbnail
                )
                saveImage(thumbnail)
                bitmapToFile(thumbnail)
                Toast.makeText(activity, "Image Saved!", Toast.LENGTH_SHORT).show()
            }

            if (requestCode == REQUEST_CAMERA) {
                try {
                    onCaptureImageResult(data!!)
                } catch (e: Exception) {
                    println("Exception===>$e")
                }
            }
            //   onCaptureImageResult(data!!)
            else if (requestCode == SELECT_FILE) {
                try {
                    onSelectFromGalleryResult(data)
                } catch (e: Exception) {
                    println("Exception===>$e")
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (data != null) {
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri
                    Picasso.get().load(resultUri)
                        .into(fragmentAddPatientForDoctorBookingBinding?.imgRootscarePatientProfileImage)
                    imageFile = File(result.uri.path)
                    println("resultUri===>$resultUri")
                }

            }
        }

    }

    private fun saveImage(myBitmap: Bitmap): String {
        val bytes = ByteArrayOutputStream()
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val wallpaperDirectory = File(
            activity?.externalCacheDir!!.absolutePath.toString() + IMAGE_DIRECTORY
        )

        // have the object build the directory structure, if needed.
        Log.d("fee", wallpaperDirectory.toString())
        if (!wallpaperDirectory.exists()) {

            wallpaperDirectory.mkdirs()
        }

        try {
            Log.d("heel", wallpaperDirectory.toString())
            val f = File(
                wallpaperDirectory, ((Calendar.getInstance()
                    .timeInMillis).toString() + ".jpg")
            )
            //     File file = new File("/storage/emulated/0/Download/Corrections 6.jpg");


            f.createNewFile()
            // imageFile=f

            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                activity,
                arrayOf(f.path),
                arrayOf("image/jpeg"), null
            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.absolutePath)


            return f.absolutePath
        } catch (e1: IOException) {
            e1.printStackTrace()
        }

        return ""
    }


    // Receive the permissions request result
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
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

    // Method to save an bitmap to a file
    private fun bitmapToFile(bitmap: Bitmap): Uri {
        // Get the context wrapper
        val wrapper = ContextWrapper(activity)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        imageFile = file
        try {
            // Compress the bitmap and save in jpg format
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        //updateProfileImageApiCall(imageFile!!)
        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath)


    }

    private fun checkFieldValidationForPersonalDetails(): Boolean {
        if (TextUtils.isEmpty(fragmentAddPatientForDoctorBookingBinding?.edtAddpatientFname?.text?.toString())) {
            Toast.makeText(activity, "Please enter patient first name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(fragmentAddPatientForDoctorBookingBinding?.edtAddpatientLname?.text?.toString())) {
            Toast.makeText(activity, "Please enter patient last name", Toast.LENGTH_SHORT).show()
            return false
        }

//        if (TextUtils.isEmpty(fragmentAddPatientForDoctorBookingBinding?.edtAddpatientEmail?.text?.toString())) {
//            Toast.makeText(activity, "Please enter patient email", Toast.LENGTH_SHORT).show()
//            return false
//        }
//        if (TextUtils.isEmpty(fragmentAddPatientForDoctorBookingBinding?.edtAddpatientPhonenumber?.text?.toString())) {
//            Toast.makeText(activity, "Please enter patient phone number", Toast.LENGTH_SHORT).show()
//            return false
//        }

        if (TextUtils.isEmpty(fragmentAddPatientForDoctorBookingBinding?.edtAddpatientAge?.text?.toString())) {
            Toast.makeText(activity, "Please enter patient age", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }


    private fun apiInsertPatientFamilyApiCall() {
        baseActivity?.showLoading()
        val patient_id = fragmentAddPatientForDoctorBookingViewModel!!.appSharedPref!!.userId!!
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val first_name =
            fragmentAddPatientForDoctorBookingBinding?.edtAddpatientFname?.text?.toString()!!
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val last_name =
            fragmentAddPatientForDoctorBookingBinding?.edtAddpatientLname?.text?.toString()!!
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val email =
            fragmentAddPatientForDoctorBookingBinding?.edtAddpatientEmail?.text?.toString()!!
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val phone_number =
            fragmentAddPatientForDoctorBookingBinding?.edtAddpatientPhonenumber?.text?.toString()!!
                .toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val gender = selectedGender.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val age = fragmentAddPatientForDoctorBookingBinding?.edtAddpatientAge?.text?.toString()!!
            .toRequestBody("multipart/form-data".toMediaTypeOrNull())
//        val status = RequestBody.create(MediaType.parse("multipart/form-data"), fragmentProfileBinding?.layoutProfilePersonal?.txtProfilePersonalStatus?.text?.toString())
//        val status = RequestBody.create(MediaType.parse("multipart/form-data"),"1")
        if (imageFile != null) {
            val image = imageFile!!.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("image", imageFile?.name, image)
//            fragmentProfileViewModel?.apieditpatientprofilepersonal(userId,first_name,last_name,id_number,status,multipartBody)
//            email,phone_number,
            fragmentAddPatientForDoctorBookingViewModel?.apiinsertpatientfamily(
                patient_id,
                first_name,
                last_name,
                multipartBody,
                gender,
                age
            )

        } else {
            val image = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val multipartBody = MultipartBody.Part.createFormData("image", "", image)
//            fragmentProfileViewModel?.apieditpatientprofilepersonal(userId,first_name,last_name,id_number,status,multipartBody)email,phone_number,
            fragmentAddPatientForDoctorBookingViewModel?.apiinsertpatientfamily(
                patient_id,
                first_name,
                last_name,
                multipartBody,
                gender,
                age
            )
            //Toast.makeText(activity, "Image can not be blank", Toast.LENGTH_SHORT).show()
        }
    }

    override fun successAddPatientResponse(addPatientResponse: AddPatientResponse?) {
        baseActivity?.hideLoading()
        if (addPatientResponse?.code.equals(SUCCESS_CODE)) {
            Toast.makeText(activity, addPatientResponse?.message, Toast.LENGTH_SHORT).show()
      //   (activity as HomeActivity).checkInBackstack(FragmentNursesBookingAppointment.newInstance(nurseId))
            FragmentProvderBooking.IS_MEMBER_ADDED = true
          (activity as? HomeActivity)?.onBackPressed()

        } else {
            showToast(addPatientResponse?.message ?:"")
        }

    }

    override fun errorAddPatientResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        showToast(throwable?.message ?: getString(R.string.something_went_wrong))
    }


    // Callback with the request from calling requestPermissions(...)
    ///New Image Upload Section


    //image upload*********************************************************************************************************************************************
    private fun checkAndRequestPermissionsTest(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            val permissionCamera = ContextCompat.checkSelfPermission(
               requireActivity(),
                Manifest.permission.CAMERA
            )
            val permissionWriteExternalStorage = ContextCompat.checkSelfPermission(
               requireActivity(),
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
        if (!TextUtils.isEmpty(imageFile?.path) && File(imageFile?.path)
                .exists()
        ) {
            CropImage.activity(Uri.fromFile(File(imageFile?.path)))
                .setCropShape(CropImageView.CropShape.OVAL)
                .setInitialCropWindowPaddingRatio(0F)
                .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(requireActivity())
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


}