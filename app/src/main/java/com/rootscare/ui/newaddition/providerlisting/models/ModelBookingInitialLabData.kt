package com.rootscare.ui.newaddition.providerlisting.models


import androidx.annotation.Keep

@Keep
data class ModelBookingInitialLabData(
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
        val hospital_id: String?,
        val hospital_name: String?,
        val image: String?,
        val lgoin_user_id: String?,
        val package_base_task: ArrayList<PackageBaseTask?>?,
        val provider_id: String?,
        val provider_name: String?,
        val iso_text: String?,
        val provider_type: String?,
        val qualification: String?,
        val speciality: String?,
        val package_base_enable: String?,
        val task_base_enable: String?,
        val task_base_task: ArrayList<TaskBaseTask?>?,
        val task_base_text: String?,
        val task_time: String?,
        val vat_price: String?,
        val vat_text: String?
    ) {
        @Keep
        data class PackageBaseTask(
            val name: String?,
            val pid: String?,
            val price: String?,
            val test_count: String?,
            var isChecked: Boolean?
        )

        @Keep
        data class TaskBaseTask(
            val id: String?,
            val name: String?,
            val price: String?
        )
    }
}

fun ArrayList<ModelBookingInitialLabData.Result.TaskBaseTask?>?.toDomainModel() = this?.map {
    ModelBookingInitialData.Result.TaskBaseTask(it?.id, it?.name, it?.price) }?.toList()













