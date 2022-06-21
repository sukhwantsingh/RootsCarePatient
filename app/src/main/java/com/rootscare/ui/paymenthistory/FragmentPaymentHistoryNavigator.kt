package com.rootscare.ui.paymenthistory

import com.rootscare.data.model.api.response.paymenthistoryresponse.PatientPaymentHistoryResponse

interface FragmentPaymentHistoryNavigator {
    fun successPatientPaymentHistoryResponse(patientPaymentHistoryResponse: PatientPaymentHistoryResponse?)
    fun errorPatientPaymentHistoryResponse(throwable: Throwable?)
}