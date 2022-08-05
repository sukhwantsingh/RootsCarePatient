package com.rootscare.ui.newaddition.appointments

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.rootscare.data.model.api.request.videoPushRequest.VideoPushRequest
import com.rootscare.ui.base.BaseViewModel
import okhttp3.RequestBody

class ViewModelMyAppointments : BaseViewModel<AppointmentNavigator>() {

    fun apiBookingTimeSlotsForLab(reqBody: RequestBody) {
        val disposable = apiServiceWithGsonFactory.apiBookingTimeSlotsForLab(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.onSuccessBookingTimeSlots(response)
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

    fun apiBookingTimeSlots(reqBody: RequestBody) {
        val disposable = apiServiceWithGsonFactory.apiBookingTimeSlots(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.onSuccessBookingTimeSlots(response)
                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorInRecheduleApi(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apiBookingTimeSlotsForDoc(reqBody: RequestBody) {
        val disposable = apiServiceWithGsonFactory.apiBookingTimeSlotsForDoctor(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.onSuccessBookingTimeSlots(response)
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

    fun apiOngoing(reqBody: RequestBody) {
        val disposable = apiServiceWithGsonFactory.apiAppointmentOngoing(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.onSuccessResponse(response)
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

    fun apiUpcoming(reqBody: RequestBody) {
        val disposable = apiServiceWithGsonFactory.apiAppointmentUpcoming(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.onSuccessResponse(response)
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

    fun apiPast(reqBody: RequestBody) {
        val disposable = apiServiceWithGsonFactory.apiAppointmentPast(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.onSuccessResponse(response)
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

    fun apiALl(reqBody: RequestBody) {
        val disposable = apiServiceWithGsonFactory.apiAppointmentAll(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.onSuccessResponse(response)
                }
            }, { throwable ->
                run {
                    navigator.errorInApi(throwable)

                }
            })
        compositeDisposable.add(disposable)
    }

    fun apiReschedule(reqBody: RequestBody, position: Int) {
        val disposable = apiServiceWithGsonFactory.apiReschedule(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.onRescheduleDetails(response, position)

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorInRecheduleApi(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })
        compositeDisposable.add(disposable)
    }

    fun apiRescheduleForLab(reqBody: RequestBody, position: Int) {
        val disposable = apiServiceWithGsonFactory.apiRescheduleForLab(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.onRescheduleDetails(response, position)
                }
            }, { throwable ->
                run {
                    navigator.errorInRecheduleApi(throwable)
                }
            })
        compositeDisposable.add(disposable)
    }

    fun apiUpdateReschedule(reqBody: RequestBody) {
        val disposable = apiServiceWithGsonFactory.apiUpdateReschedule(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.onUpdateReschedule(response)
                }
            }, { throwable ->
                run {
                    navigator.errorInRecheduleApi(throwable)
                }
            })
        compositeDisposable.add(disposable)
    }

    fun apiRescheduleForDoc(reqBody: RequestBody, position: Int) {
        val disposable = apiServiceWithGsonFactory.apiRescheduleForDoc(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.onRescheduleDetails(response, position)

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorInRecheduleApi(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })
        compositeDisposable.add(disposable)
    }

    fun apiUpdateRescheduleForLab(reqBody: RequestBody) {
        val disposable = apiServiceWithGsonFactory.apiUpdateRescheduleForLab(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.onUpdateReschedule(response)
                }
            }, { throwable ->
                run {
                    navigator.errorInRecheduleApi(throwable)
                }
            })
        compositeDisposable.add(disposable)
    }

    fun apiUpdateRescheduleForDoc(reqBody: RequestBody) {
        val disposable = apiServiceWithGsonFactory.apiUpdateRescheduleForDoc(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.onUpdateReschedule(response)
                }
            }, { throwable ->
                run {
                    navigator.errorInRecheduleApi(throwable)
                }
            })
        compositeDisposable.add(disposable)
    }

    fun apiAppointmentDetails(reqBody: RequestBody) {
        val disposable = apiServiceWithGsonFactory.getAppointmentDetails(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    //   Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.onAppointmentDetail(response)
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

    fun apiSendVideoPushNotification(videoPushRequest: VideoPushRequest) {
        val disposable = apiServiceWithGsonFactory.sendVideoPushNotification(videoPushRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.successVideoPushResponse(response)
                }
            }, { throwable ->
                run {
                    navigator.errorInApi(throwable)
                }
            })

        compositeDisposable.add(disposable)
    }



    fun apiInserRating(reqBody: RequestBody) {
        val disposable = apiServiceWithGsonFactory.apiInserRating(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    //   Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.onSuccessRating(response)
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

}