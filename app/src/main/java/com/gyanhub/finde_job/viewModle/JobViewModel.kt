package com.gyanhub.finde_job.viewModle


import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gyanhub.finde_job.adapters.HomeAdapter
import com.gyanhub.finde_job.model.Job
import com.gyanhub.finde_job.repository.JobRepository
import kotlinx.coroutines.launch

class JobViewModel : ViewModel() {
    private val jobRepository = JobRepository()

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
            jobRepository.getAllJob{b, liveData, s ->
                if (!b){
                    return@getAllJob
                }
               data.postValue(liveData)
            }
        }
    }

    fun getYourJobs(list:List<String> , collback : (Boolean,String) -> Unit){
      viewModelScope.launch {
          jobRepository.getYourJob(list){ b, liveData, s ->
              if (!b){
                  collback(false,"error")
                  return@getYourJob
              }
              data2.postValue(liveData)
              collback(true,"error")
              }
      }
    }

    init {
        getAllJob()
    }
}