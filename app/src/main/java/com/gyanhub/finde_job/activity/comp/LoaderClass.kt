package com.gyanhub.finde_job.activity.comp

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import com.gyanhub.finde_job.R

class LoaderClass(private val context: Context) {
    private val builder = AlertDialog.Builder(context)
    @SuppressLint("InflateParams")
    private val view = LayoutInflater.from(context).inflate(R.layout.custome_progress_bar, null)
    private val messageTextView = view.findViewById<TextView>(R.id.txtError)
    private lateinit var dialog: AlertDialog

    private val handler = Handler(Looper.getMainLooper())

    fun loading(message: String) {
        handler.post {
            messageTextView.text = message
            builder.setView(view)
            builder.setCancelable(false)
            dialog = builder.create()
        }
    }

    fun showLoder() {
        handler.post {
            dialog.show()
        }
    }

    fun hideLoder() {
        handler.post {
            dialog.dismiss()
        }
    }
}
