package com.rootscare.ui.home

import android.Manifest
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.dialog.CommonDialog
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.JsonObject
import com.interfaces.DialogClickCallback
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.appointmentrequest.AppointmentRequest
import com.rootscare.data.model.api.response.CommonResponse
import com.rootscare.data.model.api.response.NotificationCountResponse
import com.rootscare.ui.base.BaseActivity
import com.rootscare.ui.bookingcart.FragmentBookingCart
import com.rootscare.ui.home.model.ModelUpdateCurrentLocation
import com.rootscare.ui.home.subfragment.HomeFragment
import com.rootscare.ui.login.LoginActivity
import com.rootscare.ui.newaddition.appointments.FragNewAppointmentListing
import com.rootscare.ui.notification.FragmentNotification
import com.rootscare.ui.profile.FragmentProfile
import com.rootscare.ui.supportmore.FragmentSupportMore
import com.rootscare.ui.supportmore.SupportAndMore
import com.rootscare.utilitycommon.*
import com.rootscare.databinding.ActivityHomeBinding
import com.rootscare.ui.newaddition.providerlisting.*
import com.rootscare.ui.newaddition.providerlisting.patientaddition.FragmentAddPatient
import com.rootscare.utils.firebase.Config
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.common_toolbar.*
import mumayank.com.airlocationlibrary.AirLocation
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.lang.Exception
import java.net.SocketException
import java.util.*

class HomeActivity : BaseActivity<ActivityHomeBinding, HomeActivityViewModel>(), HomeActivityNavigator {
    private var activityHomeBinding: ActivityHomeBinding? = null
    private var homeViewModel: HomeActivityViewModel? = null
    private var checkForClose = false

    private val TAG = "PermissionDemo"

    companion object {
        var isProfileUpdated = MutableLiveData<Boolean?>(true)
        var isNotificationChecked = MutableLiveData<Boolean?>(false)
        var isUnreadNotficationAvailable = MutableLiveData<Boolean?>(null)
        var isCurrLocChoosed = MutableLiveData<Boolean?>(false)
        var isDiffLocChoosed = MutableLiveData<Boolean?>(false)

        var currLat :Double  = 0.0
        var currLong :Double  = 0.0
        var providerName = ""
        var appointmentHeading = ""

        fun newIntent(activity: Activity): Intent {
            return Intent(activity, HomeActivity::class.java)
        }
    }

    override val bindingVariable: Int
        get() = BR.viewModel

    override val layoutId: Int
        get() = R.layout.activity_home

    override val viewModel: HomeActivityViewModel
        get() {
            homeViewModel = ViewModelProviders.of(this).get(HomeActivityViewModel::class.java)
            return homeViewModel as HomeActivityViewModel
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        homeViewModel?.navigator = this
        activityHomeBinding = viewDataBinding
        logSentFriendRequestEvent()
      //  BottomNavigationViewHelper.removeShiftMode(activityHomeBinding!!.navigation)

        drawerNavigationMenu()
        setDataAndSelectOptionInDrawerNavigation()
        activityHomeBinding?.navigation?.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        checkInBackstack(HomeFragment.newInstance())
        observerProfileUpdate()
        hitNotificationUnread()
    }
    /**
     * This function assumes logger is an instance of AppEventsLogger and has been
     * created using AppEventsLogger.newLogger() call.
     */
    fun logSentFriendRequestEvent() {
        try {
            logger.logEvent("reachedHomeScreen")
        }catch (e:Exception){
            logger.logEvent("reachedHomeScreen_something_went_wrong")
        }
    }

    private fun observerProfileUpdate(){
        isProfileUpdated.observe(this) {
            try {
                it?.let {
                    if(it) { initViews() }
                }
            }catch (e:Exception){
                println(e)
            }
        }
        isNotificationChecked.observe(this) {
            try {
                it?.let {
                    if(it) {
                        hitNotificationUnread()
                        isNotificationChecked.value = false
                    }
                }
            }catch (e:Exception){
                println(e)
            }
        }
        isUnreadNotficationAvailable.observe(this) {
            try {
                it?.let {
                    val mTlBar = activityHomeBinding?.appBarHomepage?.toolbarLayout
                    if(it) {
                         mTlBar?.tvUnreadCount?.visibility = View.VISIBLE
                      } else mTlBar?.tvUnreadCount?.visibility = View.GONE
                }
            }catch (e:Exception){
                println(e)
            }
        }
        isCurrLocChoosed.observe(this) {
            try {
                it?.let {
               if(it) { requestPermissionForLocation() }
                }
            } catch (e:Exception){
                println(e)
            }
        }
        isDiffLocChoosed.observe(this) {
            try {
                it?.let {
               if(it) { getAddressFromLatLong(currLat, currLong) }
                }
            }catch (e:Exception){
                println(e)
            }
        }
    }

    private fun initViews() {
        activityHomeBinding?.inclLayoutNavDrawer?.run {
            homeViewModel?.appSharedPref?.let {
                txtSidemenuName.text = (if (it.userName.isNullOrBlank().not()) { it.userName ?: "" } else "").trim()
                txtSidemenueEmail.text = (if (it.userEmail.isNullOrBlank().not()) { it.userEmail ?: "" } else "").trim()
                txtSidemenueLocation.text = if (it.userCurrentLocation.isNullOrBlank().not()) { it.userCurrentLocation ?: "" } else ""
                tvhVersion.text = getAppVersionText()

                profileImage.setCircularRemoteImage(if (it.userImage.isNullOrBlank().not()) { it.userImage ?: "" } else "")
                activityHomeBinding?.appBarHomepage?.toolbarLayout?.toolbarAddMemberIvBack?.let {itiv ->
                    itiv.setHamburgerImage(if (it.userImage.isNullOrBlank().not()) { it.userImage ?: "" } else "") }
                isProfileUpdated.value = null

            }
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(brForNotificaiton, IntentFilter(Config.PUSH_NOTIFICATION))
        initViews()
        showSelectionOfBottomNavigationItem()
        setLocationOnHeader()
    }

    val brForNotificaiton = object : BroadcastReceiver() {
        override fun onReceive(contxt: Context?, intent: Intent?) {
            when (intent?.action) {
                Config.PUSH_NOTIFICATION -> hitNotificationUnread()
            }
        }
    }

    fun hitNotificationUnread() {
        try {
            val jsonObject = JsonObject().apply {
                addProperty("login_user_id", homeViewModel?.appSharedPref?.userId)
            }
            val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
            homeViewModel?.apiUnreadNotifications(body)
        } catch (e: Exception) {
            println(e)
        }

    }

    // Change once the api received
    override fun onSuccessUnreadNotification(commonResponse: NotificationCountResponse?) {
        this@HomeActivity.hideLoading()
        if (commonResponse?.code.equals(SUCCESS_CODE,ignoreCase = true)) {
            isUnreadNotficationAvailable.value = if(commonResponse?.result != null && commonResponse.result != 0) {
                activityHomeBinding?.appBarHomepage?.toolbarLayout?.
                toolbarNotification?.startAnimation(doAnimation(R.anim.shake))
                true } else false
        }
    }

     private fun setLocationOnHeader() {
        if(homeViewModel?.appSharedPref?.userCurrentLocation.isNullOrBlank().not()) {
            activityHomeBinding?.appBarHomepage?.toolbarLayout?.tvHomeLocation?.text = homeViewModel?.appSharedPref?.userCurrentLocation
        } else {
            requestPermissionForLocation()
        }

    }

    private fun drawerNavigationMenu() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setHomeButtonEnabled(false)

   //   val constraintLayout = findViewById<ConstraintLayout>(R.id.parent_layout)
        val actionBarDrawerToggle = object : ActionBarDrawerToggle(this, activityHomeBinding!!.drawerLayout, toolbar, R.string.app_name, R.string.app_name) {
            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
                hideKeyboard()
            }

            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
                     hideKeyboard()
            }

        }

        actionBarDrawerToggle.isDrawerIndicatorEnabled = false
        activityHomeBinding?.drawerLayout?.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        activityHomeBinding?.appBarHomepage?.toolbarLayout?.toolbarAddMemberIvBack?.setOnClickListener {
            toggleDrawer()
        }
        actionBarDrawerToggle.toolbarNavigationClickListener = View.OnClickListener {
            toggleDrawer()
        }

        activityHomeBinding?.appBarHomepage?.toolbarLayout?.toolbarBack?.setOnClickListener {
            onBackPressed()
        }
        activityHomeBinding?.appBarHomepage?.toolbarLayout?.toolbarNotification?.setOnClickListener {
            checkInBackstack(FragmentNotification.newInstance())
        }

    }

    private fun setDataAndSelectOptionInDrawerNavigation() {
        activityHomeBinding?.inclLayoutNavDrawer?.run {
            // appointment
            cnsAppointUpcoming.setOnClickListener {
                moveToAppointment(getString(R.string.upcoming_appointment),AppointmentTypes.UPCOMING.get())
            }
            cnsAppointOngoing.setOnClickListener {
                moveToAppointment(getString(R.string.ongoing_appointment),AppointmentTypes.ONGOING.get())
            }
            cnsAppointPast.setOnClickListener {
                moveToAppointment(getString(R.string.past_appointment),AppointmentTypes.PAST.get())
            }

            // profile setting
            profileImage.setOnClickListener { cnsProfileSetting.performClick() }
            profileImageCamera.setOnClickListener { cnsProfileSetting.performClick() }
            cnsProfileSetting.setOnClickListener {
                toggleDrawer()
                checkInBackstack(FragmentProfile.newInstance())
            }
            // Support and more
            cnsSupportMore.setOnClickListener {
                toggleDrawer()
                moveToSupportMore()
            }

            // Logout
            llLogout.setOnClickListener { toggleDrawer();  logout() }
        }
    }

    private val mOnNavigationItemSelectedListener =  BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    cartToHome()
                    return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_appointment -> {
                    openAppointmentfromBottomMenu()
                   return@OnNavigationItemSelectedListener true
                }
                R.id.navigation_cart -> {
                    openCartFromBottom()
                    return@OnNavigationItemSelectedListener true
                }

                R.id.navigation_more -> {
                // showMorePopup()
                 moveToSupportMore()
                return@OnNavigationItemSelectedListener true
            }
                else -> return@OnNavigationItemSelectedListener false
            }
        }

    private fun moveToSupportMore(showFrag: Boolean = false){
        try {
            if(showFrag) {
                checkInBackstack(FragmentSupportMore.newInstance())
                Handler(Looper.getMainLooper()).postDelayed({ showSelectionOfBottomNavigationItem() },100)
            } else {
                navigate<SupportAndMore>()
            }
        } catch (e:Exception){
            println(e)
        }
    }

    fun openCartFromBottom(){
        checkInBackstack(FragmentBookingCart.newInstance())
        Handler(Looper.getMainLooper()).postDelayed({ showSelectionOfBottomNavigationItem() }, 100)
    }

   fun openAppointmentfromBottomMenu(){
       appointmentHeading = getString(R.string.my_appointments)
       val fragment = supportFragmentManager.findFragmentById(R.id.layout_container)
       if (fragment is FragNewAppointmentListing) { fragment.ifAlreadyOpened(AppointmentTypes.ALL.get()) } else {
           checkInBackstack(FragNewAppointmentListing.newInstance(AppointmentTypes.ALL.get()))  }

       Handler(Looper.getMainLooper()).postDelayed({ showSelectionOfBottomNavigationItem() }, 100)
   }

    private fun moveToAppointment(title:String, AppType:String) {
        appointmentHeading = title
        toggleDrawer()
        //  checkInBackstack(FragmentAppointment.newInstance())
        checkInBackstack(FragNewAppointmentListing.newInstance(AppType))
    }

    private fun showMorePopup() {
        PopupMenu(ContextThemeWrapper(this, R.style.popupMenuStyle), findViewById(R.id.navigation_more)).apply {
          for (element in homeBottomMenuMoreOptions()) {
                menu.add(element)
           }
            setOnMenuItemClickListener { item ->
                when {
                    item.title.toString().equals(HomeBottomMenu.PROFILE.get(),ignoreCase = true) -> {
                        checkInBackstack(FragmentProfile.newInstance())
                        Handler(Looper.getMainLooper()).postDelayed({ showSelectionOfBottomNavigationItem() }, 100)
                    }
                }

                true
            }
            show()
        }


    }
    private fun toggleDrawer() {
        if (activityHomeBinding?.drawerLayout?.isDrawerVisible(GravityCompat.START) == true) {
            activityHomeBinding?.drawerLayout?.closeDrawer(GravityCompat.START)
        } else {
            activityHomeBinding?.drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    fun cartToHome() {
          try {
              checkInBackstack(HomeFragment.newInstance())
              Handler(Looper.getMainLooper()).postDelayed({showSelectionOfBottomNavigationItem()}, 100)
          } catch (e:Exception){
            println(e)
          }
      }

    fun showSelectionOfBottomNavigationItem() {
        val fragment = supportFragmentManager.findFragmentById(R.id.layout_container)
        // Uncheck all menu item
        val menu = activityHomeBinding!!.navigation.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            menuItem.isChecked = false
        }
        when (fragment) {
            is HomeFragment -> {
                menu.findItem(R.id.navigation_home).isChecked = true
            }
            is FragNewAppointmentListing -> {
                menu.findItem(R.id.navigation_appointment).isChecked = true
            }
            is FragmentBookingCart -> {
                menu.findItem(R.id.navigation_cart).isChecked = true
            }
            is FragmentSupportMore -> {
                menu.findItem(R.id.navigation_more).isChecked = true
            }
            else -> menu.findItem(R.id.navigation_more).isChecked = true
        }

        showTextInToolbarRelativeToFragment(fragment!!)
    }

    private fun showTextInToolbarRelativeToFragment(fragment: Fragment) {

        val appointmentSearch = activityHomeBinding!!.appBarHomepage.toolbarLayout.toolbarSearch
        val tootbar_text = activityHomeBinding!!.appBarHomepage.toolbarLayout.toolbarTitle
        val llHomeLocation = activityHomeBinding?.appBarHomepage?.toolbarLayout?.llHomeLocation
        val tootlebar_notification = activityHomeBinding?.appBarHomepage?.toolbarLayout?.toolbarNotification
        val toolbar_back = activityHomeBinding?.appBarHomepage?.toolbarLayout?.toolbarBack
        val toolbar_menu = activityHomeBinding?.appBarHomepage?.toolbarLayout?.toolbarAddMemberIvBack

        llHomeLocation?.visibility = View.GONE
        tootbar_text.visibility = View.VISIBLE
        llHomeLocation?.setOnClickListener(null)

        appointmentSearch.visibility = View.GONE
        appointmentSearch.setOnClickListener(null)

        when (fragment) {
            is HomeFragment -> {
                tootbar_text.text = getString(R.string.my_dashboard)
                tootlebar_notification?.visibility = View.VISIBLE
                toolbar_back?.visibility = View.GONE
                toolbar_menu?.visibility = View.VISIBLE

                tootbar_text.visibility = View.GONE
                llHomeLocation?.visibility = View.VISIBLE
                llHomeLocation?.setOnClickListener { navigate<ActivityLocationFetch>() }

            }
            is FragmentSupportMore -> {
                tootbar_text.text = getString(R.string.support_amp_more)
                tootlebar_notification?.visibility = View.VISIBLE
                toolbar_back?.visibility = View.VISIBLE
                toolbar_menu?.visibility = View.GONE
            }
            is FragmentProvderBooking -> {
                tootbar_text.text = getString(R.string.booking)
                tootlebar_notification?.visibility = View.VISIBLE
                toolbar_back?.visibility = View.VISIBLE
                toolbar_menu?.visibility = View.GONE
            }
            is FragmentProvderBookingForDoctor -> {
                tootbar_text.text = getString(R.string.booking)
                tootlebar_notification?.visibility = View.VISIBLE
                toolbar_back?.visibility = View.VISIBLE
                toolbar_menu?.visibility = View.GONE
            }

            is FragNewAppointmentListing -> {
                tootbar_text.text = appointmentHeading
                tootlebar_notification?.visibility = View.VISIBLE
                toolbar_back?.visibility = View.VISIBLE
                toolbar_menu?.visibility = View.GONE

                appointmentSearch.visibility = View.VISIBLE
                appointmentSearch.setOnClickListener { FragNewAppointmentListing.showSearch.value = true }
            }
            is FragmentProviderHospitalListing -> {
                tootbar_text.text = providerName
                tootlebar_notification?.visibility = View.VISIBLE
                toolbar_back?.visibility = View.VISIBLE
                toolbar_menu?.visibility = View.GONE
            }
            is FragmentProviderListing -> {
                tootbar_text.text = providerName
                tootlebar_notification?.visibility = View.VISIBLE
                toolbar_back?.visibility = View.VISIBLE
                toolbar_menu?.visibility = View.GONE
            }
            is FragmentProviderListingDetails -> {
                tootbar_text.text = providerName
                tootlebar_notification?.visibility = View.VISIBLE
                toolbar_back?.visibility = View.VISIBLE
                toolbar_menu?.visibility = View.GONE
            }
            is FragmentProfile -> {
                tootbar_text.text = getString(R.string.edit_account)

                tootlebar_notification?.visibility = View.VISIBLE
                toolbar_back?.visibility = View.VISIBLE
                toolbar_menu?.visibility = View.GONE
            }
            is FragmentAddPatient -> {
                //   drawerAdapter!!.selectItem(3)
                tootbar_text.text = getString(R.string.add_patient)


                tootlebar_notification?.visibility = View.VISIBLE
                toolbar_back?.visibility = View.VISIBLE
                toolbar_menu?.visibility = View.GONE

            }
            is FragmentBookingCart -> {
                tootbar_text.text = getString(R.string.cart_item)
                tootlebar_notification?.visibility = View.VISIBLE
                toolbar_back?.visibility = View.VISIBLE
                toolbar_card?.visibility = View.GONE
                toolbar_menu?.visibility = View.GONE

            }

            is FragmentNotification -> {
                tootbar_text.text = getString(R.string.notification)


                tootlebar_notification?.visibility = View.GONE
                toolbar_back?.visibility = View.VISIBLE
                toolbar_menu?.visibility = View.GONE

            }



        }

    }

    fun checkInBackstack(fragment: Fragment) {
        val nameFragmentInBackstack = fragment.javaClass.name
        val manager = supportFragmentManager
        val fragmentPopped = manager.popBackStackImmediate(nameFragmentInBackstack, 0)
        val ft = manager.beginTransaction()

        if (!fragmentPopped && manager.findFragmentByTag(nameFragmentInBackstack) == null) { //fragment not in back stack, create it.
            ft.replace(activityHomeBinding?.appBarHomepage?.layoutContainer?.id!!, fragment, nameFragmentInBackstack)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.addToBackStack(nameFragmentInBackstack)
            ft.commit()
        } else if (manager.findFragmentByTag(nameFragmentInBackstack) != null) {
            val currentFragment = manager.findFragmentByTag(nameFragmentInBackstack)
            ft.replace(activityHomeBinding?.appBarHomepage?.layoutContainer?.id!!, currentFragment!!, nameFragmentInBackstack)
            ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            ft.addToBackStack(nameFragmentInBackstack)
            ft.commit()
        }
        showTextInToolbarRelativeToFragment(fragment)
    }

    override fun onBackPressed() {
        if (activityHomeBinding!!.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            activityHomeBinding!!.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            if (supportFragmentManager.backStackEntryCount == 1 || supportFragmentManager.findFragmentById(R.id.layout_container) is HomeFragment) {
                if (checkForClose) {
                    finish()
                }
                checkForClose = true
                showToast(getString(R.string.click_again_to_exit))
                Handler(Looper.getMainLooper()).postDelayed({ checkForClose = false }, 2000)
            } else if (supportFragmentManager.findFragmentById(R.id.layout_container) is FragNewAppointmentListing) {
                cartToHome()
            }
            /* else if (supportFragmentManager.findFragmentById(R.id.layout_container) is FragmentProvderBooking) {
                CommonDialog.showDialog(this@HomeActivity, object : DialogClickCallback {
                    override fun onConfirm() {
                        supportFragmentManager.popBackStack()
                    }
                }, getString(R.string.warning_), getString(R.string.data_will_lost_on_back),getString(R.string.go_back))
            }*/
            else {
                super.onBackPressed()
                showSelectionOfBottomNavigationItem()
            }
        }


    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        airLocation.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        airLocation.onActivityResult(requestCode, resultCode, data)
        val fragment = supportFragmentManager.findFragmentById(activityHomeBinding?.appBarHomepage?.layoutContainer?.id!!)
        fragment?.onActivityResult(requestCode, resultCode, data)
    }

    private fun logout() {
        CommonDialog.showDialog(this@HomeActivity, object : DialogClickCallback {
            override fun onConfirm() {
                if (isNetworkConnected) {
                    this@HomeActivity.showLoading()
                    val appointmentRequest = AppointmentRequest()
                    appointmentRequest.userId = homeViewModel?.appSharedPref?.userId
                    homeViewModel?.apiLogout(appointmentRequest)
                } else {
                    getString(R.string.check_network_connection)
                }
           }

        }, getString(R.string.logout), getString(R.string.sure_to_logout_), getString(R.string.logout))
    }

    override fun reloadPageAfterConnectedToInternet() {
        showSelectionOfBottomNavigationItem()
    }


    override fun onResume() {
        super.onResume()
        val menu: Menu = navigation.menu
        for (i in 0 until menu.size()) {
            val menuItem = menu.getItem(i)
            Log.d(TAG, "onResume: " + menu.getItem(i))
        }
    }

    override fun successLogoutResponse(commonResponse: CommonResponse?) {
        this@HomeActivity.hideLoading()
        if (commonResponse?.code.equals(SUCCESS_CODE,ignoreCase = true)) {
            homeViewModel?.appSharedPref?.deleteUserId()
            homeViewModel?.appSharedPref?.deleteIsLogINRemember()
            homeViewModel?.appSharedPref?.deleteIsAppointmentType()
            startActivity(LoginActivity.newIntent(this@HomeActivity))
            finishAffinity()
        } else {
            Toast.makeText(this@HomeActivity, commonResponse?.message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun errorLogoutResponse(throwable: Throwable?) {
        this@HomeActivity.hideLoading()
        if (throwable?.message != null) {
           showToast(getString(R.string.something_went_wrong))
        }
    }

    fun requestPermissionForLocation() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED -> {
                    // You can use the API that requires the permission.
//                Permission is granted Normal app flow
                 startGettingCurrenttLocation()

                }
                ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION ) -> {
                    // In an educational UI, explain to the user why your app requires this
                    // permission for a specific feature to behave as expected. In this UI,
                    // include a "cancel" or "no thanks" button that allows the user to
                    // continue using your app without granting the permission.
                    this.showAlertWithSystemSetting(getString(R.string.enable_location_permission)) // "You have disabled Mic Permission. Please enable it"
                }
                else -> {
                    // You can directly ask for the permission.
                    // The registered ActivityResultCallback gets the result of this request.
                    requestAudioPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }
            }

        }
    }

    private val requestAudioPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                startGettingCurrenttLocation()
            } else {
                // Explain to the user that the feature is unavailable because the
                // features requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Log.e("Per", " new launcher: not Granted")
                this.showAlertWithSystemSetting(getString(R.string.enable_location_permission))
            }
        }

  private fun startGettingCurrenttLocation() {
       try {
           if (isNetworkConnected) {
               airLocation.start()
           } else {
               getString(R.string.check_network_connection)
           }

       } catch (e:Exception){
           println(e)
       }

    }

    private val airLocation = AirLocation(this, object : AirLocation.Callback {
        override fun onSuccess(locations: ArrayList<Location>) {
            try {
                for (location in locations) {
                    currLat = location.latitude
                    currLong = location.longitude

                    println("${location.longitude},${location.latitude}")
                    getAddressFromLatLong(location.latitude, location.longitude)
                }
            }catch (e:Exception){
                println(e)
            }
        }
        override fun onFailure(locationFailedEnum: AirLocation.LocationFailedEnum) {
//            Toast.makeText(this@HomeActivity, locationFailedEnum.name, Toast.LENGTH_SHORT).show()
        }
    }, true)


    fun getAddressFromLatLong(latitude: Double, longitude: Double,showLoading:Boolean = false) {
        try {
            if (isNetworkConnected) {
                currLat = latitude
                currLong = longitude

           //     val addresses: List<Address>
           //     val geocoder = Geocoder(this@HomeActivity, Locale.getDefault())
             //   addresses = geocoder.getFromLocation(latitude, longitude, 1) // 1 to 5
              //  val address: String = addresses.getOrNull(0)?.getAddressLine(0) ?: ""
             //   Log.wtf("Addr", "getAddressFromLatLong: $address")

                   //    activityHomeBinding?.inclLayoutNavDrawer?.txtSidemenueLocation?.text = address
                    // call the api in the background
                    val jsonObject = JsonObject().apply {
                        addProperty("user_id", homeViewModel?.appSharedPref?.userId)
                        addProperty("lat", latitude.toString())
                        addProperty("lng", longitude.toString())
                    }
                    if(isCurrLocChoosed.value == true || isDiffLocChoosed.value == true) showLoading()
                    val body = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull())
                    homeViewModel?.apiUpdateCurrentLocation(body)

            } else {
                getString(R.string.check_network_connection)
            }

        } catch (e: SocketException) {
            showToast(getString(R.string.check_network_connection))
        }
        catch (e:Exception){
            println(e)
        }


//        val city: String = addresses[0].getLocality()
//        val state: String = addresses[0].getAdminArea()
//        val country: String = addresses[0].getCountryName()
//        val postalCode: String = addresses[0].getPostalCode()
//        val knownName: String = addresses[0].getFeatureName() // Only if available else return NULL
    }

    override fun onSuccessUpdateLocation(response: ModelUpdateCurrentLocation?) {
        isCurrLocChoosed.value=false;   isDiffLocChoosed.value = false
        hideLoading()

        if(response?.code.equals(SUCCESS_CODE,ignoreCase = true)) {
            homeViewModel?.appSharedPref?.userCurrentLocation = response?.result?.current_address ?: ""
            activityHomeBinding?.appBarHomepage?.toolbarLayout?.tvHomeLocation?.text = response?.result?.current_address.orEmpty()
            activityHomeBinding?.inclLayoutNavDrawer?.txtSidemenueLocation?.text = response?.result?.current_address ?: ""
        } else {
            startGettingCurrenttLocation()
        }
    }

    override fun onStop() {
        super.onStop()
        LocalBroadcastManager.getInstance(this)
            .unregisterReceiver(brForNotificaiton)
    }
}
