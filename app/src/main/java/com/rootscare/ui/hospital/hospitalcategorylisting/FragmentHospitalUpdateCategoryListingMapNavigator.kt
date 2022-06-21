package com.rootscare.ui.hospital.hospitalcategorylisting

import com.google.gson.JsonObject

interface FragmentHospitalUpdateCategoryListingMapNavigator {
    fun successGetListResponse(getNurseListResponse: JsonObject?)
    fun errorGetListResponse(throwable: Throwable?)
}