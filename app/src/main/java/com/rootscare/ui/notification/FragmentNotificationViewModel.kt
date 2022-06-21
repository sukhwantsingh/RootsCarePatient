package com.rootscare.ui.notification

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.CommonNotificationIdRequest
import com.rootscare.data.model.api.request.CommonUserIdRequest
import com.rootscare.ui.base.BaseViewModel

class FragmentNotificationViewModel : BaseViewModel<FragmentNotificationNavigator>() {
    fun apiGetAllUserNotifications(commonUserIdRequest: CommonUserIdRequest) {
        val disposable = apiServiceWithGsonFactory.apiUserNotification(commonUserIdRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successNotificationListResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result != null) {
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorInApi(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }
    fun apiUpdateRead(commonUserIdRequest: CommonNotificationIdRequest) {
        val disposable = apiServiceWithGsonFactory.apiUpdateNotification(commonUserIdRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.successUpdateRead(response)
                }
            }, { throwable ->
                run {
                    navigator.errorInApi(throwable)
                }
            })

        compositeDisposable.add(disposable)
    }
}