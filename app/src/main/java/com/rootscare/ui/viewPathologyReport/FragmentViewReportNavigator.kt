package com.rootscare.ui.viewPathologyReport

import com.rootscare.data.model.api.response.patientReport.PatientReportResponse

interface FragmentViewReportNavigator {
    fun successPatientReportResponse(patientReportResponse: PatientReportResponse?)
    fun errorPatientReportResponse(throwable: Throwable?)
}