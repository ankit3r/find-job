package com.gyanhub.finde_job.model

data class Job(
    val jobId: String,
    val jobTitle: String,
    val jobCyName : String,
    val jobPostOpportunitity : String,
    val whNo : String,
    val jobDisc: String,
    val whoCanApply : String,
    val skils: List<String>,
    val pay : String,
    val filterPay : Int,
    val jobType:String,
    val state:String,
    val city:String
){
    constructor() : this("", "", "","","","","",listOf(),"",0,"","","")
}
