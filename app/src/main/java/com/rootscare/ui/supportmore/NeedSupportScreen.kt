package com.rootscare.ui.supportmore

import android.os.Bundle
import android.widget.PopupMenu
import androidx.lifecycle.ViewModelProviders
import com.google.gson.JsonObject
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.databinding.ActivityNeedSupportScreenBinding
import com.rootscare.ui.base.BaseActivity
import com.rootscare.ui.supportmore.bottomsheet.OnBottomSheetCallback
import com.rootscare.ui.supportmore.bottomsheet.DialogThankyou
import com.rootscare.ui.supportmore.models.ModelIssueTypes
import com.rootscare.ui.utilitycommon.NeedSupportRequest
import com.rootscare.utilitycommon.SUCCESS_CODE
import com.rootscare.utilitycommon.dialogDropdown
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

class NeedSupportScreen : BaseActivity<ActivityNeedSupportScreenBinding, CommonViewModel>(),
    CommonActivityNavigator,
    OnBottomSheetCallback {

    private var binding: ActivityNeedSupportScreenBinding? = null
    private var mViewModel: CommonViewModel? = null

    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_need_support_screen
    override val viewModel: CommonViewModel
        get() {
            mViewModel = ViewModelProviders.of(this).get(CommonViewModel::class.java)
            return mViewModel as CommonViewModel
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel?.navigator = this
        binding = viewDataBinding
        binding?.topToolbar?.run {
            tvHeader.text = getString(R.string.support_amp_more)
            btnBack.setOnClickListener { finish() }
        }
        initViews()
        callingApi()
    }

    private fun callingApi() {
        if (isNetworkConnected) {
            val jsonObject = JsonObject().apply {
                addProperty("login_user_id", mViewModel?.appSharedPref?.userId)
            }

            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            hideKeyboard()
            showLoading()
            mViewModel?.fetchHelpTopics(body)
        } else {
            showToast(getString(R.string.check_network_connection))
        }

    }

    private fun initViews() {
        binding?.btnSubmit?.setOnClickListener { submitApi() }
        binding?.tvSelectTopic?.setOnClickListener { showIssueList() }
    }

    override fun onCloseBottomSheet() {
        super.onCloseBottomSheet()
        finish()
    }


    private fun submitApi() {
        hideKeyboard()
        val issueType = binding?.tvSelectTopic?.text?.toString() ?: ""
        val description = binding?.edtDescription?.text?.toString() ?: ""

        if (issueType.isBlank()) {
            showToast(getString(R.string.please_select_topic))
            return
        }

        // call api
        showLoading()
        val reqBody = NeedSupportRequest(mViewModel?.appSharedPref?.userId, "patient", issueType,  description)
       mViewModel?.submitSupportApi(reqBody)
    }


    override fun onSuccessSubmitSupport(response: CommonResponse) {
        hideLoading()
        if (response.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            val mDialog = DialogThankyou.newInstance(this@NeedSupportScreen)
            mDialog.show(supportFragmentManager, "ThankyouDialog")
        } else {
            showToast(response.message ?: "")
        }

    }

    override fun onFetchHelpTopics(response: ModelIssueTypes?) {
        super.onFetchHelpTopics(response)
        hideLoading()
        if (response?.code.equals(SUCCESS_CODE, ignoreCase = true)) {
            setupTopicPopup(response?.result)
        } else {
            showToast(response?.message ?: "")
        }
    }

 //   var popupTopics: PopupMenu? = null
    private val topicList : ArrayList<String?> = ArrayList()
    private fun setupTopicPopup(mListData: ArrayList<ModelIssueTypes.Result?>?) {
         mListData?.forEach { topicList.add(it?.name) }
//        popupTopics = PopupMenu(this, binding?.tvSelectTopic).apply {
//            mListData?.forEach { menu.add(it?.name) }
//            setOnMenuItemClickListener { item ->
//                binding?.tvSelectTopic?.text = item.title?.toString() ?: ""
//                true
//            }
//        }
    }

  //  private fun showIssueList() = popupTopics?.show()
    private fun showIssueList() {
      dialogDropdown(getString(R.string.select_a_topic), topicList, binding?.tvSelectTopic)
    }
    override fun errorInApi(throwable: Throwable?) {
        hideLoading()
        showToast(throwable?.message ?: "")
    }

    override fun reloadPageAfterConnectedToInternet() {

    }


}


/*
// 2nd phase
private var docFile: File? = null
    private var dialogFPD: FilePickerDialog? = null
    private fun openFilePicker() {
        try {
            val dialogProperties = DialogProperties().apply {
                selection_mode = DialogConfigs.SINGLE_MODE
                selection_type = DialogConfigs.FILE_SELECT
                root = File(DialogConfigs.DEFAULT_DIR)
                error_dir = File(DialogConfigs.DEFAULT_DIR)
                offset = File(DialogConfigs.DEFAULT_DIR)
                extensions = arrayOf(".pdf", ".jpg", ".png", ".jpeg", ".mp4", ".bmp")
                show_hidden_files = false
            }

            dialogFPD = FilePickerDialog(this, dialogProperties)
            dialogFPD?.setTitle("Select a File")

            // callback
            dialogFPD?.setDialogSelectionListener { fileSelected ->
                for (path in fileSelected) {
                    val file = File(path)
                    docFile = file
                    Log.wtf("files_", "name: ${file.name}")
                    Log.wtf("files_", "absPath: ${file.absolutePath}")

                    binding?.tvNoattachement?.text = file.name
                }
            }

            dialogFPD?.show()
        } catch (e: Exception) {
            println(e)
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            FilePickerDialog.EXTERNAL_READ_PERMISSION_GRANT -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    dialogFPD?.show()
                } else {
                    //Permission has not been granted. Notify the user.
                    Toast.makeText(this@NeedSupportScreen, "Permission is Required for selecting file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
//        showLoading()
//        hideKeyboard()
//        val issueType = (binding?.tvSelectTopic?.text?.toString() ?: "").toRequestBody("multipart/form-data".toMediaTypeOrNull())
//        val description = (binding?.edtDescription?.text?.toString() ?: "").toRequestBody("multipart/form-data".toMediaTypeOrNull())
//        var fileMultipartDoc: MultipartBody.Part? = null
//        val docReqBody: RequestBody?
//        docFile?.let {
//            docReqBody = it.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//            fileMultipartDoc = MultipartBody.Part.createFormData("docFile", docFile?.name?:"", docReqBody)
//        }

*/














