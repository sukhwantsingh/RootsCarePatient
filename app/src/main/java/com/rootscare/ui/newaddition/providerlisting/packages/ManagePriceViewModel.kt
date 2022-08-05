package com.rootscare.serviceprovider.ui.pricelistss

import android.util.Log
import com.google.gson.Gson
import com.rootscare.ui.base.BaseViewModel
import okhttp3.RequestBody

class ManagePriceViewModel : BaseViewModel<ManagePriceNavigator>() {

    fun getLabPackagesList(requestUserRegister: RequestBody) {
        val disposable = apiServiceWithGsonFactory.getLabPackagesApi(requestUserRegister)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.onSuccessAfterGetTaskPrice(response)
                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.onThrowable(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

    fun getLabPackagesDetails(requestUserRegister: RequestBody) {
        val disposable = apiServiceWithGsonFactory.getLabPackagesDetails(requestUserRegister)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.onSuccessPackageDetails(response)
                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.onThrowable(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }

}