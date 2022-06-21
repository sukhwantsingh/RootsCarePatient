package com.rootscare.data.model.api.response.paymenthistoryresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class ResultItem(
	@field:JsonProperty("transaction_id")
	@field:SerializedName("transaction_id")
	val transactionId: String? = null,
	@field:JsonProperty("date")
	@field:SerializedName("date")
	val date: String? = null,
	@field:JsonProperty("payment_type")
	@field:SerializedName("payment_type")
	val paymentType: String? = null,
	@field:JsonProperty("amount")
	@field:SerializedName("amount")
	val amount: String? = null,
	@field:JsonProperty("user_id")
	@field:SerializedName("user_id")
	val userId: String? = null,
	@field:JsonProperty("subtotal")
	@field:SerializedName("subtotal")
	val subtotal: String? = null,
	@field:JsonProperty("payment_status")
	@field:SerializedName("payment_status")
	val paymentStatus: String? = null,
	@field:JsonProperty("vat")
	@field:SerializedName("vat")
	val vat: String? = null,
	@field:JsonProperty("id")
	@field:SerializedName("id")
	val id: String? = null,
	@field:JsonProperty("order_id")
	@field:SerializedName("order_id")
	val orderId: String? = null,
	@field:JsonProperty("order_type")
	@field:SerializedName("order_type")
	val orderType: String? = null
)