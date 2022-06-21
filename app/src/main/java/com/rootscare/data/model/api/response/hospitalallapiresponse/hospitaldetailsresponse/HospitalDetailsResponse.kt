package com.rootscare.data.model.api.response.hospitalallapiresponse.hospitaldetailsresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class HospitalDetailsResponse(
    @field:JsonProperty("result")
    @field:SerializedName("result")
    val result: Result? = null,
    @field:JsonProperty("code")
    @field:SerializedName("code")
    val code: String? = null,
    @field:JsonProperty("message")
    @field:SerializedName("message")
    val message: String? = null,
    @field:JsonProperty("status")
    @field:SerializedName("status")
    val status: Boolean? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ReviewRatingItem(
    @field:JsonProperty("review")
    @field:SerializedName("review")
    var review: String? = null,
    @field:JsonProperty("rating")
    @field:SerializedName("rating")
    var rating: String? = null,
    @field:JsonProperty("review_by")
    @field:SerializedName("review_by")
    var reviewBy: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ServiceDataItem(
    @field:JsonProperty("id")
    @field:SerializedName("id")
    val id: String? = null,
    @field:JsonProperty("title")
    @field:SerializedName("title")
    val title: String? = null

)

@JsonIgnoreProperties(ignoreUnknown = true)
data class DepartmentItem(
    @field:JsonProperty("id")
    @field:SerializedName("id")
    val id: String? = null,
    @field:JsonProperty("title")
    @field:SerializedName("title")
    val title: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Result(
    @field:JsonProperty("date")
    @field:SerializedName("date")
    val date: String? = null,
    @field:JsonProperty("service")
    @field:SerializedName("service")
    val service: ArrayList<ServiceDataItem?>? = null,

    @field:JsonProperty("password")
    @field:SerializedName("password")
    val password: String? = null,
    @field:JsonProperty("username")
    @field:SerializedName("username")
    val username: String? = null,

    @field:JsonProperty("original_password")
    @field:SerializedName("original_password")
    val original_password: String? = null,
    @field:JsonProperty("review_rating")
    @field:SerializedName("review_rating")
    val reviewRating: ArrayList<ReviewRatingItem?>? = null,
    @field:JsonProperty("forgot_code")
    @field:SerializedName("forgot_code")
    val passingYear: String? = null,

    @field:JsonProperty("user_type")
    @field:SerializedName("user_type")
    val userType: String? = null,
    @field:JsonProperty("avg_rating")
    @field:SerializedName("avg_rating")
    val avgRating: String? = null,
    @field:JsonProperty("hospital_id")
    @field:SerializedName("hospital_id")
    val hospital_id: String? = null,
    @field:JsonProperty("department")
    @field:SerializedName("department")
    val department: ArrayList<DepartmentItem?>? = null,
    @field:JsonProperty("email")
    @field:SerializedName("email")
    val email: String? = null,

    @field:JsonProperty("image")
    @field:SerializedName("image")
    val image: String? = null,

    @field:JsonProperty("address")
    @field:SerializedName("address")
    val address: String? = null,
    @field:JsonProperty("name")
    @field:SerializedName("name")
    val name: String? = null,
    @field:JsonProperty("contact")
    @field:SerializedName("contact")
    val contact: String? = null,
    @field:JsonProperty("review_ability")
    @field:SerializedName("review_ability")
    val reviewAbility: String? = null,
    @field:JsonProperty("document")
    @field:SerializedName("document")
    val document: String? = null,
    @field:JsonProperty("status")
    @field:SerializedName("status")
    val status: String? = null,

    @field:JsonProperty("user_id")
    @field:SerializedName("user_id")
    val userId: String? = null,

    @field:JsonProperty("updated_date")
    @field:SerializedName("updated_date")
    val updatedDate: String? = null,
    @field:JsonProperty("created_by")
    @field:SerializedName("created_by")
    val created_by: String? = null,
    @field:JsonProperty("updated_by")
    @field:SerializedName("updated_by")
    val updated_by: String? = null,

    @field:JsonProperty("lab_opening_time")
    @field:SerializedName("lab_opening_time")
    val startTime: String? = null,

    @field:JsonProperty("lab_closing_time")
    @field:SerializedName("lab_closing_time")
    val endTime: String? = null,

    @field:JsonProperty("longitudes")
    @field:SerializedName("longitudes")
    val longitude: String? = null,

    @field:JsonProperty("latitude")
    @field:SerializedName("latitude")
    val latitude: String? = null,
)
