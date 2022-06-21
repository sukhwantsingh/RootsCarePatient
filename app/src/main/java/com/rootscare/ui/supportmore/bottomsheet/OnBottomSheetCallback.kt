package com.rootscare.ui.supportmore.bottomsheet

interface OnBottomSheetCallback {
    fun onCloseBottomSheet(){}

    fun onSubmitReschedule(vararg data: String){}

    fun onSubmitRating(vararg data: String){}
    fun onDateChanged(vararg data: String){}


    fun onGoBack(){}
    fun onRetry(){}


}

