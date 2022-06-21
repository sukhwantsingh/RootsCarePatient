package com.rootscare.data.model.api.request.checkoutdoctorbookingrequest

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class CheckoutDoctorBookingRequest(
	@field:JsonProperty("cart_id")
	@field:SerializedName("cart_id")
	var cartId: String? = null,
	@field:JsonProperty("sub_total_price")
	@field:SerializedName("sub_total_price")
	var subTotalPrice: String? = null,
	@field:JsonProperty("payment_type")
	@field:SerializedName("payment_type")
	var paymentType: String? = null,
	@field:JsonProperty("total_price")
	@field:SerializedName("total_price")
	var totalPrice: String? = null,
	@field:JsonProperty("user_id")
	@field:SerializedName("user_id")
	var userId: String? = null,
	@field:JsonProperty("vat_price")
	@field:SerializedName("vat_price")
	var vatPrice: String? = null
)