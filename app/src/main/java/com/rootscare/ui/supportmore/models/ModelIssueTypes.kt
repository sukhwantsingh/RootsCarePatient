package com.rootscare.ui.supportmore.models

import androidx.annotation.Keep

@Keep
data class ModelIssueTypes(
    val code: String?,
    val message: String?,
    val result: ArrayList<Result?>?,
    val status: Boolean?
) {
    @Keep
    data class Result(
        val name: String?
    )
}