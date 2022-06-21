package com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingcartresponse.cartitemdeleteresponse

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class DoctorDetails(
	@field:JsonProperty("qualification")
	@field:SerializedName("qualification")
	val qualification: String? = null,
	@field:JsonProperty("image")
	@field:SerializedName("image")
	val image: String? = null,
	@field:JsonProperty("last_name")
	@field:SerializedName("last_name")
	val lastName: String? = null,
	@field:JsonProperty("description")
	@field:SerializedName("description")
	val description: String? = null,
	@field:JsonProperty("experience")
	@field:SerializedName("experience")
	val experience: String? = null,
	@field:JsonProperty("first_name")
	@field:SerializedName("first_name")
	val firstName: String? = null
)