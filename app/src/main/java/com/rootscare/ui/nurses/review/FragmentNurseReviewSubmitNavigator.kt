package com.rootscare.ui.nurses.review

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorreviewsubmitresponse.DoctorReviewRatingSubmiteResponse

interface FragmentNurseReviewSubmitNavigator {
    fun successDoctorReviewRatingSubmiteResponse(doctorReviewRatingSubmiteResponse: DoctorReviewRatingSubmiteResponse?)
    fun errorDoctorReviewRatingSubmiteResponse(throwable: Throwable?)
}