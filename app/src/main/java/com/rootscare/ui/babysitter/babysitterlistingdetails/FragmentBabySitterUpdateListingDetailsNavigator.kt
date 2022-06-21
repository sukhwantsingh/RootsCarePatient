package com.rootscare.ui.babysitter.babysitterlistingdetails

import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse
import com.rootscare.data.model.api.response.nurses.nursedetails.NurseDetailsResponse
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.NueseViewTimingsResponse

interface FragmentBabySitterUpdateListingDetailsNavigator {
    fun successNurseDetailsResponse(nurseDetailsResponse: NurseDetailsResponse?)
    fun successNueseViewTimingsResponse(nueseViewTimingsResponse: NueseViewTimingsResponse?)
    fun errorNurseDetailsResponse(throwable: Throwable?)
}