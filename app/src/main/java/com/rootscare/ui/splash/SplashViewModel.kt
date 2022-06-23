package com.rootscare.ui.splash

import com.rootscare.ui.base.BaseViewModel

class SplashViewModel : BaseViewModel<SplashNavigator>() {

    fun apiVersionCheck() {
        val disposable = apiServiceWithGsonFactory.apiVersionCheck()
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