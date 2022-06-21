package com.rootscare.data.model.api.request

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class CommonUserIdRequest(
	@field:JsonProperty("id")
	@field:SerializedName("id")
	var id: String? = null,
	@field:JsonProperty("service_type")
	@field:SerializedName("service_type")
	var service_type: String? = null
)
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class CommonNotificationIdRequest(
	@field:JsonProperty("id")
	@field:SerializedName("id")
	var id: String? = null,
	@field:JsonProperty("read")
	@field:SerializedName("read")
	var read: String? = null
)