package com.rootscare.serviceprovider.ui.pricelistss

import android.graphics.Color
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.lifecycleScope
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.databinding.ActivityPriceListScreenBinding
import com.rootscare.ui.base.BaseActivity
import com.rootscare.ui.newaddition.providerlisting.packages.fragments.FragmentPackages
import com.rootscare.utilitycommon.BookingTypes
import kotlinx.coroutines.delay

class PackageListScreen : BaseActivity<ActivityPriceListScreenBinding, ManagePriceViewModel>() {
    private var binding: ActivityPriceListScreenBinding? = null
    private var mViewModel: ManagePriceViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.activity_price_list_screen
    override val viewModel: ManagePriceViewModel
        get() {
            mViewModel = ViewModelProviders.of(this).get(ManagePriceViewModel::class.java)
            return mViewModel as ManagePriceViewModel
        }
    lateinit var adapter: ViewPagerAdapterForTab

    companion object {
        var providerId = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        binding?.topToolbar?.run {
            tvHeader.text = getString(R.string.health_packages)
            btnBack.setOnClickListener { finish() }
        }

        lifecycleScope.launchWhenCreated {
            try {
                delay(5)
                setupViewPager(binding?.viewPager)
            } catch (e: Exception) {
                hideLoading()
                println("$e")
            }
        }
    }

    private var currentFragment: Fragment? = null
    private fun setupViewPager(viewPager: ViewPager?) {
        try {
            adapter = ViewPagerAdapterForTab(supportFragmentManager)
            adapter.addFragment(FragmentPackages.newInstance(), BookingTypes.PACKAGES.get())
            viewPager?.adapter = adapter
            try {
                binding?.run {
                    tabLayout.setupWithViewPager(binding?.viewPager)
                    viewPager?.offscreenPageLimit = 2
                    tabLayout.setTabTextColors(
                        ContextCompat.getColor(this@PackageListScreen, R.color.color_tab_text_normal),
                        ContextCompat.getColor(
                            this@PackageListScreen,
                            R.color.color_tab_text_selected
                        )
                    )
                    tabLayout.setSelectedTabIndicatorColor(Color.parseColor("#0888D1"))
                }
            } catch (e: Exception) {
                println("$e")
            }

            viewPager?.isSaveFromParentEnabled = false
        } catch (e: Exception) {
            println("$e")
        }
        try {
            viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrollStateChanged(state: Int) {

                }

                override fun onPageScrolled(
                    position: Int,
                    positionOffset: Float,
                    positionOffsetPixels: Int
                ) {
                    try {
                        currentFragment = if (position == 0) {
                            adapter.getItem(0)
                        } else {
                            adapter.getItem(1)
                        }
                    } catch (e: Exception) {
                        println(e.message)
                    }
                }

                override fun onPageSelected(position: Int) {

                }

            })
        } catch (e: Exception) {
            println(e.message)
        }

    }

    override fun reloadPageAfterConnectedToInternet() {

    }
}

class ViewPagerAdapterForTab(manager: FragmentManager) : FragmentStatePagerAdapter(manager) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    override fun getItemPosition(`object`: Any): Int {
        return PagerAdapter.POSITION_NONE
    }

}