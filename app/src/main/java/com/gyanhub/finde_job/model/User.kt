package com.gyanhub.finde_job.model

data class User(
    val name: String,
    val email: String,
    val UserId: String,
    val phNo: String,
    val resume: String,
    val job: List<String>,
    val youApply: List<String>
)