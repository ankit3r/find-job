package com.gyanhub.finde_job.viewModle

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyanhub.finde_job.model.Job
import com.gyanhub.finde_job.model.User
import com.gyanhub.finde_job.repository.DbRepository
import kotlinx.coroutines.launch

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
            jobRepository.getAllJob { b, liveData, s ->
                if (b) {
                    data.postValue(liveData)
                }
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
        pay: Int,
        value: String,
        filedName: String,
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

    init {
        getAllJob()
    }
}