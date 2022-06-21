package com.rootscare.ui.notification

import com.rootscare.ui.notification.models.ModelNotificationResponse
import com.rootscare.ui.notification.models.ModelUpdateRead

interface FragmentNotificationNavigator {
    fun successNotificationListResponse(notificationResponse: ModelNotificationResponse?)
    fun successUpdateRead(response: ModelUpdateRead?){}
    fun errorInApi(throwable: Throwable?)
}