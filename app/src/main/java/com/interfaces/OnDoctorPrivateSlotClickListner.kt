package com.interfaces

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.doctorprivateslotresponse.ResultItem

interface OnDoctorPrivateSlotClickListner {
    fun onItemClick(modelData: ResultItem?)
}