package com.rootscare.utils

import android.content.Context
import android.content.res.Resources
import android.graphics.PorterDuff
import android.graphics.drawable.Drawable
import android.util.Log

import androidx.core.content.ContextCompat
import com.rootscare.R
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


object ViewUtils {

fun getCurrentDate(): String{
    val c = Calendar.getInstance().time
    println("Current time => $c")

    val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val formattedDate = df.format(c)
    return  formattedDate
}

    fun addThirtyMin(time:String, addtime:Int):String{
        val df = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
        val d: Date = df.parse(time)
        val cal: Calendar = Calendar.getInstance()
        cal.time = d
        cal.add(Calendar.MINUTE, addtime)
        val newTime: String = df.format(cal.getTime())
        Log.wtf("time 30---","--"+newTime)
        return newTime
    }
    fun changeIconDrawableToGray(context: Context, drawable: Drawable?) {
        if (drawable != null) {
            drawable.mutate()
            drawable.setColorFilter(
                ContextCompat.getColor(context, R.color.black),
                PorterDuff.Mode.SRC_ATOP
            )
        }
    }

    fun dpToPx(dp: Float): Int {
        val density = Resources.getSystem().displayMetrics.density
        return (dp * density).roundToInt()
    }

    fun pxToDp(px: Float): Float {
        val densityDpi = Resources.getSystem().displayMetrics.densityDpi.toFloat()
        return px / (densityDpi / 160f)
    }
}// This class is not publicly instantiable
