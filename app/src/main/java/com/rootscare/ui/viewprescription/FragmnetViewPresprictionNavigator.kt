package com.rootscare.ui.viewprescription

import com.rootscare.data.model.api.response.patientprescription.PatientPrescriptionResponse

interface FragmnetViewPresprictionNavigator {
    fun successPatientPrescriptionResponse(patientPrescriptionResponse: PatientPrescriptionResponse?)
    fun errorPatientPrescriptionResponse(throwable: Throwable?)
}