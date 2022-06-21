package com.rootscare.utils

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.core.widget.NestedScrollView

class WorkaroundNestedScrollView(context: Context?, attrs: AttributeSet?) : NestedScrollView(
    context!!, attrs
) {
    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (ev.action == MotionEvent.ACTION_DOWN) {
            // Explicitly call computeScroll() to make the Scroller compute itself
            computeScroll()
        }
        return super.onInterceptTouchEvent(ev)
    }
}