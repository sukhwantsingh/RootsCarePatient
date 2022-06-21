package com.rootscare.ui.newaddition.providerlisting.models


import androidx.annotation.Keep

@Keep
data class ModelBookingInitialData(
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
        val hour_base_enable: String?,
        val hour_base_task: ArrayList<HourBaseTask?>?,
        val hour_base_text: String?,
        val online_enable: String?, // 0- true 1- false
        val home_visit_enable: String?, // 0- true 1- false

        val hourly_time: String?,
        val image: String?,
        val lgoin_user_id: String?,
        val provider_id: String?,
        val provider_name: String?,
        val provider_type: String?,
        val qualification: String?,
        val speciality: String?,
        val task_base_enable: String?,
        val task_base_task: ArrayList<TaskBaseTask?>?,
        val task_base_text: String?,
        val task_time: String?,
        val vat_price: String?,
        val vat_text: String?
    ) {
        @Keep
        data class HourBaseTask(
            val duration: String?,
            val id: String?,
            val price: String?
        )

        @Keep
        data class TaskBaseTask(
            val id: String?,
            val name: String?,
            val price: String?
        )
    }
}