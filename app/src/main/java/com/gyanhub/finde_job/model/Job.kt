package com.gyanhub.finde_job.model

import com.google.firebase.Timestamp

data class Job(
    val jobId: String,
    val jobTitle: String,
    val jobCyName: String,
    val jobPostOpportunitity: String,
    val whNo: String,
    val jobDisc: String,
    val whoCanApply: String,
    val skils: List<String>,
    val pay: String,
    val filterPay: Int,
    val jobType: String,
    val state: String,
    val city: String,
    val totalApplied: Int,
    val timestamp: Timestamp
) {
    constructor() : this("", "", "", "", "", "", "", listOf(), "", 0, "", "", "",0,Timestamp.now())
}
