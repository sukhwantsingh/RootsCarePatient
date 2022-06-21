package com.rootscare.ui.login


import androidx.annotation.Keep

@Keep
data class ModelLoginResponse(
    val code: String?,
    val message: String?,
    val result: Result?,
    val status: Boolean?
) {
    @Keep
    data class Result(
        val address: String?,
        val age: String?,
        val available_time: String?,
        val current_address: String?,
        val daily_rate: String?,
        val date: String?,
        val description: String?,
        val device_type: String?,
        val place_key: String?,
        val work_area: String?,
        val currency_symbol: String?,
        val dob: String?,
        val email: String?,
        val experience: String?,
        val fcm_token: String?,
        val fees: String?,
        val first_name: String?,
        val gender: String?,
        val height: String?,
        val id_image: Any?,
        val id_number: String?,
        val image: String?,
        val institute: String?,
        val last_name: String?,
        val latitude: String?,
        val location: String?,
        val longitudes: String?,
        val marital_status: String?,
        val nationality: String?,
        val otp: String?,
        val passing_year: String?,
        val phone_number: String?,
        val qualification: String?,
        val qualification_certificate: String?,
        val scfhs_image: Any?,
        val scfhs_number: Any?,
        val sms_status: String?,
        val speciality: Any?,
        val status: String?,
        val u_details_id: String?,
        val updated_date: String?,
        val user_id: String?,
        val user_type: String?,
        val weight: String?
    )
}