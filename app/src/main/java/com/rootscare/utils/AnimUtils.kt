package com.rootscare.utils

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import com.rootscare.R

object AnimUtils{

    fun animateIntent(activity: Activity, viewStart: View?, s_data: String?, intent: Intent, requestCode: Int) { // Ordinary Intent for launching a new activity
        if (s_data != null) {
            intent.putExtra("PassData", s_data)
        }
//        val transitionName = activity.getString(R.string.transition_string)
        var transitionName = viewStart?.transitionName
        if (viewStart?.transitionName == null){
            transitionName = activity.getString(R.string.transition_string)
        }
        // Define the view that the animation will start from  viewStart = findViewById(R.id.imageView);*/
        val options: ActivityOptionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(activity,
            viewStart!!,  // Starting view
            transitionName!! // The String
        )
        //Start the Intent
        ActivityCompat.startActivityForResult(activity, intent, requestCode, options.toBundle())
    }
}