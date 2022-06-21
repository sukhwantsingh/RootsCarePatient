package com.rootscare.ui.physiotherapy.physiotherapylistingdetails

import com.rootscare.data.model.api.response.nurses.nursedetails.NurseDetailsResponse
import com.rootscare.data.model.api.response.nurses.taskresponse.GetTaskResponse

interface FragmentPhysiotherapyListingDetailsNavigator {
    fun successNurseDetailsResponse(nurseDetailsResponse: NurseDetailsResponse?)
    fun successNurseViewTimingsResponse(getTaskResponse: GetTaskResponse?)
    fun errorNurseDetailsResponse(throwable: Throwable?)
}