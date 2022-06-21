package com.rootscare.data.model.api.request.reschedule

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class DoctorAppointmentRescheduleRequest(
	@field:JsonProperty("service_type")
	@field:SerializedName("service_type")
	var serviceType: String? = null,
	@field:JsonProperty("from_date")
	@field:SerializedName("from_date")
	var fromDate: String? = null,
	@field:JsonProperty("to_date")
	@field:SerializedName("to_date")
	var toDate: String? = null,
	@field:JsonProperty("id")
	@field:SerializedName("id")
	var id: String? = null,
	@field:JsonProperty("to_time")
	@field:SerializedName("to_time")
	var toTime: String? = null,
	@field:JsonProperty("from_time")
	@field:SerializedName("from_time")
	var fromTime: String? = null
)
