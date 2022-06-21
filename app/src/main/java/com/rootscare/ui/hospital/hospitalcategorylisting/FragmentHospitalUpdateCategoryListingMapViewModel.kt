package com.rootscare.ui.hospital.hospitalcategorylisting

import android.util.Log
import com.google.gson.Gson
import com.rootscare.data.model.api.request.hospital.HospitalNearbyRequest
import com.rootscare.data.model.api.request.nurse.searchbyname.NurseSearchByNameRequest
import com.rootscare.ui.base.BaseViewModel

class FragmentHospitalUpdateCategoryListingMapViewModel :
    BaseViewModel<FragmentHospitalUpdateCategoryListingMapNavigator>() {
    fun apiDataList(nurseSearchByNameRequest: HospitalNearbyRequest) {
        val disposable = apiServiceWithGsonFactory.apihospitallistlat(nurseSearchByNameRequest)
            .subscribeOn(_scheduler_io)
            .observeOn(_scheduler_ui)
            .subscribe({ response ->
                if (response != null) {
                    Log.d("check_response", ": " + Gson().toJson(response))
//                    navigator.successGetListResponse(response)

                } else {
                    Log.d("check_response", ": null response")
                }
            }, { throwable ->
                run {
                    navigator.errorGetListResponse(throwable)
                    Log.d("check_response_error", ": " + throwable.message)
                }
            })

        compositeDisposable.add(disposable)
    }


}