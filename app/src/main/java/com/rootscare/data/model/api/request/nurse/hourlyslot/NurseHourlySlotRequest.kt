package com.rootscare.data.model.api.request.nurse.hourlyslot

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class NurseHourlySlotRequest(
	@field:JsonProperty("user_id")
	@field:SerializedName("user_id")
	var userId: Int? = null
)
