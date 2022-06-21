package com.rootscare.data.model.api.request.nurse.departmentnurselist

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.google.gson.annotations.SerializedName
@JsonIgnoreProperties(ignoreUnknown = true)
data class DepartmentNurseListRequest(
	@field:JsonProperty("department_id")
	@field:SerializedName("department_id")
	var departmentId: String? = null
)
