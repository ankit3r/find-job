package com.gyanhub.finde_job.viewModle

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.gyanhub.finde_job.fragments.LoginFragment
import com.gyanhub.finde_job.model.User
import com.gyanhub.finde_job.repository.AuthRepository

class AuthViewModel : ViewModel() {
    var fragment: Fragment = LoginFragment()
    private val authRepository = AuthRepository()

    fun registerUser(
        email: String,
        password: String,
        name: String,
        ph: String,
        callback: (Boolean, String) -> Unit
    ) {
        authRepository.registerUser(email, password, name,ph, callback)
    }

    fun loginUser(email: String, password: String, callback: (Boolean, String) -> Unit) {
        authRepository.loginUser(email, password, callback)
    }

    fun logoutUser() {
        authRepository.logoutUser()
    }

    fun isLogin(): Boolean {
        return authRepository.isUserLoggedIn()
    }

    fun getUser(callback: (Boolean, User?, String) -> Unit){
        authRepository.getUserData(callback)
    }

}