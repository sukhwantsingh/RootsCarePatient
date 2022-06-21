package com.rootscare.data.model.api.request.loginrequest

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class LoginRequest(
	@field:JsonProperty("password")
	@field:SerializedName("password")
	var password: String? = null,
	@field:JsonProperty("email_phone")
	@field:SerializedName("email_phone")
	var emailPhone: String? = null,
	@field:JsonProperty("fcm_token")
	@field:SerializedName("fcm_token")
	var fcm_token: String? = null,
	@field:JsonProperty("device_type")
	@field:SerializedName("device_type")
	var device_type: String? = null	,
	@field:JsonProperty("device_lang")
	@field:SerializedName("device_lang")
	var device_lang: String? = null
)