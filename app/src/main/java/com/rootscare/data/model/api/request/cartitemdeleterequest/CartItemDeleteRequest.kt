package com.rootscare.data.model.api.request.cartitemdeleterequest

import androidx.annotation.Keep
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@Keep
@JsonIgnoreProperties(ignoreUnknown = true)
data class CartItemDeleteRequest(
	@field:JsonProperty("cart_id")
	@field:SerializedName("cart_id")
	var id: String? = null
)