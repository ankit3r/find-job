package com.gyanhub.finde_job.utils

import com.gyanhub.finde_job.model.Applicant

sealed class ApplicantViewResult{
    data class Success(val applicant: List<Applicant>) : ApplicantViewResult()
    data class Error(val errorMessage: String) : ApplicantViewResult()
}
