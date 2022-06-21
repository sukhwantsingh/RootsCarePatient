package com.rootscare.data.model.api.response.hospitalallapiresponse.hospitallabresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class HospitalLabResponse(
    @field:JsonProperty("result")
    @field:SerializedName("result")
    val result: Result? = null,
    @field:JsonProperty("code")
    @field:SerializedName("code")
    val code: String? = null,
    @field:JsonProperty("message")
    @field:SerializedName("message")
    val message: String? = null,
    @field:JsonProperty("status")
    @field:SerializedName("status")
    val status: Boolean? = null
)
@JsonIgnoreProperties(ignoreUnknown = true)
data class Result(
    @field:JsonProperty("id")
    @field:SerializedName("id")
    val id: Int? = null,

    @field:JsonProperty("hospital_name")
    @field:SerializedName("hospital_name")
    val hospital_name: String? = null,
    @field:JsonProperty("lab")
    @field:SerializedName("lab")
    val lab: ArrayList<ResultItem?>? = null



)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ResultItem(
    @field:JsonProperty("id")
    @field:SerializedName("id")
    val id: Int? = null,
    @field:JsonProperty("name")
    @field:SerializedName("name")
    val name: String? = null,
    @field:JsonProperty("price")
    @field:SerializedName("price")
    val price: Int? = null,
    var isCheck: Boolean =false
)