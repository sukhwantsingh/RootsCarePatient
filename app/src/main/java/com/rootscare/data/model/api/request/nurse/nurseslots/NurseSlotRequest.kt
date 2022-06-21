package com.rootscare.data.model.api.request.nurse.nurseslots

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class NurseSlotRequest(
	@field:JsonProperty("service_type")
	@field:SerializedName("service_type")
	var serviceType: String? = null,
	@field:JsonProperty("service_provider_id")
	@field:SerializedName("service_provider_id")
	var serviceProviderId: String? = null,
	@field:JsonProperty("from_date")
	@field:SerializedName("from_date")
	var fromDate: String? = null,
	@field:JsonProperty("to_date")
	@field:SerializedName("to_date")
	var toDate: String? = null,
	@field:JsonProperty("user_id")
	@field:SerializedName("user_id")
	var userId: String? = null
)
