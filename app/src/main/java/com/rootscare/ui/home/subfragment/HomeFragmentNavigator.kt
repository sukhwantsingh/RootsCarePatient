package com.rootscare.ui.home.subfragment

import com.rootscare.data.model.api.response.patienthome.PatientHomeApiResponse

interface HomeFragmentNavigator {
    fun successPatientHomeApiResponse(patientHomeApiResponse: PatientHomeApiResponse?)
    fun errorPatientHomeApiResponse(throwable: Throwable?)
}