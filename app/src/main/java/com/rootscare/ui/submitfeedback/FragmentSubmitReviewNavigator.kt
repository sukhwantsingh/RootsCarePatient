package com.rootscare.ui.submitfeedback

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorreviewsubmitresponse.DoctorReviewRatingSubmiteResponse


interface FragmentSubmitReviewNavigator {
    fun successDoctorReviewRatingSubmiteResponse(doctorReviewRatingSubmiteResponse: DoctorReviewRatingSubmiteResponse?)
    fun errorDoctorReviewRatingSubmiteResponse(throwable: Throwable?)
}