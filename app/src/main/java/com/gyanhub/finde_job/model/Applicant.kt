package com.gyanhub.finde_job.model

import com.google.firebase.Timestamp

data class Applicant(
    val applicantId: String,
    val applicantName:String,
    val applicantResume: String,
    val applicantPhNo: String,
    val applicantEmail: String,
    val appliedDateTime: Timestamp
    )
{constructor():this("","","","","",Timestamp.now())}
