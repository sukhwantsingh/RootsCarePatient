package com.rootscare.ui.splash

import com.rootscare.ui.splash.model.NetworkAppCheck

interface SplashNavigator {
    fun onSuccessVersion(response: NetworkAppCheck?)
    fun errorInApi(throwable: Throwable?)
}