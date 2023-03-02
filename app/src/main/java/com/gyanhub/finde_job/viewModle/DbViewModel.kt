package com.gyanhub.finde_job.viewModle


import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyanhub.finde_job.model.Job
import com.gyanhub.finde_job.model.User
import com.gyanhub.finde_job.repository.DbRepository
import kotlinx.coroutines.launch

class DbViewModel : ViewModel() {
    private val jobRepository = DbRepository()
    private val yourYobList = MutableLiveData<List<String>>()
    val listOfYourJob: LiveData<List<String>>
        get() = yourYobList
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
        jobType: String,
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
                jobType,
                callback
            )
        }
    }

    var data = MutableLiveData<List<Job>>()
    val getJob: LiveData<List<Job>>
        get() = data


    var data2 = MutableLiveData<List<Job>>()
    val yourJob: LiveData<List<Job>>
        get() = data

    fun getAllJob() {
        viewModelScope.launch {
            jobRepository.getAllJob { b, liveData, s ->
                if (!b) {
                    return@getAllJob
                }
                data.postValue(liveData)
            }
        }
    }

    fun getYourJobs(list: List<String>, collback: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            jobRepository.getYourJob(list) { b, liveData, s ->
                if (!b) {
                    collback(false, "error")
                    return@getYourJob
                }
                data2.postValue(liveData)
                collback(true, "error")
            }
        }
    }

    fun getUserData() {
        viewModelScope.launch {
            jobRepository.getUser { success, user, error ->
                if (success) {
                    Log.d("ANKIT", user.toString())
                    setUser.postValue(user)
                  yourYobList.postValue(user?.job)
                } else {
                    Log.e("ANKIT", error)
                }

            }
        }
    }

    init {
        getUserData()
        getAllJob()
    }
}