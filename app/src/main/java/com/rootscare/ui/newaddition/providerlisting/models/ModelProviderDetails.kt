package com.rootscare.ui.newaddition.providerlisting.models

import androidx.annotation.Keep
import com.rootscare.utilitycommon.ProviderTypes

@Keep
data class ModelProviderDetails(
    val code: String?,
    val message: String?,
    val result: Result?,
    val status: Boolean?
) {
    @Keep
    data class Result(
        val availability: ArrayList<ModelProviderListing.Result.Availability?>?,
        val available_provider: ArrayList<AvailableProviders?>?,
        val avg_rating: String?,
        val booking_count: String?,
        val description: String?,
        val email: String?,
        val experience: String?,
        val provider_name: String?,
        val task_base_enable: String?,
        val online_enable: String?,
        val hour_base_enable: String?,
        val home_visit_enable: String?,
        val provider_available: String?,
        val how_work_text: String?,
        val how_work_value: String?,
        val bavi_text: String?,
        val image: String?,
        val qualification: String?,
        val speciality: String?,
        val user_type: String?) {

        // 0- true 1- false
        fun getTaskEnability() = if(user_type?.trim().equals(ProviderTypes.DOCTOR.getType(), ignoreCase = true)) {
            online_enable ?: "1" } else task_base_enable ?: "1"

        fun getHourEnability() = if(user_type?.trim().equals(ProviderTypes.DOCTOR.getType(), ignoreCase = true)) {
            home_visit_enable ?: "1" } else hour_base_enable ?: "1"

        fun getFullname() = "$provider_name".trim()
        fun getExp() = if (experience.isNullOrBlank().not()) "$experience" else "-"
        fun getAvailableDays()= availability?.joinToString { avlt -> avlt?.slot_day ?:"" }

        @Keep
        data class Availability(val slot_day: String?)

        @Keep
        data class AvailableProviders(
            val avg_rating: String?,
            val provider_name: String?,
            val image: String?,
            val qualification: String?,
            val speciality: String?,
            val user_id: String?
        )
    }
}