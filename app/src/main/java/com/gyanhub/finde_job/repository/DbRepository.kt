package com.gyanhub.finde_job.repository

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObject
import com.gyanhub.finde_job.model.Applicant
import com.gyanhub.finde_job.model.Job
import com.gyanhub.finde_job.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL

class DbRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var jobCollection = firestore.collection("Job")

     fun postJob(jobTitle: String, jobCyName: String, jobPostOpportunitity: String,
        whNo: String, jobDisc: String, whoCanApply: String, skils: List<String>, pay: String,
        filterPay: Int, jobType: String, state: String, city: String, callback: (Boolean, String) -> Unit
    ) {
        val uid = firestore.collection("job").document().id
        val job = Job(uid, jobTitle, jobCyName, jobPostOpportunitity,
            whNo, jobDisc, whoCanApply, skils, pay, filterPay, jobType,
            state, city, 0, Timestamp.now()
        )
        jobCollection.document(uid).set(job).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    firestore.collection("users").document(auth.currentUser!!.uid)
                        .update("job", FieldValue.arrayUnion(uid)).addOnSuccessListener { callback(true, "") }
                        .addOnFailureListener { e -> callback(false, e.message ?: "Unknown error occurred") }
                }
                else { callback(false, task.exception?.message ?: "Unknown error occurred") }
            }
    }

     fun getAllJob(callback: (Boolean, List<Job>, String) -> Unit) {
        val jobLiveData = mutableListOf<Job>()
        jobCollection.orderBy("timestamp", Query.Direction.DESCENDING)
            .addSnapshotListener { jobs, error ->
                if (!error?.message.isNullOrEmpty()) {
                    callback(false, jobLiveData, error?.message ?: "Unknown error occurred")
                    return@addSnapshotListener
                }
//                val job = mutableListOf<Job>()
                for (j in jobs!!) {
                    val jo = j.toObject(Job::class.java)
                    jobLiveData.add(jo)
                }
//                jobLiveData.addAll(job)
                callback(true, jobLiveData, "")
            }
    }



     fun getYourJob(
        documentIds: List<String>,
        callback: (Boolean, List<Job>?, String) -> Unit
    ) {
        val jobLiveData = mutableListOf<Job>()
        jobCollection
            .whereIn(FieldPath.documentId(), documentIds)
            .get()
            .addOnSuccessListener { documents ->
                val job = mutableListOf<Job>()
                if (!documents.isEmpty) {
                    for (document in documents) {
                        job.add(document.toObject())
                    }
                    jobLiveData.addAll(job)
                    callback(true, jobLiveData, "")
                } else {
                    callback(false, null, "No Posted Job")
                }
            }
            .addOnFailureListener { exception ->
                callback(false, jobLiveData, exception.message.toString())
            }
    }

    fun filterBySingleFiled(
        fieldName: String,
        value: String,
        callback: (Boolean, List<Job>, String) -> Unit
    ) {
        val jobLiveData = mutableListOf<Job>()
        jobCollection
            .whereEqualTo(fieldName, value)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.d("ANKIT", "Error getting filtered jobs: $error")
                    callback(false, jobLiveData, "No Data Found")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    for (doc in snapshot) {
                        val job = doc.toObject(Job::class.java)
                        jobLiveData.add(job)
                    }
                    callback(true, jobLiveData, "")
                }
            }
    }

    fun filterByPay(
        fieldName: String,
        value: Int,
        callback: (Boolean, List<Job>, String) -> Unit
    ) {
        val jobLiveData = mutableListOf<Job>()
        jobCollection
            .whereGreaterThanOrEqualTo(fieldName, value)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.d("ANKIT", "Error getting filtered jobs: $error")
                    callback(false, jobLiveData, "No Data Found")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    for (doc in snapshot) {
                        val job = doc.toObject(Job::class.java)
                        jobLiveData.add(job)
                    }
                    callback(true, jobLiveData, "")
                }
            }
    }

    fun filterByMultiple(
        pay: Int,
        location: String,
        type: String,
        callback: (Boolean, List<Job>, String) -> Unit
    ) {
        val jobLiveData = mutableListOf<Job>()
        jobCollection
            .whereGreaterThanOrEqualTo("filterPay", pay)
            .whereEqualTo("state", location)
            .whereEqualTo("jobType", type)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.d("ANKIT", "Error getting filtered jobs: $error")
                    callback(false, jobLiveData, "No data Found")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    for (doc in snapshot) {
                        val job = doc.toObject(Job::class.java)
                        jobLiveData.add(job)
                    }
                    callback(true, jobLiveData, "")
                }
            }
    }

    fun filterByDoubleValue(
        pay: Int,
        value: String,
        filedName: String,
        callback: (Boolean, List<Job>, String) -> Unit
    ) {
        val jobLiveData = mutableListOf<Job>()
        jobCollection
            .whereGreaterThanOrEqualTo("filterPay", pay)
            .whereEqualTo(filedName, value)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.d("ANKIT", "Error getting filtered jobs: $error")
                    callback(false, jobLiveData, "No data Found")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    for (doc in snapshot) {
                        val job = doc.toObject(Job::class.java)
                        jobLiveData.add(job)
                        Log.d("ANKIT", job.toString())
                    }
                    Log.d("ANKIT", jobLiveData.size.toString())
                    callback(true, jobLiveData, "")
                }
            }
    }

    fun getJobById(id: String, callBack: (Boolean, Job?, String) -> Unit) {
        jobCollection.document(id)
            .get().addOnSuccessListener { document ->
                if (document != null) {
                    callBack(true, document.toObject<Job>(), "")
                } else {
                    callBack(false, null, "No Such Document")
                }
            }
            .addOnFailureListener { exception ->
                callBack(false, null, exception.message.toString())
            }
    }

    fun deleteYourJob(id: String, callBack: (Boolean, String) -> Unit) {
        jobCollection.document(id).delete()
            .addOnSuccessListener {
                firestore.collection("users").document(auth.currentUser!!.uid)
                    .update("job", FieldValue.arrayRemove(id))
                    .addOnSuccessListener {
                        callBack(true, "")
                    }
                    .addOnFailureListener { exception ->
                        callBack(false, exception.message.toString())
                    }

            }
            .addOnFailureListener { e ->
                Log.e("ANKIT", e.message.toString())
            }
    }

    fun appliedJob(id: String, callback: (Boolean, String) -> Unit) {
        jobCollection.document(id)
            .update("totalApplied", FieldValue.increment(1))
            .addOnSuccessListener {
                firestore.collection("users").document(auth.currentUser!!.uid)
                    .update(
                        "youApply", FieldValue.arrayUnion(id)
                    ).addOnSuccessListener {
                        callback(true, "")
                    }
                    .addOnFailureListener { e ->
                        callback(false, e.message ?: "Unknown error occurred")
                    }

            }
            .addOnFailureListener { e ->
                callback(false, e.message ?: "Not applied")
                Log.e("ANKIT", e.message.toString())
            }
    }

    suspend fun viewPdf(url: String): InputStream = withContext(Dispatchers.IO) {
        val connection = URL(url).openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()
        return@withContext connection.inputStream
    }

    fun appliedForJob(uid:String,jobId: String, user: User, callback: (Boolean, String) -> Unit) {
        val jobRef = jobCollection.document(jobId)
        val applicantRef = jobRef.collection("Applicant")
        applicantRef.document(uid).get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val document = task.result
                if (document != null && document.exists()) {
                    callback(false, "You Already Applied")
                } else {
                    val applicant = Applicant(uid,user.name,user.resume,user.phNo,user.email,Timestamp.now())
                    applicantRef.document(uid).set(applicant).addOnCompleteListener{
                        if (it.isSuccessful) callback(true,"") else callback(false,it.exception?.message.toString())
                    }
                }
            } else {
                callback(false, task.exception?.message.toString())
            }
        }
    }

    fun getAllApplicant(jobId: String, callback: (Boolean, List<Applicant>, String) -> Unit) {
        val applicantLiveData = mutableListOf<Applicant>()
        val jobRef = jobCollection.document(jobId)
        jobRef.collection("Applicant").orderBy("appliedDateTime", Query.Direction.DESCENDING)
            .addSnapshotListener { applicant, error ->
                if (!error?.message.isNullOrEmpty()) {
                    callback(false, emptyList(), error?.message.toString())
                    return@addSnapshotListener
                }
               if (applicant != null){
                   for(data in applicant){
                       applicantLiveData.add(data.toObject(Applicant::class.java))
                   }
                   callback(true,applicantLiveData,"")
               }else{
                   callback(false, emptyList(),"No one Applied yet.")
               }
            }

    }


}
