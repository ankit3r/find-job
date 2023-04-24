package com.gyanhub.finde_job.activity.comp

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import com.gyanhub.finde_job.R

class CheckPhoNo {
    companion object{
        fun isValidMobileNumber(mobileNumber: String): Boolean {
            val regex = Regex("^[6-9]\\d{9}\$")
            return regex.matches(mobileNumber)
        }

    }
}