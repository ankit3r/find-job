package com.gyanhub.finde_job.model

data class Job(
    val jobId: String,
    val jobTitle: String,
    val jobDisc: String,
    val skils: List<String>,
    val pay : String,
    val jobType:String
)
