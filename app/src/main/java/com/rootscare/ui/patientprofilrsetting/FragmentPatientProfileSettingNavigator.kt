package com.rootscare.ui.patientprofilrsetting

import com.rootscare.data.model.api.response.deactivateaccountresponse.DeactivateAccountResponse

interface FragmentPatientProfileSettingNavigator {
    fun successDeactivateAccountResponse(deactivateAccountResponse: DeactivateAccountResponse?)
    fun errorDeactivateAccountResponse(throwable: Throwable?)
}