package com.rootscare.ui.doctorlistingdetails

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.doctorrequest.doctordetailsrequest.DoctorDetailsRequest
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordetailsresponse.DepartmentItem
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordetailsresponse.DoctorDetailsResponse
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordetailsresponse.QualificationDataItem
import com.rootscare.data.model.api.response.doctorallapiresponse.doctordetailsresponse.ReviewRatingItem
import com.rootscare.databinding.FragmentDoctorListingDetailsBinding
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.bookingappointment.FragmentBookingAppointment
import com.rootscare.ui.doctorlistingdetails.adapter.AdapterDoctordetailsEducationListRecyclerview
import com.rootscare.ui.doctorlistingdetails.adapter.AdapterDoctordetailsReviewListRecyclerview
import com.rootscare.ui.doctorlistingdetails.adapter.AdapterDoctordetailsSpecilityListRecyclerview
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.home.subfragment.HomeFragment
import com.rootscare.ui.submitfeedback.FragmentSubmitReview
import com.rootscare.utilitycommon.BaseMediaUrls


class FragmentDoctorListingDetails :
    BaseFragment<FragmentDoctorListingDetailsBinding, FragmentDoctorListingDetailsViewModel>(),
    FragmentDoctorListingDetailsNavigator {
    private var fragmentDoctorListingDetailsBinding: FragmentDoctorListingDetailsBinding? = null
    private var fragmentDoctorListingDetailsViewModel: FragmentDoctorListingDetailsViewModel? = null
    private var doctorId: String = ""
    private var hospitalId: String = ""
    private var isHospital: Boolean? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_doctor_listing_details
    override val viewModel: FragmentDoctorListingDetailsViewModel
        get() {
            fragmentDoctorListingDetailsViewModel =
                ViewModelProviders.of(this).get(FragmentDoctorListingDetailsViewModel::class.java)
            return fragmentDoctorListingDetailsViewModel as FragmentDoctorListingDetailsViewModel
        }

    companion object {
        fun newInstance(isHospital: Boolean, hospitalId: String, doctorId: String): FragmentDoctorListingDetails {
            val args = Bundle()
            args.putString("doctorId", doctorId)
            args.putString("hospitalId", hospitalId)
            args.putBoolean("isHospital", isHospital)
            val fragment = FragmentDoctorListingDetails()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentDoctorListingDetailsViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentDoctorListingDetailsBinding = viewDataBinding
        if (arguments != null && arguments?.getString("doctorId") != null) {
            doctorId = arguments?.getString("doctorId")!!
            hospitalId = arguments?.getString("hospitalId")!!
            isHospital = arguments?.getBoolean("isHospital")!!
            Log.d("Doctor Id", ": $doctorId")
        }

        //All Doctor Details Api Call
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            val doctorDetailsRequest = DoctorDetailsRequest()
            doctorDetailsRequest.userId =
                fragmentDoctorListingDetailsViewModel?.appSharedPref?.userId
            doctorDetailsRequest.id = doctorId
            fragmentDoctorListingDetailsViewModel?.apidoctordetails(doctorDetailsRequest)

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
//        fragmentDoctorListingDetailsBinding?.btnRootscareBookingDoctor?.setOnClickListener {
//            (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                FragmentBookingAppointment.newInstance(doctorId)
//            )
//        }

        fragmentDoctorListingDetailsBinding?.btnDoctorBooking?.setOnClickListener {
            fragmentDoctorListingDetailsViewModel?.appSharedPref?.appointmentType = "offline"
            (activity as HomeActivity).checkInBackstack(

                FragmentBookingAppointment.newInstance(isHospital!!, hospitalId, doctorId)
            )
        }

//        fragmentDoctorListingDetailsBinding?.btnDoctorVideoconsultantBooking?.setOnClickListener {
//
//            fragmentDoctorListingDetailsViewModel?.appSharedPref?.appointmentType = "online"
//            (activity as HomeActivity).checkFragmentInBackstackAndOpen(
//                FragmentBookingAppointment.newInstance(doctorId)
//            )
//        }

        fragmentDoctorListingDetailsBinding?.txtDoctorSubmitReview?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentSubmitReview.newInstance(doctorId)
            )
        }

        fragmentDoctorListingDetailsBinding?.txtHeaderDoctorWriteYourReview?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(
                FragmentSubmitReview.newInstance(doctorId)
            )
        }
    }

    override fun successDoctorDetailsResponse(doctorDetailsResponse: DoctorDetailsResponse?) {
        baseActivity?.hideLoading()
        if (doctorDetailsResponse?.code.equals("200")) {
            Toast.makeText(activity, doctorDetailsResponse?.message, Toast.LENGTH_SHORT).show()

            if (doctorDetailsResponse?.result?.avgRating != null && doctorDetailsResponse.result.avgRating != ""
            ) {
                fragmentDoctorListingDetailsBinding?.ratingBardoctordetailseview?.rating =
                    doctorDetailsResponse.result.avgRating.toFloat()
            } else {

            }
            if (doctorDetailsResponse?.result?.description != null && doctorDetailsResponse.result.description != ""
            ) {
                fragmentDoctorListingDetailsBinding?.txtDoctorAddress?.text =
                    doctorDetailsResponse.result.description
            } else {
                fragmentDoctorListingDetailsBinding?.txtDoctorAddress?.text = ""
            }
            fragmentDoctorListingDetailsBinding?.txtDoctordetailsDoctorname?.text =
                doctorDetailsResponse?.result?.firstName + " " + doctorDetailsResponse?.result?.lastName
            if (!doctorDetailsResponse?.result?.qualification.equals("") || doctorDetailsResponse?.result?.qualification != null) {
                fragmentDoctorListingDetailsBinding?.txtDoctordetailsQualification?.text =
                    doctorDetailsResponse?.result?.qualification
            }
            if (!doctorDetailsResponse?.result?.fees.equals("") || doctorDetailsResponse?.result?.fees != null) {
                fragmentDoctorListingDetailsBinding?.txtDoctordetailsDoctorfees?.text =
                    "SR " + doctorDetailsResponse?.result?.fees
            }
            if (!doctorDetailsResponse?.result?.image.equals("") && doctorDetailsResponse?.result?.image != null) {
                Glide.with(this.activity!!)
                    .load(BaseMediaUrls.USERIMAGE.url + (doctorDetailsResponse.result.image))
                    .into(fragmentDoctorListingDetailsBinding?.imgDoctordetailsProfilephoto!!)
            }
            if (doctorDetailsResponse?.result?.reviewRating != null && doctorDetailsResponse.result.reviewRating.size > 0) {
                fragmentDoctorListingDetailsBinding?.txtDoctordetailsNoofreview?.text =
                    doctorDetailsResponse.result.reviewRating.size.toString() + " " + "reviews"
            }

            if (doctorDetailsResponse?.result?.reviewAbility != null && doctorDetailsResponse.result.reviewAbility != ""
            ) {
                if (doctorDetailsResponse.result.reviewAbility == "yes") {
                    fragmentDoctorListingDetailsBinding?.txtHeaderDoctorWriteYourReview?.visibility =
                        View.GONE
                    fragmentDoctorListingDetailsBinding?.txtDoctorSubmitReview?.visibility =
                        View.GONE

                } else if (doctorDetailsResponse.result.reviewAbility == "no") {
                    fragmentDoctorListingDetailsBinding?.txtHeaderDoctorWriteYourReview?.visibility =
                        View.GONE
                    fragmentDoctorListingDetailsBinding?.txtDoctorSubmitReview?.visibility =
                        View.GONE
                }
            }

            if (doctorDetailsResponse?.result?.qualificationData != null && doctorDetailsResponse.result.qualificationData.size > 0) {
                fragmentDoctorListingDetailsBinding?.recyclerViewDoctorlistingEducation?.visibility =
                    View.VISIBLE
                fragmentDoctorListingDetailsBinding?.tvDoctorlistingEducationNoDate?.visibility =
                    View.GONE
                setQualificationDataListing(doctorDetailsResponse.result.qualificationData)
            } else {
                fragmentDoctorListingDetailsBinding?.recyclerViewDoctorlistingEducation?.visibility =
                    View.GONE
                fragmentDoctorListingDetailsBinding?.tvDoctorlistingEducationNoDate?.visibility =
                    View.VISIBLE
                fragmentDoctorListingDetailsBinding?.tvDoctorlistingEducationNoDate?.text =
                    "No Qualification Found"
            }

            if (doctorDetailsResponse?.result?.reviewRating != null && doctorDetailsResponse.result.reviewRating.size > 0) {
                fragmentDoctorListingDetailsBinding?.recyclerViewDoctorlistingReview?.visibility =
                    View.VISIBLE
                fragmentDoctorListingDetailsBinding?.tvDoctorlistingReviewNoDate?.visibility =
                    View.GONE
                setReviewRatingListing(doctorDetailsResponse.result.reviewRating)
            } else {
                fragmentDoctorListingDetailsBinding?.recyclerViewDoctorlistingReview?.visibility =
                    View.GONE
                fragmentDoctorListingDetailsBinding?.tvDoctorlistingReviewNoDate?.visibility =
                    View.VISIBLE
                fragmentDoctorListingDetailsBinding?.tvDoctorlistingReviewNoDate?.text =
                    "No Reviews Found"
            }

            if (doctorDetailsResponse?.result?.department != null && doctorDetailsResponse.result.department.size > 0) {
                fragmentDoctorListingDetailsBinding?.recyclerViewDoctorlistingSpecility?.visibility =
                    View.VISIBLE
                fragmentDoctorListingDetailsBinding?.tvDoctorlistingSpecilityNoDate?.visibility =
                    View.GONE
                setSpecilityDataListing(doctorDetailsResponse.result.department)
            } else {
                fragmentDoctorListingDetailsBinding?.recyclerViewDoctorlistingSpecility?.visibility =
                    View.GONE
                fragmentDoctorListingDetailsBinding?.tvDoctorlistingSpecilityNoDate?.visibility =
                    View.VISIBLE
                fragmentDoctorListingDetailsBinding?.tvDoctorlistingSpecilityNoDate?.text =
                    "No Speciality Found"
            }


        } else {
            Toast.makeText(activity, doctorDetailsResponse?.message, Toast.LENGTH_SHORT).show()

            fragmentDoctorListingDetailsBinding?.recyclerViewDoctorlistingEducation?.visibility =
                View.GONE
            fragmentDoctorListingDetailsBinding?.tvDoctorlistingEducationNoDate?.visibility =
                View.VISIBLE
            fragmentDoctorListingDetailsBinding?.tvDoctorlistingEducationNoDate?.text =
                "No Qualification Found"

            fragmentDoctorListingDetailsBinding?.recyclerViewDoctorlistingReview?.visibility =
                View.GONE
            fragmentDoctorListingDetailsBinding?.tvDoctorlistingReviewNoDate?.visibility =
                View.VISIBLE
            fragmentDoctorListingDetailsBinding?.tvDoctorlistingReviewNoDate?.text =
                "No Reviews Found"

            fragmentDoctorListingDetailsBinding?.recyclerViewDoctorlistingSpecility?.visibility =
                View.GONE
            fragmentDoctorListingDetailsBinding?.tvDoctorlistingSpecilityNoDate?.visibility =
                View.VISIBLE
            fragmentDoctorListingDetailsBinding?.tvDoctorlistingSpecilityNoDate?.text =
                "No Speciality Found"

        }

    }

    override fun errorDoctorDetailsResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(HomeFragment.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

    // Set up recycler view for service listing if available
    private fun setQualificationDataListing(qualificationDataList: ArrayList<QualificationDataItem?>?) {
        assert(fragmentDoctorListingDetailsBinding!!.recyclerViewDoctorlistingEducation != null)
        val recyclerView = fragmentDoctorListingDetailsBinding!!.recyclerViewDoctorlistingEducation
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter =
            AdapterDoctordetailsEducationListRecyclerview(qualificationDataList, context!!)
        recyclerView.adapter = contactListAdapter


    }

    // Set up recycler view for service listing if available
    private fun setSpecilityDataListing(departmentList: ArrayList<DepartmentItem?>?) {
        assert(fragmentDoctorListingDetailsBinding!!.recyclerViewDoctorlistingSpecility != null)
        val recyclerView = fragmentDoctorListingDetailsBinding!!.recyclerViewDoctorlistingSpecility
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter =
            AdapterDoctordetailsSpecilityListRecyclerview(departmentList, context!!)
        recyclerView.adapter = contactListAdapter


    }

    // Set up recycler view for service listing if available
    private fun setReviewRatingListing(reviewRatingList: ArrayList<ReviewRatingItem?>?) {
        assert(fragmentDoctorListingDetailsBinding!!.recyclerViewDoctorlistingReview != null)
        val recyclerView = fragmentDoctorListingDetailsBinding!!.recyclerViewDoctorlistingReview
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
        val contactListAdapter =
            AdapterDoctordetailsReviewListRecyclerview(reviewRatingList, context!!)
        recyclerView.adapter = contactListAdapter


    }


}