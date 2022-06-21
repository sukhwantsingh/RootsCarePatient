package com.rootscare.data.model.api.request.nurse.nursrtask

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class nursetask(
    @field:JsonProperty("nurse_id")
    @field:SerializedName("nurse_id")
    var nurse_id: String? = null
)
