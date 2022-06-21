package com.rootscare.data.model.api.response.nurses.taskresponse

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class GetTaskResponse(
    @field:JsonProperty("result")
    @field:SerializedName("result")
    val result: ArrayList<ResultItem?>? = null,
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
    @field:JsonProperty("duration")
    @field:SerializedName("duration")
    val duration: String? = null,
    var isCheck:Boolean=false

)

