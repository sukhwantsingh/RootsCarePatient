package com.rootscare.ui.newaddition.providerlisting.models


import androidx.annotation.Keep

@Keep
data class ModelNetworkTimeSlots(
    val code: String?,
    val message: String?,
    val result: Result?,
    val status: Boolean?
) {
    @Keep
    data class Result(
        val date: String?,
        val task_time: String?,
        val hourly_time: String?,
    )
}