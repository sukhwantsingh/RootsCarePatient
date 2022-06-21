package com.rootscare.ui.newaddition.providerlisting

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.rootscare.data.model.api.request.deletepatientfamilymemberrequest.DeletePatientFamilyMemberRequest
import com.rootscare.data.model.api.request.doctorrequest.getpatientfamilymemberrequest.GetPatientFamilyMemberRequest
import com.rootscare.ui.base.BaseViewModel
import io.reactivex.disposables.Disposable
import okhttp3.MultipartBody
import okhttp3.RequestBody

class ProviderListingViewModel : BaseViewModel<ProviderListingNavigator>() {

    val mlFamilyMemberId  = MutableLiveData("")

    fun apiProviderList(reqBody: RequestBody?) {
        val disposable = apiServiceWithGsonFactory.apiProviderListing(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.onSuccessProviderListing(response)
                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorInAPi(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }
    fun apiProviderDetail(reqBody: RequestBody?) {
        val disposable = apiServiceWithGsonFactory.apiProviderDetails(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.onSuccessProviderDetails(response)
                    /* Saving access token after singup or login */
                    if (response.result!= null){
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorInAPi(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }
    fun apipatientfamilymember(getPatientFamilyMemberRequest: GetPatientFamilyMemberRequest) {
        val disposable = apiServiceWithGsonFactory.apipatientfamilymember(getPatientFamilyMemberRequest)
                .subscribeOn(_scheduler_io)
                .observeOn(_scheduler_ui)
                .subscribe({ response ->
                    if (response != null) {
                        // Store last login time
                        Log.d("check_response", ": " + Gson().toJson(response))
                        navigator.successGetPatientFamilyListResponse(response)
                        /* Saving access token after singup or login */
                    } else {
                        Log.d("check_response", ": null response")
                    }
                }, { throwable ->
                    run {
                        navigator.errorInAPi(throwable)
                        Log.d("check_response_error", ": " + throwable.message)
                    }
                })

        compositeDisposable.add(disposable)
    }

    fun apiBookingInitialDetails(reqBody: RequestBody?) {
        val disposable = apiServiceWithGsonFactory.apiBookingInitialData(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.onSuccessInitialData(response)

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorInAPi(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apiBookingInitialDetailsForDoc(reqBody: RequestBody?) {
        val disposable = apiServiceWithGsonFactory.apiBookingInitialDataForDoctor(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.onSuccessInitialData(response)

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorInAPi(throwable)
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
                    navigator.errorInAPi(throwable)
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
                    navigator.errorInAPi(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apiDeleteFamiliyMember(req: DeletePatientFamilyMemberRequest) {
        val disposable =  apiServiceWithGsonFactory.apideletepatientfamilymember(req)
                .subscribeOn(_scheduler_io)
                .observeOn(_scheduler_ui)
                .subscribe({ response ->
                    if (response != null) {
                        navigator.successDeletePatientFamilyListResponse(response)
                      } else {
                        Log.d("check_response", ": null response")
                    }
                }, { throwable ->
                    run {
                        navigator.errorInAPi(throwable)
                    }
                })

        compositeDisposable.add(disposable)
    }

    fun apiBookAppointment(reqBody: RequestBody?) {
        val disposable = apiServiceWithGsonFactory.apiBookAppointment(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.onSuccessBooking(response)
                }
            }, { throwable ->
                run {
                    navigator.errorInAPi(throwable)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apiBookAppointmentforDoctor(
        serviceType: RequestBody?, currency: RequestBody?, loggedId: RequestBody?, familyMembId: RequestBody?, providerId: RequestBody?,
        taskId: RequestBody?,taskType: RequestBody?, appointType: RequestBody?,from_date: RequestBody?,
        from_time: RequestBody?, taskPrice: RequestBody?, taskPriceTotal: RequestBody?,
        vatPrice: RequestBody?,
        vatPercent: RequestBody?,
        dis_fare: RequestBody?,
        subTotalPrice: RequestBody?,
        totalPrice: RequestBody?,
        symptomText: RequestBody?,
        symptom_recording: MultipartBody.Part? = null,
        upload_prescription: MultipartBody.Part? = null
    ) {
       val disposable = apiServiceWithGsonFactory.apiBookAppointmentForDoctor(
           serviceType,currency, loggedId, familyMembId, providerId, taskId,
           taskType, appointType, from_date, from_time, taskPrice, taskPriceTotal,
           vatPrice, vatPercent, dis_fare, subTotalPrice,
           totalPrice, symptomText, symptom_recording, upload_prescription
        )
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.onSuccessBooking(response)
                }
            }, { throwable ->
                run {
                    navigator.errorInAPi(throwable)
                }
            })
        compositeDisposable.add(disposable)
    }
}