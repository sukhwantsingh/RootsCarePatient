package com.interfaces

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.bookingcartresponse.ResultItem
import com.rootscare.ui.bookingcart.models.ModelPatientCartNew

interface OnClickOfCartItem {
    fun onFirstItemClick(id: Int)
    fun onSecondItemClick(cartItemList: ArrayList<ModelPatientCartNew.Result?>?)
}