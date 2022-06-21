package com.rootscare.ui.medicalrecords

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import com.dialog.CommonDialog
import com.interfaces.OnMedicalRecordFileDeleteListner
import com.rootscare.BR
import com.rootscare.R
import com.rootscare.data.model.api.request.medicalrecorddeleterequest.MedicalFileDeleteRequest
import com.rootscare.data.model.api.request.medicalrecordsrequest.GetMedicalRecordListRequest
import com.rootscare.data.model.api.response.medicalfiledeleteresponse.MedicalFileDeleteResponse
import com.rootscare.data.model.api.response.medicalrecordresponse.MedicalRecordListResponse
import com.rootscare.data.model.api.response.medicalrecordresponse.ResultItem
import com.rootscare.databinding.FragmentMedicalRecordsBinding
import com.interfaces.DialogClickCallback
import com.rootscare.ui.addmedicalrecords.FragmentAddMedicalRecord
import com.rootscare.ui.base.BaseFragment
import com.rootscare.ui.home.HomeActivity
import com.rootscare.ui.medicalrecords.adapter.AdapterMedicalRecordsRecyclerview
import com.rootscare.ui.profile.FragmentProfile


class FragmentMedicalRecords :
    BaseFragment<FragmentMedicalRecordsBinding, FragmentMedicalRecordsViewModel>(),
    FragmentMedicalRecordsNavigator {
    private var fragmentMedicalRecordsBinding: FragmentMedicalRecordsBinding? = null
    private var fragmentMedicalRecordsViewModel: FragmentMedicalRecordsViewModel? = null
    override val bindingVariable: Int
        get() = BR.viewModel
    override val layoutId: Int
        get() = R.layout.fragment_medical_records
    override val viewModel: FragmentMedicalRecordsViewModel
        get() {
            fragmentMedicalRecordsViewModel =
                ViewModelProviders.of(this).get(FragmentMedicalRecordsViewModel::class.java!!)
            return fragmentMedicalRecordsViewModel as FragmentMedicalRecordsViewModel
        }

    companion object {
        fun newInstance(): FragmentMedicalRecords {
            val args = Bundle()
            val fragment = FragmentMedicalRecords()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentMedicalRecordsViewModel!!.navigator = this
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fragmentMedicalRecordsBinding = viewDataBinding

        fragmentMedicalRecordsBinding?.btnAddMedicalRecord?.setOnClickListener {
            (activity as HomeActivity).checkInBackstack(FragmentAddMedicalRecord.newInstance())
        }

        if (isNetworkConnected) {
            baseActivity?.showLoading()
            var getMedicalRecordListRequest = GetMedicalRecordListRequest()
            getMedicalRecordListRequest?.userId =
                fragmentMedicalRecordsViewModel?.appSharedPref?.userId
//            appointmentRequest?.userId="11"

            fragmentMedicalRecordsViewModel?.apipatientmedicalrecord(getMedicalRecordListRequest)

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }
    }

    // Set up recycler view for service listing if available
    private fun setUpViewPrescriptionlistingRecyclerview(medicalrecordlist: ArrayList<ResultItem?>?) {
//        trainerList: ArrayList<TrainerListItem?>?
        assert(fragmentMedicalRecordsBinding!!.recyclerViewRootscareMedicalrecord != null)
        val recyclerView = fragmentMedicalRecordsBinding!!.recyclerViewRootscareMedicalrecord
        val gridLayoutManager = GridLayoutManager(activity, 1, GridLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = gridLayoutManager
        recyclerView.setHasFixedSize(true)
//        recyclerView.layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
//        val contactListAdapter = AdapterHospitalRecyclerviw(trainerList,context!!)
        val contactListAdapter = AdapterMedicalRecordsRecyclerview(medicalrecordlist, context!!)
        recyclerView.adapter = contactListAdapter
        contactListAdapter.recyclerViewItemClickWithView = object :
            OnMedicalRecordFileDeleteListner {
            override fun onDelectClick(id: String) {
                //  Toast.makeText(activity, "Delete api will call", Toast.LENGTH_SHORT).show()

                CommonDialog.showDialog(context!!, object :
                    DialogClickCallback {
                    override fun onDismiss() {
                    }

                    override fun onConfirm() {
                        if (isNetworkConnected) {
                            baseActivity?.showLoading()
                            val medicalFileDeleteRequest = MedicalFileDeleteRequest()
                            medicalFileDeleteRequest.id = id
                            fragmentMedicalRecordsViewModel?.apideletepatientmedicalrecord(
                                medicalFileDeleteRequest
                            )

                        } else {
                            Toast.makeText(
                                activity,
                                "Please check your network connection.",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                    }

                }, "Delete Member", "Are you sure to delete this file?")
            }

        }


    }

    override fun successMedicalRecordListResponse(medicalRecordListResponse: MedicalRecordListResponse?) {
        baseActivity?.hideLoading()
        if (medicalRecordListResponse?.code.equals("200")) {
            if (medicalRecordListResponse?.result != null && medicalRecordListResponse?.result.size > 0) {
                fragmentMedicalRecordsBinding?.recyclerViewRootscareMedicalrecord?.visibility =
                    View.VISIBLE
                fragmentMedicalRecordsBinding?.tvNoDate?.visibility = View.GONE
                setUpViewPrescriptionlistingRecyclerview(medicalRecordListResponse?.result)

            } else {
                fragmentMedicalRecordsBinding?.recyclerViewRootscareMedicalrecord?.visibility =
                    View.GONE
                fragmentMedicalRecordsBinding?.tvNoDate?.visibility = View.VISIBLE
                fragmentMedicalRecordsBinding?.tvNoDate?.text = medicalRecordListResponse?.message
            }

        } else {
            Toast.makeText(activity, medicalRecordListResponse?.message, Toast.LENGTH_SHORT).show()
            fragmentMedicalRecordsBinding?.recyclerViewRootscareMedicalrecord?.visibility =
                View.GONE
            fragmentMedicalRecordsBinding?.tvNoDate?.visibility = View.VISIBLE
            fragmentMedicalRecordsBinding?.tvNoDate?.text = medicalRecordListResponse?.message
        }
    }

    override fun successMedicalFileDeleteResponse(medicalFileDeleteResponse: MedicalFileDeleteResponse?) {
        baseActivity?.hideLoading()

        Toast.makeText(activity, medicalFileDeleteResponse?.message, Toast.LENGTH_SHORT).show()
        if (isNetworkConnected) {
            baseActivity?.showLoading()
            var getMedicalRecordListRequest = GetMedicalRecordListRequest()
            getMedicalRecordListRequest?.userId =
                fragmentMedicalRecordsViewModel?.appSharedPref?.userId
//            appointmentRequest?.userId="11"

            fragmentMedicalRecordsViewModel?.apipatientmedicalrecord(getMedicalRecordListRequest)

        } else {
            Toast.makeText(activity, "Please check your network connection.", Toast.LENGTH_SHORT)
                .show()
        }


//        else{
//            Toast.makeText(activity, medicalFileDeleteResponse?.message, Toast.LENGTH_SHORT).show()
//        }
    }

    override fun errorMedicalRecordListResponse(throwable: Throwable?) {
        baseActivity?.hideLoading()
        if (throwable?.message != null) {
            Log.d(FragmentProfile.TAG, "--ERROR-Throwable:-- ${throwable.message}")
            Toast.makeText(activity, "Something went wrong", Toast.LENGTH_SHORT).show()
        }
    }

}