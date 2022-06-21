package com.rootscare.ui.login.subfragment.loginfragment

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.loginrequest.LoginRequest
import com.rootscare.ui.base.BaseViewModel

class FragmentLoginViewModel : BaseViewModel<FragmentLoginNavigator>() {
    fun apiPatientLogin(loginRequest: LoginRequest) {
        val disposable = apiServiceWithGsonFactory.apipatientlogin(loginRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    navigator.successLoginResponse(response)
                }
            }, { throwable ->
                run {
                    navigator.errorLoginResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }
}