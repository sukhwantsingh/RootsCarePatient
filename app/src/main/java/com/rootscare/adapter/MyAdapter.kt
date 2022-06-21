package com.rootscare.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.rootscare.ui.login.subfragment.forgotpassword.FragmentForGotPassword
import com.rootscare.ui.login.subfragment.loginfragment.FragmentLogin
import com.rootscare.ui.login.subfragment.registrationfragment.FragmentRegistration
import com.rootscare.ui.login.subfragment.registrationotp.FragmentRegistrationOtpCheck


class MyAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm!!) {
    val NUMBER_OF_PAGES = 4
    override fun getCount(): Int {
        return NUMBER_OF_PAGES
    }

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> FragmentLogin.newInstance()
            1 ->  // return a different Fragment class here
                // if you want want a completely different layout
                FragmentRegistration.newInstance()
            2->  // return a different Fragment class here
                // if you want want a completely different layout
                FragmentRegistrationOtpCheck.newInstance()

            3->  // return a different Fragment class here
                // if you want want a completely different layout
                FragmentForGotPassword.newInstance()
            else -> FragmentLogin.newInstance()
        }
    }


}

