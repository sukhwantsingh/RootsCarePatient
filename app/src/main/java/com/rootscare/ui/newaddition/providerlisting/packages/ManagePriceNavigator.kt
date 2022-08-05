package com.rootscare.serviceprovider.ui.pricelistss

import com.rootscare.ui.newaddition.providerlisting.packages.models.ModelPackageDetails
import com.rootscare.ui.newaddition.providerlisting.packages.models.ModelPackages


interface ManagePriceNavigator {
    fun onSuccessAfterGetTaskPrice(taskList: ModelPackages){}
    fun onSuccessPackageDetails(response: ModelPackageDetails?){}
    fun onThrowable(throwable: Throwable)


}