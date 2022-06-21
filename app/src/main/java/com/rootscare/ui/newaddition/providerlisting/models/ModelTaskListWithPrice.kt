package com.rootscare.ui.newaddition.providerlisting.models

import androidx.annotation.Keep

@Keep
data class ModelTaskListWithPrice(
    val code: String?, val message: String?,
    val result: ArrayList<Result?>?, val status: Boolean?
) {
    @Keep
    data class Result(
        val id: String?,
        val name: String?,
        val price: String?,
        var isChecked: Boolean?
    )
}

@Keep
data class ModelDateSlots(
    var dayNdate:String?,
    var dateValue:String?,
    var isSelected_:Boolean = false
)