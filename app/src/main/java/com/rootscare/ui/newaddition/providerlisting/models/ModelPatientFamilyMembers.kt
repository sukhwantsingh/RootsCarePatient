package com.rootscare.ui.newaddition.providerlisting.models


import androidx.annotation.Keep

@Keep
data class ModelPatientFamilyMembers(
    val code: String?,
    val message: String?,
    val result: ArrayList<Result?>?,
    val status: Boolean?
) {
    @Keep
    data class Result(
//        val age: String?,
//        val created_by: String?,
//        val created_date: String?,
//        val email: String?,
//        val gender: String?,
        val id: String?,
        val patient_id: String?,
        val first_name: String?,
        val last_name: String?,
        val image: String?,
//        val phone_number: String?,
//        val status: String?,
//        val updated_by: String?,
//        val updated_date: String?
    )
}