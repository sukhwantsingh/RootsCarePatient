package com.rootscare.ui.nurses.nurseslistingdetails

import com.rootscare.data.model.api.response.nurses.nursedetails.NurseDetailsResponse
import com.rootscare.data.model.api.response.nurses.nurseviewtiming.NueseViewTimingsResponse

interface FragmentNursesListingDetailsNavigator {
    fun successNurseDetailsResponse(nurseDetailsResponse: NurseDetailsResponse?)
    fun successNueseViewTimingsResponse(nueseViewTimingsResponse: NueseViewTimingsResponse?)
    fun errorNurseDetailsResponse(throwable: Throwable?)
}