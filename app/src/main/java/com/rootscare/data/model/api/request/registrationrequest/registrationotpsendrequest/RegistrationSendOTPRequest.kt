package com.rootscare.data.model.api.request.registrationrequest.registrationotpsendrequest

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class RegistrationSendOTPRequest(
	@field:JsonProperty("id_number")
	@field:SerializedName("id_number")
	var idNumber: String? = null,
	@field:JsonProperty("password")
	@field:SerializedName("password")
	var password: String? = null,
	@field:JsonProperty("last_name")
	@field:SerializedName("last_name")
	var lastName: String? = null,
	@field:JsonProperty("phone_number")
	@field:SerializedName("phone_number")
	var phoneNumber: String? = null,
	@field:JsonProperty("first_name")
	@field:SerializedName("first_name")
	var firstName: String? = null,
	@field:JsonProperty("email")
	@field:SerializedName("email")
	var email: String? = null,
	@field:JsonProperty("confirm_password")
	@field:SerializedName("confirm_password")
	var confirmPassword: String? = null,
	@field:JsonProperty("fcm_token")
	@field:SerializedName("fcm_token")
	var fcmToken: String? = null,
	@field:JsonProperty("device_type")
	@field:SerializedName("device_type")
	var deviceType: String? = null,
	@field:JsonProperty("work_area")
	@field:SerializedName("work_area")
	var work_area: String? = null
)