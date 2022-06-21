package com.rootscare.ui.myupcomingappointment

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.appointmentrequest.AppointmentRequest
import com.rootscare.data.model.api.request.cancelappointmentrequest.CancelAppointmentRequest
import com.rootscare.data.model.api.request.pushNotificationRequest.PushNotificationRequest
import com.rootscare.data.model.api.request.videoPushRequest.VideoPushRequest
import com.rootscare.ui.base.BaseViewModel

class FragmentMyUpCommingAppointmentViewModel :
    BaseViewModel<FragmentMyUpCommingAppointmentnavigator>() {
    fun apiPatientUpcomingAppointment(appointmentRequest: AppointmentRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apipatientupcomingappointment(appointmentRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successAppointmentHistoryResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result != null) {
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorAppointmentHistoryResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }


    fun apiCancelAppointment(cancelAppointmentRequest: CancelAppointmentRequest, userId: String?) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.apicancelappointment(cancelAppointmentRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successAppointmentCancelResponse(
                        response, userId, cancelAppointmentRequest.serviceType
                    )
                    /* Saving access token after singup or login */
                    if (response.result != null) {
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorAppointmentHistoryResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apiSendVideoPushNotification(videoPushRequest: VideoPushRequest) {
//        val body = RequestBody.create(MediaType.parse("application/json"), "")
        val disposable = apiServiceWithGsonFactory.sendVideoPushNotification(videoPushRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successVideoPushResponse(response)
                    /* Saving access token after sign up or login */
//                    if (response.result != null) {
//                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorVideoPushResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apiSendPush(pushNotificationRequest: PushNotificationRequest) {
        val disposable = apiServiceWithGsonFactory.sendPushNotification(pushNotificationRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successPushNotification(response)

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorAppointmentHistoryResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }
}