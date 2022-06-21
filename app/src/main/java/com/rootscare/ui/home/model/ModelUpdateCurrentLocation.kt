package com.rootscare.ui.home.model


import androidx.annotation.Keep

@Keep
data class ModelUpdateCurrentLocation(
    val code: String?,
    val message: String?,
    val result: Result?,
    val status: Boolean?
) {
    @Keep
    data class Result(
        val current_address: String?,
        val latitude: String?,
        val longitudes: String?,
        val user_id: String?
    )
}