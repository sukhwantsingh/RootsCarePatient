package com.rootscare.ui.newaddition.providerlisting.packages.models


import androidx.annotation.Keep

@Keep
data class ModelPackages(
    val code: String?,
    val message: String?,
    val result: Result?,
    val status: Boolean?
) {
    @Keep
    data class Result(
        val dispaly_provider_type: String?,
        val experience: String?,
        val hospital_id: String?,
        val hospital_name: String?,
        val image: String?,
        val iso_text: String?,
        val lgoin_user_id: String?,
        val package_base_enable: String?,
        val package_base_task: List<PackageBaseTask?>?,
        val package_base_text: String?,
        val provider_id: String?,
        val provider_name: String?,
        val provider_type: String?,
        val qualification: String?,
        val speciality: String?
    ) {
        @Keep
        data class PackageBaseTask(
            val dis_off: String?,
            val id: String?,
            val maxrp: String?,
            val name: String?,
            val price: String?,
            val task_details: String?,
            val test_count: String?
        )
    }
}