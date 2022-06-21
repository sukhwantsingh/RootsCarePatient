package com.rootscare.ui.notification.models


import androidx.annotation.Keep

@Keep
data class ModelNotificationResponse(
    val code: String?,
    val message: String?,
    val result: ArrayList<Result?>?,
    val status: Boolean?
) {
    @Keep
    data class Result(
        val body: String?,
        val date: String?,
        val datetime: String?,
        val id: String?,
        val order_id: String?,
        var read: String?,
        val title: String?,
        val user_id: String?
    )
}