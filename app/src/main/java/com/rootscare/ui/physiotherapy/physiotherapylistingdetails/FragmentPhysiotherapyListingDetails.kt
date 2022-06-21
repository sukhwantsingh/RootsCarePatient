package com.rootscare.ui.physiotherapy.physiotherapylistingdetails

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.nurse.nursedetailsrequest.NurseDetailsRequest
import com.rootscare.data.model.api.request.nurse.nursrtask.PhysiotherapyTask
import com.rootscare.data.model.api.response.nurses.nursedetails.*
import com.rootscare.data.model.api.response.nurses.taskresponse.GetTaskResponse
import com.rootscare.data.model.api.response.nurses.taskresponse.ResultItem
import com.rootscare.databinding.FragmentPhysiotherapyListingDetailsBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.nurses.nursesbookingappointment.FragmentNursesBookingAppointment
import com.rootscare.ui.nurses.nurseslistingdetails.adapter.*
import com.rootscare.ui.nurses.review.FragmentNurseReviewSubmit
import com.rootscare.ui.physiotherapy.bookingappointment.FragmentPhysiotherapyBookingAppointment
import com.rootscare.ui.physiotherapy.physiotherapylistingdetails.adapter.AdapterPhysiotherapyTaskListRecyclerview
import com.rootscare.ui.profile.FragmentProfile
import com.rootscare.utilitycommon.BaseMediaUrls
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class FragmentPhysiotherapyListingDetails :
    BaseFragment<FragmentPhysiotherapyListingDetailsBinding, FragmentPhysiotherapyListingDetailsViewModel>(),
    FragmentPhysiotherapyListingDetailsNavigator {
    private var fragmentNursesListingDetailsBinding: FragmentPhysiotherapyListingDetailsBinding? =
        null
    private var fragmentNursesListingDetailsViewModel: FragmentPhysiotherapyListingDetailsViewModel? =
        null
    var nurseFirstName = ""
    var nurseLastName = ""
    var nurseId = ""
    var currentDate = ""
    private var hidden = true
    private var initialReviewRatingList: ArrayList<ReviewRatingItem?>? = null
    var finalReviewRatingList: ArrayList<ReviewRatingItem?>? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_physiotherapy_listing_details
    override val viewModel: FragmentPhysiotherapyListingDetailsViewModel
        get() {
            fragmentNursesListingDetailsViewModel =
                ViewModelProviders.of(this)
                    .get(FragmentPhysiotherapyListingDetailsViewModel::class.java)
            return fragmentNursesListingDetailsViewModel as FragmentPhysiotherapyListingDetailsViewModel
        }

    companion object {
        fun newInstance(nurseid: String): FragmentPhysiotherapyListingDetails {
            val args = Bundle()
            args.putString("nurseid", nurseid)
            val fragment = FragmentPhysiotherapyListingDetails()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentNursesListingDetailsViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentNursesListingDetailsBinding = viewDataBinding
        if (arguments != null && arguments?.getString("nurseid") != null) {
            nurseId = arguments?.getString("nurseid")!!
            Log.d("Nurse ID", ": $nurseId")
        }

        val c = Calendar.getInstance().time
        println("Current time => $c")

        val df = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
        val formattedDate = df.format(c)
        currentDate = formattedDate
        //Api hit for nurse details
        apiHitForNurseViewTiming()
        apiHitForNurseDetails()
        fragmentNursesListingDetailsBinding?.btnRootscareBookingNurses?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentNursesBookingAppointment.newInstance(nurseId)
            )
        }

        fragmentNursesListingDetailsBinding?.btnBookingAppointment?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentPhysiotherapyBookingAppointment.newInstance(nurseId)
            )
        }
        fragmentNursesListingDetailsBinding?.txtNurseSubmitReview?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentNurseReviewSubmit.newInstance(nurseId)
            )
        }

        fragmentNursesListingDetailsBinding?.txtNursedetaisheaderReviewWrite?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentNurseReviewSubmit.newInstance(nurseId)
            )
        }
        fragmentNursesListingDetailsBinding?.txtNursedetailsViewTimings?.setOnClickListener {
            if (hidden) {
                fragmentNursesListingDetailsBinding?.llNurseViewTiming?.visibility = View.VISIBLE
                val animSlideDown = AnimationUtils.loadAnimation(
                    activity?.applicationContext,
                    R.anim.slide_down
                )
                fragmentNursesListingDetailsBinding?.llNurseViewTiming?.startAnimation(animSlideDown)
                hidden = false
                fragmentNursesListingDetailsBinding?.txtNursedetailsViewTimings?.text =
                    "Close Slots"
            } else {
                val animSlideUp: Animation =
                    AnimationUtils.loadAnimation(activity?.applicationContext, R.anim.slide_up)
                fragmentNursesListingDetailsBinding?.llNurseViewTiming?.startAnimation(animSlideUp)
                fragmentNursesListingDetailsBinding?.llNurseViewTiming?.visibility = View.GONE
                hidden = true
                fragmentNursesListingDetailsBinding?.txtNursedetailsViewTimings?.text = "View Slots"
            }

        }

        fragmentNursesListingDetailsBinding?.imgClose?.setOnClickListener {
            val animSlideUp: Animation =
                AnimationUtils.loadAnimation(activity?.applicationContext, R.anim.slide_up)
            fragmentNursesListingDetailsBinding?.llNurseViewTiming?.startAnimation(animSlideUp)
            fragmentNursesListingDetailsBinding?.llNurseViewTiming?.visibility = View.GONE
        }

        fragmentNursesListingDetailsBinding?.txtNursedetailsReviewMore?.setOnClickListener {
            if (fragmentNursesListingDetailsBinding?.txtNursedetailsReviewMore?.text!! == "More..") {
                if (finalReviewRatingList != null && finalReviewRatingList!!.size > 0) {
                    setReviewRatingListing(finalReviewRatingList)
                    fragmentNursesListingDetailsBinding?.txtNursedetailsReviewMore?.text = "Less.."
                }
            } else if (fragmentNursesListingDetailsBinding?.txtNursedetailsReviewMore?.text!! == "Less.."
            ) {
                if (initialReviewRatingList != null && initialReviewRatingList!!.size > 0) {
                    setReviewRatingListing(initialReviewRatingList)
                    fragmentNursesListingDetailsBinding?.txtNursedetailsReviewMore?.text = "More.."
                }
            }
        }


    }

    // Set up recycler view for service listing if available
    private fun setUpViewNursesFeeslistingRecyclerview(hourlyRatesList: ArrayList<HourlyRatesItem?>?) {
//        assert(fragmentNursesListingDetailsBinding!!.recyclerViewRootscareNursesfeesListing != null)
        val recyclerView =
            fragmentNursesListingDetailsBinding!!.recyclerViewRootscareNursesfeesListing
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterNursesFeesListingRecyclerView(hourlyRatesList, context!!)
        recyclerView.adapter = contactListAdapter

    }

    override fun successNurseDetailsResponse(nurseDetailsResponse: NurseDetailsResponse?) {
        baseActivity?.hideLoading()
        if (nurseDetailsResponse?.code.equals("200")) {
            nurseFirstName =
                if (nurseDetailsResponse?.result?.firstName != null && nurseDetailsResponse.result.firstName != ""
                ) {
                    nurseDetailsResponse.result.firstName
                } else {
                    ""
                }

            nurseLastName =
                if (nurseDetailsResponse?.result?.lastName != null && nurseDetailsResponse.result.lastName != ""
                ) {
                    nurseDetailsResponse.result.lastName
                } else {
                    ""
                }

            fragmentNursesListingDetailsBinding?.txtNursedetaisName?.text =
                "$nurseFirstName $nurseLastName"

            if (nurseDetailsResponse?.result?.qualification != null && nurseDetailsResponse.result.qualification != ""
            ) {
                fragmentNursesListingDetailsBinding?.txtNursedetaisQualification?.text =
                    nurseDetailsResponse.result.qualification
            } else {
                fragmentNursesListingDetailsBinding?.txtNursedetaisQualification?.text = ""
            }

            if (nurseDetailsResponse?.result?.reviewAbility != null && nurseDetailsResponse.result.reviewAbility != ""
            ) {
                if (nurseDetailsResponse.result.reviewAbility == "yes") {
                    fragmentNursesListingDetailsBinding?.txtNursedetaisheaderReviewWrite?.visibility =
                        View.VISIBLE
                    fragmentNursesListingDetailsBinding?.txtNurseSubmitReview?.visibility =
                        View.VISIBLE

                } else if (nurseDetailsResponse.result.reviewAbility == "no") {
                    fragmentNursesListingDetailsBinding?.txtNursedetaisheaderReviewWrite?.visibility =
                        View.GONE
                    fragmentNursesListingDetailsBinding?.txtNurseSubmitReview?.visibility =
                        View.GONE
                }
            }

            if (nurseDetailsResponse?.result?.avgRating != null && nurseDetailsResponse.result.avgRating != ""
            ) {
                fragmentNursesListingDetailsBinding?.ratingBardoctordetailseview?.rating =
                    nurseDetailsResponse.result.avgRating.toFloat()
//            } else {

            }
            if (nurseDetailsResponse?.result?.reviewRating != null && nurseDetailsResponse.result.reviewRating.size > 0) {
                fragmentNursesListingDetailsBinding?.txtNursedetailsNoofreview?.visibility =
                    View.VISIBLE
                fragmentNursesListingDetailsBinding?.txtNursedetailsNoofreview?.text =
                    nurseDetailsResponse.result.reviewRating.size.toString() + " " + "reviews"
            } else {
                fragmentNursesListingDetailsBinding?.txtNursedetailsNoofreview?.visibility =
                    View.GONE
            }
            if (nurseDetailsResponse?.result?.description != null && nurseDetailsResponse.result.description != ""
            ) {
                fragmentNursesListingDetailsBinding?.txtNursedetaisAddressDescription?.text =
                    nurseDetailsResponse.result.description
            } else {
                fragmentNursesListingDetailsBinding?.txtNursedetaisAddressDescription?.text = ""
            }

            if (nurseDetailsResponse?.result?.hourlyRates != null && nurseDetailsResponse.result.hourlyRates.size > 0) {
                fragmentNursesListingDetailsBinding?.recyclerViewRootscareNursesfeesListing?.visibility =
                    View.VISIBLE
                fragmentNursesListingDetailsBinding?.tvNoDateNursefeeslist?.visibility = View.GONE
                setUpViewNursesFeeslistingRecyclerview(nurseDetailsResponse.result.hourlyRates)
            } else {
                fragmentNursesListingDetailsBinding?.recyclerViewRootscareNursesfeesListing?.visibility =
                    View.GONE
                fragmentNursesListingDetailsBinding?.tvNoDateNursefeeslist?.visibility =
                    View.VISIBLE
                fragmentNursesListingDetailsBinding?.tvNoDateNursefeeslist?.text = "No fees found."
            }

            if (nurseDetailsResponse?.result?.qualificationData != null && nurseDetailsResponse.result.qualificationData.size > 0) {
                fragmentNursesListingDetailsBinding?.recyclerViewNursedetailsEducation?.visibility =
                    View.VISIBLE
                fragmentNursesListingDetailsBinding?.tvNursedetailsEducationNoDate?.visibility =
                    View.GONE
                setQualificationDataListing(nurseDetailsResponse.result.qualificationData)
            } else {
                fragmentNursesListingDetailsBinding?.recyclerViewNursedetailsEducation?.visibility =
                    View.GONE
                fragmentNursesListingDetailsBinding?.tvNursedetailsEducationNoDate?.visibility =
                    View.VISIBLE
                fragmentNursesListingDetailsBinding?.tvNursedetailsEducationNoDate?.text =
                    "No qualification data found."
            }

            initialReviewRatingList = ArrayList()
            finalReviewRatingList = ArrayList()

            if (nurseDetailsResponse?.result?.reviewRating != null && nurseDetailsResponse.result.reviewRating.size > 0) {
                fragmentNursesListingDetailsBinding?.recyclerViewNurselistingReview?.visibility =
                    View.VISIBLE
                fragmentNursesListingDetailsBinding?.tvNurselistingReviewNoDate?.visibility =
                    View.GONE
                finalReviewRatingList = nurseDetailsResponse.result.reviewRating
                if (nurseDetailsResponse.result.reviewRating.size > 1) {
                    fragmentNursesListingDetailsBinding?.txtNursedetailsReviewMore?.visibility =
                        View.VISIBLE
                    val reviewRatingItem = ReviewRatingItem()
                    reviewRatingItem.rating =
                        nurseDetailsResponse.result.reviewRating[0]?.rating
                    reviewRatingItem.review =
                        nurseDetailsResponse.result.reviewRating[0]?.review
                    reviewRatingItem.reviewBy =
                        nurseDetailsResponse.result.reviewRating[0]?.reviewBy
                    initialReviewRatingList?.add(reviewRatingItem)
                    setReviewRatingListing(initialReviewRatingList)
                } else {
                    fragmentNursesListingDetailsBinding?.txtNursedetailsReviewMore?.visibility =
                        View.GONE
                    finalReviewRatingList = ArrayList()
                    for (i in 0 until nurseDetailsResponse.result.reviewRating.size) {
                        val reviewRatingItem = ReviewRatingItem()
                        reviewRatingItem.rating =
                            nurseDetailsResponse.result.reviewRating[0]?.rating
                        reviewRatingItem.review =
                            nurseDetailsResponse.result.reviewRating[0]?.review
                        reviewRatingItem.reviewBy =
                            nurseDetailsResponse.result.reviewRating[0]?.reviewBy
                        finalReviewRatingList?.add(reviewRatingItem)
                        setReviewRatingListing(finalReviewRatingList)
                    }
                }
                //  setReviewRatingListing(nurseDetailsResponse?.result?.reviewRating)
            } else {
                fragmentNursesListingDetailsBinding?.recyclerViewNurselistingReview?.visibility =
                    View.GONE
                fragmentNursesListingDetailsBinding?.tvNurselistingReviewNoDate?.visibility =
                    View.VISIBLE
                fragmentNursesListingDetailsBinding?.tvNurselistingReviewNoDate?.text =
                    "No review found."
            }

            if (nurseDetailsResponse?.result?.image != null && nurseDetailsResponse.result.image != ""
            ) {

                Glide.with(this.activity!!)
                    .load(BaseMediaUrls.USERIMAGE.url + (nurseDetailsResponse.result.image))
                    .into(fragmentNursesListingDetailsBinding?.imgNursedetailsProfilephoto!!)
            } else {
                Glide.with(this.activity!!)
                    .load(R.drawable.no_image)
                    .into(fragmentNursesListingDetailsBinding?.imgNursedetailsProfilephoto!!)
            }


        } else {
            Toast.makeText(activity, nurseDetailsResponse?.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun successNurseViewTimingsResponse(getTaskResponse: GetTaskResponse?) {
        baseActivity?.hideLoading()
        if (getTaskResponse?.code.equals("200")) {
            if (getTaskResponse?.result != null && getTaskResponse.result.size > 0) {
                fragmentNursesListingDetailsBinding?.recyclerViewNursedetailsSpecility?.visibility =
                    View.VISIBLE
                fragmentNursesListingDetailsBinding?.tvNursedetailsSpecilityNoDate?.visibility =
                    View.GONE
                setNurseViewTimingListing(getTaskResponse.result)
            } else {
                fragmentNursesListingDetailsBinding?.recyclerViewNursedetailsSpecility?.visibility =
                    View.GONE
                fragmentNursesListingDetailsBinding?.tvNursedetailsSpecilityNoDate?.visibility =
                    View.VISIBLE
                fragmentNursesListingDetailsBinding?.tvNursedetailsSpecilityNoDate?.text =
                    "No tasks found."
            }

        } else {
            fragmentNursesListingDetailsBinding?.recyclerViewNursetiming?.visibility = View.GONE
            fragmentNursesListingDetailsBinding?.tvNoDateNursetiming?.visibility = View.VISIBLE
            fragmentNursesListingDetailsBinding?.tvNoDateNursetiming?.text = "No tasks found."
        }
    }

    override fun errorNurseDetailsResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }


    // Set up recycler view for service listing if available
    private fun setQualificationDataListing(qualificationDataList: ArrayList<QualificationDataItem?>?) {
//        assert(fragmentNursesListingDetailsBinding!!.recyclerViewNursedetailsEducation != null)
        val recyclerView = fragmentNursesListingDetailsBinding!!.recyclerViewNursedetailsEducation
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter =
            AdapterNurseEducationListRecyclerView(qualificationDataList, context!!)
        recyclerView.adapter = contactListAdapter
    }

    // Set up recycler view for service listing if available
    private fun setSpecialityDataListing(departmentList: ArrayList<DepartmentItem?>?) {
//        assert(fragmentNursesListingDetailsBinding!!.recyclerViewNursedetailsSpecility != null)
        val recyclerView = fragmentNursesListingDetailsBinding!!.recyclerViewNursedetailsSpecility
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterNurseSpecilityListRecyclerview(departmentList, context!!)
        recyclerView.adapter = contactListAdapter
    }

    // Set up recycler view for service listing if available
    private fun setReviewRatingListing(reviewRatingList: ArrayList<ReviewRatingItem?>?) {
//        assert(fragmentNursesListingDetailsBinding!!.recyclerViewNurselistingReview != null)
        val recyclerView = fragmentNursesListingDetailsBinding!!.recyclerViewNurselistingReview
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterNurseReviewrecyclerview(reviewRatingList, context!!)
        recyclerView.adapter = contactListAdapter
    }

    //Setup recyclerview for nurse view timing recyclerview
    private fun setNurseViewTimingListing(nurseTimingList: ArrayList<ResultItem?>?) {
//        assert(fragmentNursesListingDetailsBinding!!.recyclerViewNursetiming != null)
        val recyclerView = fragmentNursesListingDetailsBinding!!.recyclerViewNursedetailsSpecility
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter =
            AdapterPhysiotherapyTaskListRecyclerview(nurseTimingList, requireContext())
        recyclerView.adapter = contactListAdapter
    }

    fun apiHitForNurseDetails() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val nurseDetailsRequest = NurseDetailsRequest()
            nurseDetailsRequest.id = nurseId.toInt()
            nurseDetailsRequest.userId =
                fragmentNursesListingDetailsViewModel?.appSharedPref?.userId?.toInt()

            fragmentNursesListingDetailsViewModel?.apiPhysiotherapyDetails(nurseDetailsRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    fun apiHitForNurseViewTiming() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val physiotherapyTask = PhysiotherapyTask()
            physiotherapyTask.physiotherapyId = nurseId
            fragmentNursesListingDetailsViewModel?.physiotherapyTaskList(physiotherapyTask)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }
}