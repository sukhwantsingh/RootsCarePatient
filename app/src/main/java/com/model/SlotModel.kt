package com.model

import androidx.annotation.Keep

@Keep
data class SlotModel(
    var isSelected:Boolean,
    var time:String,
    var isvalid:Boolean,
    var timetoAdd:Int
    )