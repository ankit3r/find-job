package com.gyanhub.finde_job.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.gyanhub.finde_job.model.Job

class JobRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun postJob(
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
        val uid = firestore.collection("job").document().id

        val job = Job(
            uid,
            jobTitle,
            jobCyName,
            jobPostOpportunitity,
            whNo,
            jobDisc,
            whoCanApply,
            skils,
            pay,
            jobType
        )
        firestore.collection("Job").document(uid)
            .set(job)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firestore.collection("users").document(auth.currentUser!!.uid)
                        .update(
                            "job", FieldValue.arrayUnion(uid)
                        ).addOnSuccessListener {
                            callback(true, "")
                        }
                        .addOnFailureListener { e ->
                            callback(false, e.message ?: "Unknown error occurred")
                        }

                } else {
                    callback(false, task.exception?.message ?: "Unknown error occurred")
                }
            }
    }

    suspend fun getAllJob(callback: (Boolean,List<Job>, String) -> Unit) {
        val jobLiveData = mutableListOf<Job>()
        firestore.collection("Job")
            .addSnapshotListener { jobs, error ->
                if (error != null) {
                    callback(false,jobLiveData, error.message ?: "Unknown error occurred")
                    return@addSnapshotListener
                }
                val job = mutableListOf<Job>()
                for (j in jobs!!){
                    val jo = j.toObject(Job::class.java)
                    job.add(jo)
                }
                jobLiveData.addAll(job)
                callback(true,jobLiveData,"")
            }
    }

}
