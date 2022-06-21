package com.rootscare.ui.login.subfragment.registrationotp

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.registrationrequest.registrationotpcheckrequest.RegistrationCheckOTPRequest
import com.rootscare.ui.base.BaseViewModel

class FragmentRegistrationOtpCheckViewModel: BaseViewModel<FragmentRegistrationOtpCheckNavigator>() {

    fun apipatientregistrationotpcheck(registrationCheckOTPRequest: RegistrationCheckOTPRequest) {
        val disposable = apiServiceWithGsonFactory.apipatientregistrationotpcheck(registrationCheckOTPRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    // Store last login time
                    Log.d("check_response", ": " + Gson().toJson(response))
                    navigator.successRegistrationCheckOTPResponse(response)
                    /* Saving access token after singup or login */
                    if (response.result!= null){
                    }

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorRegistrationCheckOTPResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }
}