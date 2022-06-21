package com.rootscare.ui.newaddition.providerlisting.models


import androidx.annotation.Keep

@Keep
data class ModelTImeSlotsForDoctor(
    val code: String?,
    val message: String?,
    val result: Result?,
    val status: Boolean?
) {
    @Keep
    data class Result(
        val date: String?,
        val home_visit_time: String?,
        val online_task_time: String?
    )
}