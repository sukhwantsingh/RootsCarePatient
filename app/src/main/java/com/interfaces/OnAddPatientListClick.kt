package com.interfaces

import com.rootscare.data.model.api.response.doctorallapiresponse.doctorbooking.getpatientfamilymemberlistresponse.ResultItem

interface OnAddPatientListClick {
    fun onItemClick(modelOfGetAddPatientList: ResultItem?)
}