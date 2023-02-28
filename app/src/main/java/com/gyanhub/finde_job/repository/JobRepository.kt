package com.gyanhub.finde_job.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.gyanhub.finde_job.model.Job

class JobRepository {
    private val firestore = FirebaseFirestore.getInstance()

   suspend fun postJob(
        jobTitle: String,
        jobCyName : String,
        jobPostOpportunitity : String,
        whNo : String,
        jobDisc: String,
        whoCanApply : String,
        skils: List<String>,
        pay : String,
       jobType:String,
        callback: (Boolean, String) -> Unit) {
        val uid = firestore.collection("job").document().id
        val job = Job(uid, jobTitle,jobCyName,jobPostOpportunitity,whNo,jobDisc,whoCanApply,skils,pay,jobType)
        firestore.collection("Job").document(uid)
            .set(job)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "")
                } else {
                    callback(false, task.exception?.message ?: "Unknown error occurred")
                }
            }
    }
}