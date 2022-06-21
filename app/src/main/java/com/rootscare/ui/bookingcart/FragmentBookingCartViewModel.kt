package com.rootscare.ui.bookingcart

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.cartitemdeleterequest.CartItemDeleteRequest
import com.rootscare.data.model.api.request.doctorrequest.bookingcartrequests.BookingCartRequest
import com.rootscare.ui.base.BaseViewModel
import okhttp3.RequestBody

class FragmentBookingCartViewModel : BaseViewModel<FragmentBookingCartNavigator>() {

    fun apiPatientCart(bookingCartRequest: BookingCartRequest) {
        val disposable = apiServiceWithGsonFactory.apipatientcart(bookingCartRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.successBookingCartResponse(response)
                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorBookingCartResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apiDeletePatientCart(cartItemDeleteRequest: CartItemDeleteRequest) {
        val disposable = apiServiceWithGsonFactory.apideletepatientcart(cartItemDeleteRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successDoctorCartItemDeleteResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result != null) {
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorBookingCartResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun apiAfterPaymentHit(reqBody: RequestBody?) {
        val disposable = apiServiceWithGsonFactory.apiAfterPaymentSuccess(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                navigator.onAfterPaymentSuccess(response)
                }
            }, { throwable ->
                run {
                    navigator.errorBookingCartResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }
}