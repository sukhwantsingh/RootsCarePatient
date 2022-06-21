package com.rootscare.ui.login.subfragment.registrationotp

import com.rootscare.data.model.api.response.registrationresponse.registrationcheckotpresponse.RegistrationCheckOTPResponse

interface FragmentRegistrationOtpCheckNavigator {
    fun successRegistrationCheckOTPResponse(registrationCheckOTPResponse: RegistrationCheckOTPResponse?)
    fun errorRegistrationCheckOTPResponse(throwable: Throwable?)
}