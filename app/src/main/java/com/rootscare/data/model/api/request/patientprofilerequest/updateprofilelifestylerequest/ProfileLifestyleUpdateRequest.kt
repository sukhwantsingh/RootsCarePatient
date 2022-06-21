package com.rootscare.data.model.api.request.patientprofilerequest.updateprofilelifestylerequest

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class ProfileLifestyleUpdateRequest(
	@field:JsonProperty("alcohol")
	@field:SerializedName("alcohol")
	var alcohol: String? = null,
	@field:JsonProperty("activity_level")
	@field:SerializedName("activity_level")
	var activityLevel: String? = null,
	@field:JsonProperty("occupation")
	@field:SerializedName("occupation")
	var occupation: String? = null,
	@field:JsonProperty("user_id")
	@field:SerializedName("user_id")
	var userId: String? = null,
	@field:JsonProperty("smoking")
	@field:SerializedName("smoking")
	var smoking: String? = null,
	@field:JsonProperty("blood_group")
	@field:SerializedName("blood_group")
	var bloodGroup: String? = null,
	@field:JsonProperty("food_preference")
	@field:SerializedName("food_preference")
	var foodPreference: String? = null
)