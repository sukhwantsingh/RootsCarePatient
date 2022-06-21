package com.rootscare.ui.babysitter.babysitterseealllistingUpdate

import com.rootscare.data.model.api.response.caregiverallresponse.allcaregiverlistingresponse.AllCaregiverListingResponse
import com.rootscare.data.model.api.response.caregiverallresponse.caregiverlist.GetCaregiverListResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse
import com.rootscare.data.model.api.response.nurses.nurselist.GetNurseListResponse

interface FragmentBabySitterSeeAllListingByGridNavigator {

    fun successDoctorDepartmentListingResponse(doctorDepartmentListingResponse: DoctorDepartmentListingResponse?)
    fun successAllCaregiverListingResponse(caregiverListingResponse: AllCaregiverListingResponse?)
    fun errorGetNurseListResponse(throwable: Throwable?)

    fun errorCaregiverDepartmentListingResponse(throwable: Throwable?)
}