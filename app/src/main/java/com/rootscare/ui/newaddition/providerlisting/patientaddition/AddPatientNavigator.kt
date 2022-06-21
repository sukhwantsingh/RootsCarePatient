package com.rootscare.ui.newaddition.providerlisting.patientaddition
import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.addpatientresponse.AddPatientResponse

interface AddPatientNavigator {
    fun successAddPatientResponse(addPatientResponse: AddPatientResponse?)
    fun errorAddPatientResponse(throwable: Throwable?)
}