package com.rootscare.ui.supportmore

import android.util.Log
import com.google.gson.Gson
import com.rootscare.ui.base.BaseViewModel
import com.rootscare.ui.utilitycommon.NeedSupportRequest
import okhttp3.RequestBody

class CommonViewModel : BaseViewModel<CommonActivityNavigator>() {

    fun changeLanguage(reqBody: RequestBody?) {
        val disposable = apiServiceWithGsonFactory.apiChangelanguage(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    Log.wtf("check_response", ": " + Gson().toJson(response))
                    navigator.onSuccessSubmitSupport(response)
                } else {
                    Log.wtf("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorInApi(throwable)
                    Log.wtf("check_response_error", ": " + throwable.message)
                }
            })
        compositeDisposable.add(disposable)

    }

    fun submitSupportApi(reqBody: NeedSupportRequest) {
        val disposable = apiServiceWithGsonFactory.apiToSubmitNeedSupport(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    Log.wtf("check_response", ": " + Gson().toJson(response))
                    navigator.onSuccessSubmitSupport(response)
                } else {
                    Log.wtf("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorInApi(throwable)
                    Log.wtf("check_response_error", ": " + throwable.message)
                }
            })
        compositeDisposable.add(disposable)

    }

    fun fetchHelpTopics(reqBody: RequestBody?) {
        val disposable = apiServiceWithGsonFactory.getHelpTopics(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.onFetchHelpTopics(response)
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