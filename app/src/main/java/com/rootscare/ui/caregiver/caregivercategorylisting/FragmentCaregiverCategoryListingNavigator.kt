package com.rootscare.ui.caregiver.caregivercategorylisting

import com.rootscare.data.model.api.response.caregiverallresponse.allcaregiverlistingresponse.AllCaregiverListingResponse
import com.rootscare.data.model.api.response.caregiverallresponse.caregiverlist.GetCaregiverListResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordepartmentlistingresponse.DoctorDepartmentListingResponse
import com.rootscare.data.model.api.response.nurses.nurselist.GetNurseListResponse

interface FragmentCaregiverCategoryListingNavigator {
    fun successGetNurseListResponse(getCaregiverListResponse: GetCaregiverListResponse?)
    fun errorGetCAregiverListResponse(throwable: Throwable?)
    fun errorCaregiverDepartmentListingResponse(throwable: Throwable?)

    fun successDoctorDepartmentListingResponse(doctorDepartmentListingResponse: DoctorDepartmentListingResponse?)

}