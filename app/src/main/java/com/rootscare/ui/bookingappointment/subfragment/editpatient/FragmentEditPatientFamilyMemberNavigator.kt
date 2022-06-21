package com.rootscare.ui.bookingappointment.subfragment.editpatient

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.addpatientresponse.AddPatientResponse
import com.rootscare.data.model.api.response.editpatientfamilymemberresponse.EditFamilyMemberResponse

interface FragmentEditPatientFamilyMemberNavigator {
    fun successEditFamilyMemberResponse(editFamilyMemberResponse: EditFamilyMemberResponse?)
    fun errorEditFamilyMemberResponse(throwable: Throwable?)
}