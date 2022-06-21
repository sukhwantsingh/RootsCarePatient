package com.rootscare.data.model.api.request.home

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName

@JsonIgnoreProperties(ignoreUnknown = true)
data class HomeSearchRequest(
    @field:JsonProperty("search_content")
    @field:SerializedName("search_content")
    var searchContent: String? = null,
    @field:JsonProperty("latitude")
    @field:SerializedName("latitude")
    var latitude: String? = null,
    @field:JsonProperty("longitudes")
    @field:SerializedName("longitudes")
    var longitudes: String? = null
)
