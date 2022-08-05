package com.rootscare.ui.splash

import com.rootscare.ui.base.BaseViewModel
import okhttp3.RequestBody

class SplashViewModel : BaseViewModel<SplashNavigator>() {

    fun apiVersionCheck(reqBody: String?) {
        val disposable = apiServiceWithGsonFactory.apiVersionCheck(reqBody)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.onSuccessVersion(response)
                }
            }, { throwable ->
                run { navigator.errorInApi(throwable) }
            })

        compositeDisposable.add(disposable)
    }
}