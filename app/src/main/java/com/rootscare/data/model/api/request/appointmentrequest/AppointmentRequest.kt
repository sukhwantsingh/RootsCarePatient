package com.rootscare.data.model.api.request.appointmentrequest

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class AppointmentRequest(
	@field:JsonProperty("user_id")
	@field:SerializedName("user_id")
	var userId: String? = null
)