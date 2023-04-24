package com.gyanhub.finde_job.model

import com.google.firebase.database.PropertyName

data class User(
    @PropertyName("UserId")
    val name: String,
    val email: String,
    val UserId: String,
    val phNo: String,
    val resume: String,
    val job: List<String>,
    val youApply: List<String>,
    val jobAppliedToken:String
){
    constructor() : this("","","","","", listOf(), listOf(),"")
}