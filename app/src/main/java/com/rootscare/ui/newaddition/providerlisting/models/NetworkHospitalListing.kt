package com.rootscare.ui.newaddition.providerlisting.models


import androidx.annotation.Keep

@Keep
data class NetworkHospitalListing(
    val code: String?,
    val message: String?,
    val result: ArrayList<Result?>?,
    val status: Boolean?
) {
    @Keep
    data class Result(
        val available_provider: ArrayList<AvailableProvider?>?,
        val avg_rating: String?,
        val hb_count: String?,
        val hospital_id: String?,
        val hospital_name: String?,
        val image: String?,
        val user_type: String?,
        val work_area: String?
    ) {
        @Keep
        data class AvailableProvider(
            val avg_rating: String?,
            val doctor_name: String?,
            val speciality: String?,
            val user_type: String?,
            val qualification: String?,
            val image: String?,
            val user_id: String?
        )
    }
}