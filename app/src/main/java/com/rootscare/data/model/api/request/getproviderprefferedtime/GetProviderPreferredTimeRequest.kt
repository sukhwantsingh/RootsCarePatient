package com.rootscare.data.model.api.request.getproviderprefferedtime

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class GetProviderPreferredTimeRequest(
	@field:JsonProperty("slot_date")
	@field:SerializedName("slot_date")
	var slot_date: String? = null,
	@field:JsonProperty("user_id")
	@field:SerializedName("user_id")
    var user_id: String? = null,
	@field:JsonProperty("service_type")
	@field:SerializedName("service_type")
    var service_type: String? = null
)