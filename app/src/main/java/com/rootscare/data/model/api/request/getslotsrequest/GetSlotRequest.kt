package com.rootscare.data.model.api.request.getslotsrequest

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class GetSlotRequest(
	@field:JsonProperty("service_id")
	@field:SerializedName("service_id")
	var serviceid: Int? = null,
	@field:JsonProperty("appointment_date")
	@field:SerializedName("appointment_date")
    var appointmentdate: String? = null,
	@field:JsonProperty("service_type")
	@field:SerializedName("service_type")
    var service_type: String? = null
)
