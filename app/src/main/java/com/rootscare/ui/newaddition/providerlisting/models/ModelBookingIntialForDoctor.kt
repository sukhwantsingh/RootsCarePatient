package com.rootscare.ui.newaddition.providerlisting.models


import androidx.annotation.Keep

@Keep
data class ModelBookingIntialForDoctor(
    val code: String?,
    val message: String?,
    val result: Result?,
    val status: Boolean?
) {
    @Keep
    data class Result(
        val dispaly_provider_type: String?,
        val distance_fare: Int?,
        val distance_fare_text: String?,
        val experience: String?,
        val home_visit_enable: String?,
        val home_visit_task: ArrayList<HomeVisitTask?>?,
        val home_visit_text: String?,
        val home_visit_time: String?,
        val image: String?,
        val lgoin_user_id: String?,
        val online_base_task: ArrayList<OnlineBaseTask?>?,
        val online_base_text: String?,
        val online_enable: String?,
        val online_task_time: String?,
        val provider_id: String?,
        val provider_name: String?,
        val provider_type: String?,
        val qualification: String?,
        val speciality: String?,
        val vat_price: String?,
        val vat_text: String?
    ) {
        @Keep
        data class HomeVisitTask(
            val duration: String?,
            val id: String?,
            val task_price: String?
        )

        @Keep
        data class OnlineBaseTask(
            val duration: String?,
            val id: String?,
            val task_price: String?
        )
    }
}