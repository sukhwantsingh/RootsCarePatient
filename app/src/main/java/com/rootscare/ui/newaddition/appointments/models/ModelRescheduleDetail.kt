package com.rootscare.ui.newaddition.appointments.models


import androidx.annotation.Keep

@Keep
data class ModelRescheduleDetail(
    val code: String?,
    val message: String?,
    val result: Result?,
    val status: Boolean?
) {
    @Keep
    data class Result(
        val acceptance_status: String?,
        val app_date: String?,
        val display_app_date: String?,
        val app_time: String?,
        val appointment_status: String?,
        val booking_date: String?,
        val booking_dates: String?,
        val heading: String?,
        val id: String?,
        val order_id: String?,
        val paymentStatus: String?,
        val paymentType: String?,
        val price: String?,
        val provider_type: String?,
        val provider_id: String?,
        val slot_booking_id: String?,
        val task_details: ArrayList<TaskDetail?>?,
        val task_id: String?,
        val task_time: String?,
        val home_visit_time: String?,
        val online_task_time: String?,
        val slot_time: String?,
        val task_type: String?
    ) {
        @Keep
        data class TaskDetail(
            val id: String?,
            val name: String?,
            val price: String?
        )
    }
}