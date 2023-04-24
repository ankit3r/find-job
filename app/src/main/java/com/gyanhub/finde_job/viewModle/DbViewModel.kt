package com.gyanhub.finde_job.viewModle

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyanhub.finde_job.activity.comp.LoaderClass
import com.gyanhub.finde_job.model.Job
import com.gyanhub.finde_job.model.User
import com.gyanhub.finde_job.repository.DbRepository
import com.gyanhub.finde_job.utils.ApplicantViewResult
import kotlinx.coroutines.launch
import java.io.InputStream

class DbViewModel : ViewModel() {

    private val _progressBarVisible = MutableLiveData<Boolean>()
    val progressBarVisible: LiveData<Boolean> = _progressBarVisible
    var life = true

    val listOfPay = listOf(
        "All",
        "5000",
        "10000",
        "15000",
        "20000",
        "25000",
        "30000",
        "35000",
        "40000",
        "45000",
        "50000"
    )
    val listOfJobType = listOf("All", "Internship", "Job", "Part Time")
    val payField = "filterPay"
    val stateField = "state"
    val typeField = "jobType"
    var phNo = ""
    var resume: String = ""
    var list: List<Job> = listOf()
    var yourJoblist: List<String> = listOf()
    var appliedJobList: List<String> = listOf()
    fun showProgressBar() {
        _progressBarVisible.postValue(true)
    }

    fun hideProgressBar() {
        _progressBarVisible.postValue(false)
    }


    private val jobRepository = DbRepository()
    private val setUser = MutableLiveData<User>()
    val userData: LiveData<User>
        get() = setUser

    fun postJob(
        jobTitle: String,
        jobCyName: String,
        jobPostOpportunitity: String,
        whNo: String,
        jobDisc: String,
        whoCanApply: String,
        skils: List<String>,
        pay: String,
        filterPay: Int,
        jobType: String,
        state: String,
        city: String,
        callback: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            jobRepository.postJob(
                jobTitle,
                jobCyName,
                jobPostOpportunitity,
                whNo,
                jobDisc,
                whoCanApply,
                skils,
                pay,
                filterPay,
                jobType,
                state,
                city,
                callback
            )
        }
    }

    var data = MutableLiveData<List<Job>>()
    val getJob: LiveData<List<Job>>
        get() = data
    private var data2 = MutableLiveData<List<Job>>()
    val yourJob: LiveData<List<Job>>
        get() = data2

    fun getAllJob() {
        viewModelScope.launch {
            jobRepository.getAllJob { b, liveData, _ ->
                if (b) data.postValue(liveData)
            }
        }
    }

    fun getYourJobs(list: List<String>?, collback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (!list.isNullOrEmpty()) {
                jobRepository.getYourJob(list) { b, liveData, s ->
                    if (b) {
                        data2.postValue(liveData)
                        collback(false, "Success")
                    }
                    collback(true, "error")
                }
            }
        }
    }

    fun singlFieldFilter(
        fieldName: String,
        value: String,
        callback: (Boolean, List<Job>, String) -> Unit
    ) {
        jobRepository.filterBySingleFiled(fieldName, value, callback)
    }

    fun filterPay(
        fieldName: String,
        value: Int,
        callback: (Boolean, List<Job>, String) -> Unit
    ) {
        jobRepository.filterByPay(fieldName, value, callback)
    }


    fun multiplFieldFilter(
        pay: Int,
        location: String,
        type: String,
        callback: (Boolean, List<Job>, String) -> Unit
    ) {
        jobRepository.filterByMultiple(pay, location, type, callback)
    }

    fun doubleValueFilter(
        pay: Int, value: String, filedName: String,
        callback: (Boolean, List<Job>, String) -> Unit
    ) {
        jobRepository.filterByDoubleValue(pay, value, filedName, callback)
    }

    fun getJobById(id: String, callback: (Boolean, Job?, String) -> Unit) {
        jobRepository.getJobById(id, callback)
    }

    fun deleteJob(id: String, callback: (Boolean, String) -> Unit) {
        jobRepository.deleteYourJob(id, callback)
    }

    fun appliedJob(id: String, callback: (Boolean, String) -> Unit) {
        jobRepository.appliedJob(id, callback)
    }

    suspend fun viewPdf(url: String): InputStream {
        return jobRepository.viewPdf(url)
    }

    fun appliedForJob(uid: String, jobId: String, user: User, context: Context): Boolean {
        var success = false
        val loader = LoaderClass(context)
        loader.loading("Sending data...")
        loader.showLoder()
        jobRepository.appliedForJob(uid,jobId, user) { s, e ->
            if (s) {
                loader.hideLoder()
                toastMessage(context, "Done. next Send message on whatsapp")
                success = true
            } else {
                loader.hideLoder()
                toastMessage(context, e)
                success = false
            }
        }
        return success
    }

    fun viewApplicantList(jobId:String,context: Context):LiveData<ApplicantViewResult>{
        val loader = LoaderClass(context)
        loader.loading("Loading data...")
        loader.showLoder()
        val applicantLiveData = MutableLiveData<ApplicantViewResult>()
        jobRepository.getAllApplicant(jobId){s,applicant,e->
            if (s) {
                applicantLiveData.value = ApplicantViewResult.Success(applicant)
                loader.hideLoder()
            } else {
                applicantLiveData.value = ApplicantViewResult.Error(e)
                loader.hideLoder()
            }
        }
        return applicantLiveData
    }

    private fun toastMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}