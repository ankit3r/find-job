package com.gyanhub.finde_job.utils

import com.gyanhub.finde_job.model.User

sealed class UserResult {
    data class Success(val user: User) : UserResult()
    data class Error(val errorMessage: String) : UserResult()
}
