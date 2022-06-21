package com.rootscare.data.model.api.request.doctorrequest.bookingcartrequests

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class BookingCartRequest(
	@field:JsonProperty("login_user_id")
	@field:SerializedName("login_user_id")
    var userId: String? = null
)