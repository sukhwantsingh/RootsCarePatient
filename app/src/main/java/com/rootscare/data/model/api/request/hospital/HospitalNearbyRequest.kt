package com.rootscare.data.model.api.request.hospital

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class HospitalNearbyRequest(
    @field:JsonProperty("latitude")
    @field:SerializedName("latitude")
    var latitude: String? = null,

    @field:JsonProperty("longitudes")
    @field:SerializedName("longitudes")
    var longitudes: String? = null,

    @field:JsonProperty("distance")
    @field:SerializedName("distance")
    var distance: String? = null
)
