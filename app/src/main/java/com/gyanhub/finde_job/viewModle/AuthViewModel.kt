package com.gyanhub.finde_job.viewModle

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.gyanhub.finde_job.activity.comp.LoaderClass
import com.gyanhub.finde_job.fragments.LoginFragment
import com.gyanhub.finde_job.repository.AuthRepository
import com.gyanhub.finde_job.utils.UserResult

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
        authRepository.registerUser(email, password, name, ph, callback)
    }

    fun loginUser(email: String, password: String, callback: (Boolean, String) -> Unit) {
        authRepository.loginUser(email, password, callback)
    }

    fun logoutUser(context: Context) {
        authRepository.logoutUser(context)
    }


    fun getUser(context: Context): LiveData<UserResult> {
        val userResult = MutableLiveData<UserResult>()
        val loaderClass = LoaderClass(context)
        Log.d("ANKIT", "user data")
        loaderClass.loading("Loading")
        loaderClass.showLoder()
        authRepository.getUserData { b, u, e ->
            if (b) {
                if (u != null) userResult.value = UserResult.Success(u)
                else userResult.value = UserResult.Error("Null")
                loaderClass.hideLoder()
            } else {
                userResult.value = UserResult.Error(e)
                loaderClass.hideLoder()
            }
        }
        return userResult
    }

    suspend fun uploadResume(fileUri: Uri, callback: (Boolean, String, String) -> Unit) {
        authRepository.uploadResume(fileUri, callback)
    }

    suspend fun updateResume(
        resumeUrl: String,
        fileUri: Uri,
        callback: (Boolean, String, String) -> Unit
    ) {
        authRepository.deleteResume(resumeUrl, fileUri, callback)
    }


    fun updatePhNo(no: String, context: Context) {
        val loading = LoaderClass(context)
        loading.loading("Saving Data")
        loading.showLoder()
        authRepository.updatePhNo(no) { s, e ->
            if (s) {
                loading.hideLoder()
                getUser(context)
                Toast.makeText(context, "Updated...", Toast.LENGTH_SHORT).show()
            } else {
                loading.hideLoder()
                Toast.makeText(context, "Updating failed...", Toast.LENGTH_SHORT).show()
            }
        }
    }

}