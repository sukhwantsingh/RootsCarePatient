package com.rootscare.ui.newaddition.providerlisting.models


import androidx.annotation.Keep
import androidx.core.text.parseAsHtml
import java.util.*
import kotlin.collections.ArrayList

@Keep
data class ModelProviderListing(
    val code: String?,
    val message: String?,
    val result: ArrayList<Result?>?,
    val status: Boolean?
) {
    @Keep
    data class Result(
        val availability: List<ModelProviderDetails.Result.Availability?>?,
        val avg_rating: String?,
        val bavi_text: String?,
        val av_text: String?,
        val loc_text: String?,
        val booking_count: String?,
        val distance: String?,
        val email: String?,
        val image: String?,
        val qualification: String?,
        val hospital_id: String?,
        val hospital_name: String?,
        val experience: String?,
        val provider_address: String?,
        val package_base_enable: String?,  // 0- true 1- false
        val task_base_enable: String?,  // 0- true 1- false
        val hour_base_enable: String?, // 0- true 1- false
        val online_enable: String?, // 0- true 1- false
        val home_visit_enable: String?, // 0- true 1- false

        val iso_certificate: String?,
        val safe_text: String?,
        val provider_available: String?,
        val provider_name: String?,
        val speciality: String?,
        val total_review_rating: String?,
        val user_id: String?,
        val user_type: String?) {

        fun getFullName() = provider_name

        fun getForceSpecialityOrHospital() = if(hospital_id.isNullOrBlank()) speciality else speciality

        fun getLocationText() =  "$loc_text,  <font color='#0888D1'>$distance</font>".parseAsHtml()

        fun showLocationOrNot() = if(hospital_id.isNullOrBlank()) "show" else null

        fun getAvailDays() = "$av_text  <font color='#0888D1'><b>${availability?.joinToString { avd -> avd?.slot_day ?:"" }?.uppercase(Locale.getDefault())}</b></font>".parseAsHtml()

        fun getExpAndQualification() = "$experience | $qualification"  //  "8 Years Exp. | MBBS, MD"

        @Keep
        data class Availability(
            val slot_day: String?
        )
    }
}