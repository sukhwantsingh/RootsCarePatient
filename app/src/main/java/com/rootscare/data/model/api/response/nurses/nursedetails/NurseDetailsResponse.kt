package com.rootscare.data.model.api.response.nurses.nursedetails

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class NurseDetailsResponse(
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
data class QualificationDataItem(
	@field:JsonProperty("passing_year")
	@field:SerializedName("passing_year")
	val passingYear: String? = null,
	@field:JsonProperty("qualification")
	@field:SerializedName("qualification")
	val qualification: String? = null,
	@field:JsonProperty("institute")
	@field:SerializedName("institute")
	val institute: String? = null,
	@field:JsonProperty("qualification_certificate")
	@field:SerializedName("qualification_certificate")
	val qualificationCertificate: String? = null
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class HourlyRatesItem(
	@field:JsonProperty("duration")
	@field:SerializedName("duration")
	val duration: String? = null,
	@field:JsonProperty("price")
	@field:SerializedName("price")
	val price: String? = null
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
	@field:JsonProperty("qualification_data")
	@field:SerializedName("qualification_data")
	val qualificationData: ArrayList<QualificationDataItem?>? = null,
	@field:JsonProperty("fees")
	@field:SerializedName("fees")
	val fees: String? = null,
	@field:JsonProperty("daily_rate")
	@field:SerializedName("daily_rate")
	val dailyRate: String? = null,
	@field:JsonProperty("gender")
	@field:SerializedName("gender")
	val gender: String? = null,
	@field:JsonProperty("hourly_rates")
	@field:SerializedName("hourly_rates")
	val hourlyRates: ArrayList<HourlyRatesItem?>? = null,
	@field:JsonProperty("description")
	@field:SerializedName("description")
	val description: String? = null,
	@field:JsonProperty("speciality")
	@field:SerializedName("speciality")
	val speciality: String? = null,
	@field:JsonProperty("experience")
	@field:SerializedName("experience")
	val experience: String? = null,
	@field:JsonProperty("review_rating")
	@field:SerializedName("review_rating")
	val reviewRating: ArrayList<ReviewRatingItem?>? = null,
	@field:JsonProperty("passing_year")
	@field:SerializedName("passing_year")
	val passingYear: String? = null,
	@field:JsonProperty("available_time")
	@field:SerializedName("available_time")
	val availableTime: String? = null,
	@field:JsonProperty("user_type")
	@field:SerializedName("user_type")
	val userType: String? = null,
	@field:JsonProperty("avg_rating")
	@field:SerializedName("avg_rating")
	val avgRating: String? = null,
	@field:JsonProperty("u_details_id")
	@field:SerializedName("u_details_id")
	val uDetailsId: String? = null,
	@field:JsonProperty("department")
	@field:SerializedName("department")
	val department: ArrayList<DepartmentItem?>? = null,
	@field:JsonProperty("first_name")
	@field:SerializedName("first_name")
	val firstName: String? = null,
	@field:JsonProperty("email")
	@field:SerializedName("email")
	val email: String? = null,
	@field:JsonProperty("height")
	@field:SerializedName("height")
	val height: String? = null,
	@field:JsonProperty("image")
	@field:SerializedName("image")
	val image: String? = null,
	@field:JsonProperty("id_number")
	@field:SerializedName("id_number")
	val idNumber: String? = null,
	@field:JsonProperty("address")
	@field:SerializedName("address")
	val address: String? = null,
	@field:JsonProperty("last_name")
	@field:SerializedName("last_name")
	val lastName: String? = null,
	@field:JsonProperty("weight")
	@field:SerializedName("weight")
	val weight: String? = null,
	@field:JsonProperty("review_ability")
	@field:SerializedName("review_ability")
	val reviewAbility: String? = null,
	@field:JsonProperty("qualification")
	@field:SerializedName("qualification")
	val qualification: String? = null,
	@field:JsonProperty("marital_status")
	@field:SerializedName("marital_status")
	val maritalStatus: String? = null,
	@field:JsonProperty("nationality")
	@field:SerializedName("nationality")
	val nationality: String? = null,
	@field:JsonProperty("user_id")
	@field:SerializedName("user_id")
	val userId: String? = null,
	@field:JsonProperty("dob")
	@field:SerializedName("dob")
	val dob: String? = null,
	@field:JsonProperty("phone_number")
	@field:SerializedName("phone_number")
	val phoneNumber: String? = null,
	@field:JsonProperty("location")
	@field:SerializedName("location")
	val location: String? = null,
	@field:JsonProperty("institute")
	@field:SerializedName("institute")
	val institute: String? = null,
	@field:JsonProperty("updated_date")
	@field:SerializedName("updated_date")
	val updatedDate: String? = null,
	@field:JsonProperty("age")
	@field:SerializedName("age")
	val age: String? = null,
	@field:JsonProperty("qualification_certificate")
	@field:SerializedName("qualification_certificate")
	val qualificationCertificate: String? = null,
	@field:JsonProperty("start_time")
	@field:SerializedName("start_time")
	val startTime: String? = null,
	@field:JsonProperty("end_time")
	@field:SerializedName("end_time")
	val endTime: String? = null
)
