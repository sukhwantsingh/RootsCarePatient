package com.rootscare.ui.nurses.addpatient

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.addpatientresponse.AddPatientResponse

interface FragmentNurseAddPatientNavigator {
    fun successAddPatientResponse(addPatientResponse: AddPatientResponse?)
    fun errorAddPatientResponse(throwable: Throwable?)
}