package com.rootscare.ui.hospital.hospitallistdetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.hospital.HospitalRequest
import com.rootscare.data.model.api.response.hospitalallapiresponse.hospitaldetailsresponse.DepartmentItem
import com.rootscare.data.model.api.response.hospitalallapiresponse.hospitaldetailsresponse.HospitalDetailsResponse
import com.rootscare.data.model.api.response.hospitalallapiresponse.hospitaldetailsresponse.ServiceDataItem
import com.rootscare.databinding.FragmentHospitalListDetailsBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.hospital.hospitallistdetails.adapter.AdapterHospitalPhotoRecyclerView
import com.rootscare.ui.hospital.hospitallistdetails.adapter.AdapterHospitalServiceListRecyclerview
import com.rootscare.ui.hospital.hospitallistdetails.adapter.AdapterNurseReviewrecyclerview
import com.rootscare.ui.hospital.hospitallistdetails.adapter.AdapterNurseSpecilityListRecyclerview
import com.rootscare.ui.lab.FragmentUpdateLabBookingAppointment
import com.rootscare.ui.physiotherapy.submitreviewforservice.FragmentSubmitReview
import com.rootscare.ui.profile.FragmentProfile
import com.rootscare.ui.seealldoctorbygridHospital.FragmentSeeAllDoctorHospitalByGrid
import com.rootscare.utilitycommon.BaseMediaUrls


class FragmentHospitalListDetails :
    BaseFragment<FragmentHospitalListDetailsBinding, FragmentHospitalListDetailsViewModel>(),
    FragmentHospitalListDetailsNavigator {
    private var fragmentHospitalListDetailsBinding: FragmentHospitalListDetailsBinding? =
        null
    private var fragmentHospitalListDetailsViewModel: FragmentHospitalListDetailsViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    var nurseId = ""
    var nurseImage = ""
    private var hospitalId = ""
    private var nurseFirstName = ""
    private var latitude: Double? = null
    private var longitude: Double? = null
    var address = ""
    private var initialReviewRatingList: ArrayList<com.rootscare.data.model.api.response.hospitalallapiresponse.hospitaldetailsresponse.ReviewRatingItem?>? =
        null
    private var finalReviewRatingList: ArrayList<com.rootscare.data.model.api.response.hospitalallapiresponse.hospitaldetailsresponse.ReviewRatingItem?>? =
        null
    override val layoutId: Int
        get() = R.layout.fragment_hospital_list_details
    override val viewModel: FragmentHospitalListDetailsViewModel
        get() {
            fragmentHospitalListDetailsViewModel =
                ViewModelProviders.of(this).get(FragmentHospitalListDetailsViewModel::class.java)
            return fragmentHospitalListDetailsViewModel as FragmentHospitalListDetailsViewModel
        }

    companion object {
        fun newInstance(nurseId: String): FragmentHospitalListDetails {
            val args = Bundle()
            args.putString("nurseid", nurseId)
            val fragment = FragmentHospitalListDetails()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentHospitalListDetailsViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentHospitalListDetailsBinding = viewDataBinding
        if (arguments != null && arguments?.getString("nurseid") != null) {
            nurseId = arguments?.getString("nurseid")!!
            Log.d("Nurse ID", ": $nurseId")
        }
        setUpHospitalImageListingRecyclerview()
        apiHitForNurseDetails()
        // setUpHospitalServiceListingRecyclerview()
//        fragmentSeeAllHospitalListBinding?.btnRootscareMoreHospita?.setOnClickListener(View.OnClickListener {
//            (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                FragmentHospitalCategoryList.newInstance())
//        })

        fragmentHospitalListDetailsBinding?.btnDoctorBooking?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentSeeAllDoctorHospitalByGrid.newInstance(nurseId)
            )
        }
        fragmentHospitalListDetailsBinding?.btnLabBooking?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentUpdateLabBookingAppointment.newInstance(nurseId, nurseImage, hospitalId)
            )
        }

        fragmentHospitalListDetailsBinding?.txtWriteYourReview?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentSubmitReview.newInstance()
            )
        }

        fragmentHospitalListDetailsBinding?.txtLocationAndDirection?.setOnClickListener {
            val intent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/maps?daddr=$latitude,$longitude")
            )
            startActivity(intent)
        }

    }

    // Set up recycler view for service listing if available
    private fun setUpHospitalImageListingRecyclerview() {
//        trainerList: ArrayList<TrainerListItem?>?
//        assert(fragmentHospitalListDetailsBinding!!.recyclerViewRootscareHospitalImage != null)
        val recyclerView = fragmentHospitalListDetailsBinding!!.recyclerViewRootscareHospitalImage
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter = AdapterHospitalPhotoRecyclerView(context!!)
        recyclerView.adapter = contactListAdapter
//        contactListAdapter?.recyclerViewItemClickWithView= object : OnItemClikWithIdListener {
//            override fun onItemClick(id: Int) {
//                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                    FragmentAppiontmentDetails.newInstance())
//            }
//
//        }

    }


    // Set up recycler view for service listing if available
/*
    private fun setUpHospitalServiceListingRecyclerview() {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentHospitalListDetailsBinding!!.recyclerViewRootscareHospitalService != null)
        val recyclerView = fragmentHospitalListDetailsBinding!!.recyclerViewRootscareHospitalService
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.HORIZONTAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter = AdapterHospitalServiceListingRecyclerview(context!!)
        recyclerView.adapter = contactListAdapter
//        contactListAdapter?.recyclerViewItemClickWithView= object : OnItemClikWithIdListener {
//            override fun onItemClick(id: Int) {
//                (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                    FragmentAppiontmentDetails.newInstance())
//            }
//
//        }

    }
*/
    override fun successNurseDetailsResponse(nurseDetailsResponse: HospitalDetailsResponse?) {
        baseActivity?.hideLoading()
        if (nurseDetailsResponse?.code.equals("200")) {
            hospitalId =
                if (nurseDetailsResponse?.result?.hospital_id != null && nurseDetailsResponse.result.hospital_id != ""
                ) {
                    nurseDetailsResponse.result.hospital_id
                } else {
                    ""
                }
            nurseFirstName =
                if (nurseDetailsResponse?.result?.name != null && nurseDetailsResponse.result.name != ""
                ) {
                    nurseDetailsResponse.result.name
                } else {
                    ""
                }

            fragmentHospitalListDetailsBinding?.name?.text = nurseFirstName

            nurseFirstName =
                if (nurseDetailsResponse?.result?.name != null && nurseDetailsResponse.result.name != ""
                ) {
                    nurseDetailsResponse.result.name
                } else {
                    ""
                }

            fragmentHospitalListDetailsBinding?.txtName?.text = nurseFirstName
            address =
                if (nurseDetailsResponse?.result?.address != null && nurseDetailsResponse.result.address != ""
                ) {
                    nurseDetailsResponse.result.address
                } else {
                    ""
                }

            fragmentHospitalListDetailsBinding?.txtaddress?.text = address



            if (nurseDetailsResponse?.result?.reviewAbility != null && nurseDetailsResponse.result.reviewAbility != ""
            ) {
                if (nurseDetailsResponse.result.reviewAbility == "yes") {
                    fragmentHospitalListDetailsBinding?.readWrite?.visibility = View.VISIBLE
                    fragmentHospitalListDetailsBinding?.txtWriteYourReview?.visibility =
                        View.VISIBLE

                } else if (nurseDetailsResponse.result.reviewAbility == "no") {
                    fragmentHospitalListDetailsBinding?.readWrite?.visibility = View.GONE
                    fragmentHospitalListDetailsBinding?.txtWriteYourReview?.visibility = View.GONE
                }
            }

            if (nurseDetailsResponse?.result?.avgRating != null && nurseDetailsResponse.result.avgRating != ""
            ) {
                fragmentHospitalListDetailsBinding?.ratingBardoctordetailseview?.rating =
                    nurseDetailsResponse.result.avgRating.toFloat()
//            } else {

            }
            if (nurseDetailsResponse?.result?.reviewRating != null && nurseDetailsResponse.result.reviewRating.size > 0) {
                fragmentHospitalListDetailsBinding?.txtDoctordetailsNoofreview?.visibility =
                    View.VISIBLE
                fragmentHospitalListDetailsBinding?.txtDoctordetailsNoofreview?.text =
                    nurseDetailsResponse.result.reviewRating.size.toString() + " " + "reviews"
            } else {
                fragmentHospitalListDetailsBinding?.txtDoctordetailsNoofreview?.visibility =
                    View.GONE
            }


            if (nurseDetailsResponse?.result?.department != null && nurseDetailsResponse.result.department.size > 0) {
                fragmentHospitalListDetailsBinding?.recyclerViewRootscareHospitalDepart?.visibility =
                    View.VISIBLE
                // fragmentHospitalListDetailsBinding?.tvNursedetailsSpecilityNoDate?.visibility=View.GONE
                setSpecialityDataListing(nurseDetailsResponse.result.department)
            } else {
                fragmentHospitalListDetailsBinding?.recyclerViewRootscareHospitalDepart?.visibility =
                    View.GONE
                // fragmentHospitalListDetailsBinding?.tvNursedetailsSpecilityNoDate?.visibility=View.VISIBLE
                //  fragmentHospitalListDetailsBinding?.tvNursedetailsSpecilityNoDate?.setText("No specility data found.")
            }

            if (nurseDetailsResponse?.result?.service != null && nurseDetailsResponse.result.service.size > 0) {
                fragmentHospitalListDetailsBinding?.recyclerViewRootscareHospitalService?.visibility =
                    View.VISIBLE
                // fragmentHospitalListDetailsBinding?.tvNursedetailsSpecilityNoDate?.visibility=View.GONE
                setServiceDataListing(nurseDetailsResponse.result.service)
            } else {
                fragmentHospitalListDetailsBinding?.recyclerViewRootscareHospitalService?.visibility =
                    View.GONE
                // fragmentHospitalListDetailsBinding?.tvNursedetailsSpecilityNoDate?.visibility=View.VISIBLE
                //  fragmentHospitalListDetailsBinding?.tvNursedetailsSpecilityNoDate?.setText("No specility data found.")
            }

            if (nurseDetailsResponse?.result?.latitude != null && nurseDetailsResponse.result.longitude != null) {
                latitude = nurseDetailsResponse.result.latitude.toDouble()
                longitude = nurseDetailsResponse.result.longitude.toDouble()
            } else {
                latitude = 22.572646
                longitude = 88.3638950
            }

            initialReviewRatingList = ArrayList()
            finalReviewRatingList = ArrayList()

            if (nurseDetailsResponse?.result?.reviewRating != null && nurseDetailsResponse.result.reviewRating.size > 0) {
                fragmentHospitalListDetailsBinding?.recyclerViewNurselistingReview?.visibility =
                    View.VISIBLE
                fragmentHospitalListDetailsBinding?.tvNurselistingReviewNoDate?.visibility =
                    View.GONE
                finalReviewRatingList = nurseDetailsResponse.result.reviewRating
                if (nurseDetailsResponse.result.reviewRating.size > 1) {
                    fragmentHospitalListDetailsBinding?.txtNursedetailsReviewMore?.visibility =
                        View.GONE
                    val reviewRatingItem =
                        com.rootscare.data.model.api.response.hospitalallapiresponse.hospitaldetailsresponse.ReviewRatingItem()
                    reviewRatingItem.rating =
                        nurseDetailsResponse.result.reviewRating[0]?.rating
                    reviewRatingItem.review =
                        nurseDetailsResponse.result.reviewRating[0]?.review
                    reviewRatingItem.reviewBy =
                        nurseDetailsResponse.result.reviewRating[0]?.reviewBy
                    initialReviewRatingList?.add(reviewRatingItem)
//                    setReviewRatingListing(initialReviewRatingList)
                    setReviewRatingListing(nurseDetailsResponse.result.reviewRating)
                } else {
                    fragmentHospitalListDetailsBinding?.txtNursedetailsReviewMore?.visibility =
                        View.GONE
                    finalReviewRatingList =
                        ArrayList()
                    for (i in 0 until nurseDetailsResponse.result.reviewRating.size) {
                        val reviewRatingItem =
                            com.rootscare.data.model.api.response.hospitalallapiresponse.hospitaldetailsresponse.ReviewRatingItem()
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
                fragmentHospitalListDetailsBinding?.recyclerViewNurselistingReview?.visibility =
                    View.GONE
                fragmentHospitalListDetailsBinding?.tvNurselistingReviewNoDate?.visibility =
                    View.VISIBLE
                fragmentHospitalListDetailsBinding?.tvNurselistingReviewNoDate?.text =
                    "No review found."
            }

            if (nurseDetailsResponse?.result?.image != null && nurseDetailsResponse.result.image != ""
            ) {
                nurseImage = nurseDetailsResponse.result.image
                Glide.with(this.activity!!)
                    .load(BaseMediaUrls.USERIMAGE.url + (nurseDetailsResponse.result.image))
                    .into(fragmentHospitalListDetailsBinding?.imgDoctordetailsProfilephoto!!)
            } else {
                Glide.with(this.activity!!)
                    .load(R.drawable.no_image)
                    .into(fragmentHospitalListDetailsBinding?.imgDoctordetailsProfilephoto!!)
            }


        } else {
            Toast.makeText(activity, nurseDetailsResponse?.message, Toast.LENGTH_SHORT).show()
        }

    }

    override fun errorNurseDetailsResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setSpecialityDataListing(departmentList: ArrayList<DepartmentItem?>?) {
        assert(fragmentHospitalListDetailsBinding!!.recyclerViewRootscareHospitalDepart != null)
        val recyclerView = fragmentHospitalListDetailsBinding!!.recyclerViewRootscareHospitalDepart
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterNurseSpecilityListRecyclerview(departmentList, context!!)
        recyclerView.adapter = contactListAdapter
    }

    private fun setServiceDataListing(departmentList: ArrayList<ServiceDataItem?>?) {
        assert(fragmentHospitalListDetailsBinding!!.recyclerViewRootscareHospitalDepart != null)
        val recyclerView = fragmentHospitalListDetailsBinding!!.recyclerViewRootscareHospitalService
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterHospitalServiceListRecyclerview(departmentList, context!!)
        recyclerView.adapter = contactListAdapter
    }

    private fun setReviewRatingListing(reviewRatingList: ArrayList<com.rootscare.data.model.api.response.hospitalallapiresponse.hospitaldetailsresponse.ReviewRatingItem?>?) {
        assert(fragmentHospitalListDetailsBinding!!.recyclerViewNurselistingReview != null)
        val recyclerView = fragmentHospitalListDetailsBinding!!.recyclerViewNurselistingReview
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter = AdapterNurseReviewrecyclerview(reviewRatingList, context!!)
        recyclerView.adapter = contactListAdapter
    }

    private fun apiHitForNurseDetails() {
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val nurseDetailsRequest = HospitalRequest()
            nurseDetailsRequest.id = nurseId.toInt()


            fragmentHospitalListDetailsViewModel?.apinursedetails(nurseDetailsRequest)
        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

}