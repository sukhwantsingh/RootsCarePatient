package com.rootscare.ui.profile

import com.model.ModelServiceFor
import com.rootscare.data.model.api.response.nationalityresponse.NationalityResponse
import com.rootscare.data.model.api.response.patientprofileresponse.PatientProfileResponse

interface FragmentProfileNavigator {
    fun successPatientProfileResponse(response: PatientProfileResponse?)
    fun successNationalityResponse(response: NationalityResponse?)
    fun onSuccessServiceFor(specialityList: ModelServiceFor?){}
    fun errorPatientProfileResponse(throwable: Throwable?)
}