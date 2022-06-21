package com.rootscare.ui.notification

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.dialog.CommonDialog
import com.interfaces.DialogClickCallback
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.CommonNotificationIdRequest
import com.rootscare.data.model.api.request.CommonUserIdRequest
import com.rootscare.databinding.FragmentNotificationBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.notification.adapter.AdapterHospitalManageNotificationRecyclerview
import com.rootscare.ui.notification.adapter.OnNotificationCallback
import com.rootscare.ui.notification.models.ModelNotificationResponse
import com.rootscare.ui.notification.models.ModelUpdateRead
import com.rootscare.utilitycommon.SUCCESS_CODE

class FragmentNotification :
    BaseFragment<FragmentNotificationBinding, FragmentNotificationViewModel>(),
    FragmentNotificationNavigator {
    private var fragmentNotificationBinding: FragmentNotificationBinding? = null
    private var mViewModel: FragmentNotificationViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_notification
    override val viewModel: FragmentNotificationViewModel
        get() {
            mViewModel =
                ViewModelProviders.of(this).get(FragmentNotificationViewModel::class.java)
            return mViewModel as FragmentNotificationViewModel
        }
    var mAdapterNotification: AdapterHospitalManageNotificationRecyclerview? = null
    var clickedPos = -1
    companion object {
        fun newInstance(): FragmentNotification {
            val args = Bundle()
            val fragment = FragmentNotification()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel?.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentNotificationBinding = viewDataBinding
       fetchNotification()
    }

    private fun fetchNotification() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val commonUserIdRequest = CommonUserIdRequest()
            commonUserIdRequest.id = mViewModel?.appSharedPref?.userId
            mViewModel?.apiGetAllUserNotifications(commonUserIdRequest)

        } else {
          showToast(getString(R.string.check_network_connection))
        }

    }
    fun updateReadNotiApi(id_:String){
        if (isNetworkConnected) {
            // baseActivity?.showLoading()
            val commonUserIdRequest = CommonNotificationIdRequest()
            commonUserIdRequest.id = id_
            commonUserIdRequest.read ="1"
            mViewModel?.apiUpdateRead(commonUserIdRequest)

        } else {
            showToast(getString(R.string.network_unavailable))
        }
    }

    // Set up recycler view for service listing if available
    private fun setUpPaymentHistoryListingRecyclerview(notificationItemResult: ArrayList<ModelNotificationResponse.Result?>?) {
        mAdapterNotification= AdapterHospitalManageNotificationRecyclerview(notificationItemResult, requireContext())
        fragmentNotificationBinding?.rvNotification?.adapter = mAdapterNotification
        mAdapterNotification?.mCalllback = object : OnNotificationCallback {
            override fun onNotificationClick(node: ModelNotificationResponse.Result?, pos:Int) {
                clickedPos = pos
                CommonDialog.showDialogForSingleButton(requireActivity(), object :
                    DialogClickCallback {
                }, getString(R.string.notification), node?.body?:"")
                if(!node?.read.equals("1",ignoreCase = true)) updateReadNotiApi(node?.id?:"")
            }
        }
    }

    override fun successNotificationListResponse(notificationResponse: ModelNotificationResponse?) {
        baseActivity?.hideLoading()
        if (notificationResponse?.code.equals(SUCCESS_CODE)) {
            if (notificationResponse?.result != null && notificationResponse.result.isNotEmpty()) {
                setUpPaymentHistoryListingRecyclerview(notificationResponse.result)
            } else {
                fragmentNotificationBinding?.rvNotification?.visibility =  View.GONE
                fragmentNotificationBinding?.tvNoDate?.visibility = View.VISIBLE
                fragmentNotificationBinding?.tvNoDate?.text = notificationResponse?.message
            }

        } else {
            fragmentNotificationBinding?.rvNotification?.visibility =  View.GONE
            fragmentNotificationBinding?.tvNoDate?.visibility = View.VISIBLE
            fragmentNotificationBinding?.tvNoDate?.text = notificationResponse?.message
        }
    }

    override fun successUpdateRead(response: ModelUpdateRead?) {
        baseActivity?.hideLoading()
        if (response?.code.equals(SUCCESS_CODE)) {
            HomeActivity.isNotificationChecked.value = true
            if(clickedPos!= -1) mAdapterNotification?.updatedToRead(clickedPos)
        }
    }


    override fun errorInApi(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            showToast(throwable.message ?: getString(R.string.something_went_wrong))
        }
    }


}