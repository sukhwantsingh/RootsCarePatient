package com.rootscare.ui.reviewandrating

import com.rootscare.data.model.api.response.patientreviewandratingresponse.PatientReviewAndRatingResponse

interface FragmentReviewAndRatingNavigator {

    fun successPatientReviewAndRatingResponse(patientReviewAndRatingResponse: PatientReviewAndRatingResponse?)
    fun errorPatientReviewAndRatingResponse(throwable: Throwable?)
}