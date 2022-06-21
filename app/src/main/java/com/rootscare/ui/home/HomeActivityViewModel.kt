package com.rootscare.ui.home

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.appointmentrequest.AppointmentRequest
import com.rootscare.ui.base.BaseViewModel
import okhttp3.RequestBody

class HomeActivityViewModel : BaseViewModel<HomeActivityNavigator>() {

    fun apiLogout(appointmentRequest: AppointmentRequest) {
        val disposable = apiServiceWithGsonFactory.apiLogoutUser(appointmentRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.successLogoutResponse(response)
                 } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorLogoutResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }
    fun apiUpdateCurrentLocation(reqBody: RequestBody) {
        val disposable = apiServiceWithGsonFactory.apiUpdateCurrentLocation(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.onSuccessUpdateLocation(response)
                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorLogoutResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })
        compositeDisposable.add(disposable)
    }

    fun apiUnreadNotifications(reqBody: RequestBody?) {
        val disposable = apiServiceWithGsonFactory.apiNotificationUnreadCounts(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.onSuccessUnreadNotification(response)
                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorLogoutResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })
        compositeDisposable.add(disposable)
    }

}