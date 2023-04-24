package com.gyanhub.finde_job.adapters

import android.app.ProgressDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.gyanhub.finde_job.databinding.LayoutApplicantCardBinding
import com.gyanhub.finde_job.model.Applicant
import com.google.firebase.Timestamp
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.comp.LoaderClass
import com.gyanhub.finde_job.adapters.onClickInterface.ApplicantInterFace
import com.gyanhub.finde_job.viewModle.DbViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ApplicantAdapter(
    val list: List<Applicant>,
    val click: ApplicantInterFace,
    val context: Context,
) :
    RecyclerView.Adapter<ApplicantAdapter.ApplicantViewHolder>() {
    inner class ApplicantViewHolder(val binding: LayoutApplicantCardBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun setData(data: Applicant) {
            binding.apply {
                txtApplicantName.text = data.applicantName
                txtApplicantTime.text = timeConverter(data.appliedDateTime)
                txtApplicantEmail.text = data.applicantEmail
                txtApplicantPh.text = data.applicantPhNo
                btnApplicantResume.setOnClickListener {
                  click.applicantResumeView(data.applicantResume)
                }
            }
        }

        private fun timeConverter(timestamp: Timestamp): String {
            val formatter = SimpleDateFormat("HH:mm a \ndd/MM/yyyy", Locale.getDefault())
            return formatter.format(Date(timestamp.seconds * 1000))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ApplicantViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = LayoutApplicantCardBinding.inflate(inflater, parent, false)
        return ApplicantViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ApplicantViewHolder, position: Int) {
        holder.setData(list[position])
    }
}