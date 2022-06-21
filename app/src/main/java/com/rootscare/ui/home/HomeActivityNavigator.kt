package com.rootscare.ui.home

import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.data.model.api.response.NotificationCountResponse
import com.rootscare.ui.home.model.ModelUpdateCurrentLocation

interface HomeActivityNavigator {
    fun successLogoutResponse(commonResponse: CommonResponse?){}
    fun onSuccessUnreadNotification(commonResponse: NotificationCountResponse?){}
    fun onSuccessUpdateLocation(response: ModelUpdateCurrentLocation?){}
    fun errorLogoutResponse(throwable: Throwable?)
}