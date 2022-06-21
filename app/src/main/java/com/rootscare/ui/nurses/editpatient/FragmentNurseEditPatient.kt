package com.rootscare.ui.nurses.editpatient

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
import com.bumptech.glide.Glide
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.response.editpatientfamilymemberresponse.EditFamilyMemberResponse
import com.rootscare.databinding.FragmentEditPatientFamilyMemberBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment
import com.rootscare.ui.nurses.nursesbookingappointment.FragmentNursesBookingAppointment
import com.rootscare.utils.ManagePermissions
import com.squareup.picasso.Picasso
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.*
import java.util.*

class FragmentNurseEditPatient :
    BaseFragment<FragmentEditPatientFamilyMemberBinding, FragmentNurseEditPatientViewModel>(),
    FragmentNurseEditPatientNavigator {
    private var fragmentEditPatientFamilyMemberBinding: FragmentEditPatientFamilyMemberBinding? =
        null
    private var fragmentEditPatientFamilyMemberViewModel: FragmentNurseEditPatientViewModel? = null
    private val GALLERY = 1
    private val CAMERA = 2
    private val MY_PERMISSIONS_REQUEST_READ_CONTACTS = 3
    private val PermissionsRequestCode = 123
    private lateinit var managePermissions: ManagePermissions
    private var selectedGender = ""
    var imageFile: File? = null
    private var nurseId: String = ""
    private var patientId: String = ""
    private var patientimage: String = ""
    private var patientFirstName: String = ""
    private var patientLastname: String = ""
    private var patientEmail: String = ""
    private var patientPhonenumber: String = ""
    private var patientAge: String = ""


    private val PICK_IMAGE_REQUEST = 1

    var thumbnail: Bitmap? = null
    var bytes: ByteArrayOutputStream? = null

    private val REQUEST_CAMERA = 4
    var SELECT_FILE: Int = 5
    var REQUEST_ID_MULTIPLE_PERMISSIONS = 123
    var requested = false

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_edit_patient_family_member
    override val viewModel: FragmentNurseEditPatientViewModel
        get() {
            fragmentEditPatientFamilyMemberViewModel =
                ViewModelProviders.of(this).get(FragmentNurseEditPatientViewModel::class.java!!)
            return fragmentEditPatientFamilyMemberViewModel as FragmentNurseEditPatientViewModel
        }

    companion object {
        private val IMAGE_DIRECTORY = "/demonuts"
        fun newInstance(
            nurseid: String,
            id: String,
            imagename: String,
            firstname: String,
            lastname: String,
            age: String,
            gender: String
        ): FragmentNurseEditPatient {

//            email:String,phoneno:String,
            val args = Bundle()
            args.putString("nurseid", nurseid)
            args.putString("id", id)
            args.putString("imagename", imagename)
            args.putString("firstname", firstname)
            args.putString("lastname", lastname)
//            args.putString("email",email)
//            args.putString("phoneno",phoneno)
            args.putString("age", age)
            args.putString("gender", gender)


            val fragment = FragmentNurseEditPatient()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentEditPatientFamilyMemberViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentEditPatientFamilyMemberBinding = viewDataBinding
        fragmentEditPatientFamilyMemberBinding?.llMain?.setOnClickListener(View.OnClickListener {
            baseActivity?.hideKeyboard()
        })
        val list = listOf<String>(
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        // Initialize a new instance of ManagePermissions class
        managePermissions = ManagePermissions(this!!.activity!!, list, PermissionsRequestCode)
        //check permissions states

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            managePermissions.checkPermissions()


        if (arguments != null && arguments?.getString("nurseid") != null) {
            nurseId = arguments?.getString("nurseid")!!
            Log.d("nurseid Id", ": " + nurseId)
        }

        if (arguments != null && arguments?.getString("id") != null) {
            patientId = arguments?.getString("id")!!
            Log.d("patientId", ": " + patientId)
        }

        if (arguments != null && arguments?.getString("imagename") != null) {
            patientimage = arguments?.getString("imagename")!!
            Log.d("patientimage", ": " + patientimage)
        }
        if (arguments != null && arguments?.getString("firstname") != null) {
            patientFirstName = arguments?.getString("firstname")!!
            Log.d("patientFirstName", ": " + patientFirstName)
        }
        if (arguments != null && arguments?.getString("lastname") != null) {
            patientLastname = arguments?.getString("lastname")!!
            Log.d("patientLastname", ": " + patientLastname)
        }
//        if (arguments!=null && arguments?.getString("email")!=null){
//            patientEmail = arguments?.getString("email")!!
//            Log.d("patientEmail", ": " + patientEmail )
//        }
//        if (arguments!=null && arguments?.getString("phoneno")!=null){
//            patientPhonenumber = arguments?.getString("phoneno")!!
//            Log.d("patientPhonenumber", ": " + patientPhonenumber )
//        }
        if (arguments != null && arguments?.getString("age") != null) {
            patientAge = arguments?.getString("age")!!
            Log.d("age", ": " + patientAge)
        }
        if (arguments != null && arguments?.getString("gender") != null) {
            selectedGender = arguments?.getString("gender")!!
            Log.d("gender", ": " + selectedGender)
        }

        fragmentEditPatientFamilyMemberBinding?.edtPatientProfileImage?.setOnClickListener(View.OnClickListener {
            //  showPictureDialog()
            if (checkAndRequestPermissionsTest()) {
                captureImage()
            }

        })
        fragmentEditPatientFamilyMemberBinding?.radioPatientGenderFemale?.setOnClickListener(View.OnClickListener {
            selectedGender = "Female"
        })

        fragmentEditPatientFamilyMemberBinding?.radioPatientGenderMale?.setOnClickListener(View.OnClickListener {
            selectedGender = "Male"
        })

        fragmentEditPatientFamilyMemberBinding?.btnRootscareAddpatientForDoctorBooking?.setOnClickListener(
            View.OnClickListener {
                if (checkFieldValidationForPersonalDetails()) {
                    apieditpatientfamilyApiCall()
                }

            })


        if (patientFirstName != null && !patientFirstName.equals("")) {
            fragmentEditPatientFamilyMemberBinding?.edtAddpatientFname?.setText(patientFirstName)
        } else {
            patientFirstName = ""
        }

        if (patientLastname != null && !patientLastname.equals("")) {
            fragmentEditPatientFamilyMemberBinding?.edtAddpatientLname?.setText(patientFirstName)
        } else {
            patientLastname = ""
        }
//        if (patientEmail!=null && !patientEmail.equals("")){
//            fragmentEditPatientFamilyMemberBinding?.edtAddpatientEmail?.setText(patientEmail)
//        }else{
//            patientEmail=""
//        }
//
//        if (patientPhonenumber!=null && !patientPhonenumber.equals("")){
//            fragmentEditPatientFamilyMemberBinding?.edtAddpatientPhonenumber?.setText(patientPhonenumber)
//        }else{
//            patientPhonenumber=""
//        }

        if (patientAge != null && !patientAge.equals("")) {
            fragmentEditPatientFamilyMemberBinding?.edtAddpatientAge?.setText(patientAge)
        } else {
            patientAge = ""
        }

        if (selectedGender != null && !selectedGender.equals("")) {
            if (selectedGender.equals("Female")) {
                fragmentEditPatientFamilyMemberBinding?.radioPatientGenderFemale?.isChecked = true
            } else if (selectedGender.equals("Male")) {
                fragmentEditPatientFamilyMemberBinding?.radioPatientGenderMale?.isChecked = true
            }

        } else {
            selectedGender = ""
        }
        if (patientimage != null && !patientimage.equals("")) {
            Log.d(
                "PROFILE IMAGE---->",
                "http://166.62.54.122/rootscare/" + "uploads/images/" + (patientimage)
            )
            Glide.with(this!!.activity!!)
                .load("http://166.62.54.122/rootscare/" + "uploads/images/" + (patientimage))
                .into(fragmentEditPatientFamilyMemberBinding?.imgRootscarePatientProfileImage!!)
        } else {
            patientimage = ""
            Glide.with(this!!.activity!!)
                .load(this.getResources().getDrawable(R.drawable.profile_no_image))
                .into(fragmentEditPatientFamilyMemberBinding?.imgRootscarePatientProfileImage!!)
        }

    }

    //IMAGE SELECTION AND GET IMAGE PATH

    private fun showPictureDialog() {
        val pictureDialog = AlertDialog.Builder(activity)
        pictureDialog.setTitle("Select Action")
        val pictureDialogItems = arrayOf("Select photo from gallery", "Capture photo from camera")
        pictureDialog.setItems(
            pictureDialogItems
        ) { dialog, which ->
            when (which) {
                0 -> choosePhotoFromGallary()
                1 -> takePhotoFromCamera()
            }
        }
        pictureDialog.show()
    }

    fun choosePhotoFromGallary() {
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


    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)
        /* if (resultCode == this.RESULT_CANCELED)
         {
         return
         }*/
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentURI = data!!.data
                    try {
                        val bitmap =
                            MediaStore.Images.Media.getBitmap(activity?.contentResolver, contentURI)
                        val path = saveImage(bitmap)
                        bitmapToFile(bitmap)
                        Toast.makeText(activity, "Image Saved!", Toast.LENGTH_SHORT).show()

                        fragmentEditPatientFamilyMemberBinding?.imgRootscarePatientProfileImage?.setImageBitmap(
                            bitmap
                        )

                    } catch (e: IOException) {
                        e.printStackTrace()
                        Toast.makeText(activity, "Failed!", Toast.LENGTH_SHORT).show()
                    }

                }

            } else if (requestCode == CAMERA) {
                val thumbnail = data!!.extras!!.get("data") as Bitmap
                fragmentEditPatientFamilyMemberBinding?.imgRootscarePatientProfileImage?.setImageBitmap(
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
                    println("Exception===>${e.toString()}")
                }
            }
            //   onCaptureImageResult(data!!)
            else if (requestCode == SELECT_FILE) {
                try {
                    onSelectFromGalleryResult(data)
                } catch (e: Exception) {
                    println("Exception===>${e.toString()}")
                }

            } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (data != null) {
                    val result = CropImage.getActivityResult(data)
                    val resultUri = result.uri
                    Picasso.get().load(resultUri)
                        .into(fragmentEditPatientFamilyMemberBinding?.imgRootscarePatientProfileImage)
                    imageFile = File(result.uri.path)
                    println("resultUri===>$resultUri")
                }

            }
        }

    }

    fun saveImage(myBitmap: Bitmap): String {
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
                    .getTimeInMillis()).toString() + ".jpg")
            )
            //     File file = new File("/storage/emulated/0/Download/Corrections 6.jpg");


            f.createNewFile()
            // imageFile=f

            val fo = FileOutputStream(f)
            fo.write(bytes.toByteArray())
            MediaScannerConnection.scanFile(
                activity,
                arrayOf(f.getPath()),
                arrayOf("image/jpeg"), null
            )
            fo.close()
            Log.d("TAG", "File Saved::--->" + f.getAbsolutePath())


            return f.getAbsolutePath()
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
        if (TextUtils.isEmpty(fragmentEditPatientFamilyMemberBinding?.edtAddpatientFname?.text?.toString())) {
            Toast.makeText(activity, "Please enter patient first name", Toast.LENGTH_SHORT).show()
            return false
        }
        if (TextUtils.isEmpty(fragmentEditPatientFamilyMemberBinding?.edtAddpatientLname?.text?.toString())) {
            Toast.makeText(activity, "Please enter patient last name", Toast.LENGTH_SHORT).show()
            return false
        }

//        if (TextUtils.isEmpty(fragmentEditPatientFamilyMemberBinding?.edtAddpatientEmail?.text?.toString())) {
//            Toast.makeText(activity, "Please enter patient email", Toast.LENGTH_SHORT).show()
//            return false
//        }
//        if (TextUtils.isEmpty(fragmentEditPatientFamilyMemberBinding?.edtAddpatientPhonenumber?.text?.toString())) {
//            Toast.makeText(activity, "Please enter patient phone number", Toast.LENGTH_SHORT).show()
//            return false
//        }

        if (TextUtils.isEmpty(fragmentEditPatientFamilyMemberBinding?.edtAddpatientAge?.text?.toString())) {
            Toast.makeText(activity, "Please enter patient age", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun apieditpatientfamilyApiCall() {
        baseActivity?.showLoading()
        val patient_id = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), patientId)
        val first_name = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            fragmentEditPatientFamilyMemberBinding?.edtAddpatientFname?.text?.toString()!!
        )
        val last_name = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            fragmentEditPatientFamilyMemberBinding?.edtAddpatientLname?.text?.toString()!!
        )
//        val email = RequestBody.create(MediaType.parse("multipart/form-data"), fragmentEditPatientFamilyMemberBinding?.edtAddpatientEmail?.text?.toString())
//        val phone_number = RequestBody.create(MediaType.parse("multipart/form-data"), fragmentEditPatientFamilyMemberBinding?.edtAddpatientPhonenumber?.text?.toString())
        val gender = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), selectedGender)
        val age = RequestBody.create(
            "multipart/form-data".toMediaTypeOrNull(),
            fragmentEditPatientFamilyMemberBinding?.edtAddpatientAge?.text?.toString()!!
        )
//        val status = RequestBody.create(MediaType.parse("multipart/form-data"), fragmentProfileBinding?.layoutProfilePersonal?.txtProfilePersonalStatus?.text?.toString())
//        val status = RequestBody.create(MediaType.parse("multipart/form-data"),"1")
        if (imageFile != null) {
            val image = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), imageFile!!)
            var multipartBody = MultipartBody.Part.createFormData("image", imageFile?.name, image)
//            fragmentProfileViewModel?.apieditpatientprofilepersonal(userId,first_name,last_name,id_number,status,multipartBody)email,phone_number,
            fragmentEditPatientFamilyMemberViewModel?.apieditpatientfamily(
                patient_id,
                first_name,
                last_name,
                multipartBody,
                gender,
                age
            )

        } else {
            val image = RequestBody.create("multipart/form-data".toMediaTypeOrNull(), patientimage)
            var multipartBody = MultipartBody.Part.createFormData("image", "", image)
//            fragmentProfileViewModel?.apieditpatientprofilepersonal(userId,first_name,last_name,id_number,status,multipartBody)email,phone_number,
            fragmentEditPatientFamilyMemberViewModel?.apieditpatientfamily(
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

    override fun successEditFamilyMemberResponse(editFamilyMemberResponse: EditFamilyMemberResponse?) {
        baseActivity?.hideLoading()
        if (editFamilyMemberResponse?.code.equals("200")) {
            Toast.makeText(activity, editFamilyMemberResponse?.message, Toast.LENGTH_SHORT).show()
            (activity as HomeActivity).checkInBackstack(
                FragmentNursesBookingAppointment.newInstance(nurseId)
            )

        } else {
            Toast.makeText(activity, editFamilyMemberResponse?.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun errorEditFamilyMemberResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(HomeFragment.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }


    ///New Image Upload Section


    //image upload*********************************************************************************************************************************************
    private fun checkAndRequestPermissionsTest(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            val permissionText = " "
            val permissioncamera = ContextCompat.checkSelfPermission(
                this!!.activity!!,
                Manifest.permission.CAMERA
            )
            val permissionwriteexternalstorage = ContextCompat.checkSelfPermission(
                this!!.activity!!,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
            val listPermissionsNeeded: MutableList<String> =
                ArrayList()
            if (permissioncamera != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.CAMERA)
            }
            if (permissionwriteexternalstorage != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
            if (!listPermissionsNeeded.isEmpty()) {
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
            if (options[item] == "Take Photo") {
                val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(intent, REQUEST_CAMERA)
            } else if (options[item] == "Choose from Gallery") {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT //
                startActivityForResult(Intent.createChooser(intent, "Select File"), SELECT_FILE)
            } else if (options[item] == "Cancel") {
                dialog.dismiss()
            }
        }
        builder.show()
    }


    private fun OpenPictureEditActivity() {
        if (!TextUtils.isEmpty(imageFile?.getPath()) && File(imageFile?.getPath())
                .exists()
        ) {
            CropImage.activity(Uri.fromFile(File(imageFile?.getPath())))
                .setCropShape(CropImageView.CropShape.OVAL)
                .setInitialCropWindowPaddingRatio(0F)
                .setAspectRatio(1, 1)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this!!.activity!!)
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
        OpenPictureEditActivity()

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
        OpenPictureEditActivity()
        /*     im_upload.setImageBitmap(thumbnail);
        im_editbutton.setVisibility(View.GONE);
        im_holder.setVisibility(View.GONE);*/
    }


    fun goToImageIntent() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }


}