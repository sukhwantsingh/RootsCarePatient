package com.rootscare.ui.login.subfragment.registrationfragment

import com.model.ModelServiceFor
import com.rootscare.data.model.api.response.registrationresponse.registrationcheckotpresponse.RegistrationCheckOTPResponse
import com.rootscare.data.model.api.response.registrationresponse.registrationsendotpresponse.RegistrationSendOTPResponse

interface FragmentRegistrationNavigator {
    fun successRegistrationSendOTPResponse(registrationSendOTPResponse: RegistrationSendOTPResponse?)
    fun onSuccessServiceFor(specialityList: ModelServiceFor?){}
    fun errorRegistrationSendOTPResponse(throwable: Throwable?)

}