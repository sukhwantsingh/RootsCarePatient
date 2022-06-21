package com.interfaces

import com.rootscare.data.model.api.response.nurses.nursehourlyslot.ResultItem

interface OnHourlyItemClick {

    fun onConfirm(modelItem: ResultItem)
}