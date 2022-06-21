package com.interfaces

import com.rootscare.data.model.api.response.nurses.nurseviewtiming.ResultItem


interface OnNurseSlotClick {
    fun onConfirm(modelItem: ResultItem)
}