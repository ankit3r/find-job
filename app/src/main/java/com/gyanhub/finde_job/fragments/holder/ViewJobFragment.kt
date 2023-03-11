package com.gyanhub.finde_job.fragments.holder

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.databinding.FragmentViewJobBinding
import com.gyanhub.finde_job.databinding.LayoutApplyBinding
import com.gyanhub.finde_job.viewModle.AuthViewModel
import com.gyanhub.finde_job.viewModle.DbViewModel
import okhttp3.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

@SuppressLint("SetTextI18n")
class ViewJobFragment(private val jobId: String, private val v: Boolean,private val re:Boolean) : Fragment() {
    private lateinit var binding: FragmentViewJobBinding
    private lateinit var bottomBinding: LayoutApplyBinding
    private lateinit var dbModel: DbViewModel
    private lateinit var userDb: AuthViewModel
    private lateinit var dialog: AlertDialog


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewJobBinding.inflate(layoutInflater, container, false)
        dbModel = ViewModelProvider(this)[DbViewModel::class.java]
        userDb = ViewModelProvider(this)[AuthViewModel::class.java]
        progressBar()
        if (v){
            binding.btnApplied.visibility = View.GONE
        }
        if(re){
            binding.btnApplied.text = "ReApply Now"
        }
        if (dbModel.life) {
            dbModel.showProgressBar()
            dbModel.progressBarVisible.observe(viewLifecycleOwner) {
                if (it) dialog.show() else dialog.dismiss()
            }
            dbModel.getJobById(jobId) { success, data, _ ->
                if (success && data != null) {
                    data.apply {
                        binding.apply {
                            txtJobTitle.text = "$jobTitle ($jobType)"
                            txtJobDis.text = jobDisc
                            txtAbout.text = "About $jobType"
                            txtPay.text = "$pay /Month"
                            txtCyName.text = "$jobCyName ($city, $state)"
                            txtSkill.text = skils.toString()
                            txtOpportunities.text = "Numbers of Vacancies :- $jobPostOpportunitity"
                            txtWhoCanApply.text = whoCanApply
                            txtTotalAppled.text =
                                getString(R.string.job_total_applied, totalApplied.toString())
                        }
                        dbModel.phNo = whNo
                    }
                    dbModel.hideProgressBar()

                } else
                    dbModel.hideProgressBar()

            }
        }


        binding.btnApplied.setOnClickListener {
            bottomSheet()
        }
        return binding.root
    }

    private fun progressBar() {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.custome_progress_bar, null)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
    }


    @SuppressLint("QueryPermissionsNeeded")
    private fun bottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomBinding = LayoutApplyBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomBinding.root)
        dbModel.showProgressBar()
        userDb.getUser { b, user, _ ->
            if (b && user != null) {
                dbModel.hideProgressBar()
                bottomBinding.textUserName.text = "Dear ${user.name}"
                bottomBinding.btnSend.setOnClickListener {
                    val message =
                        "Applicant has applied here are the details check whether the applicant is qualified to join you or not." +
                                "\n\n*Applicant Details:*\n\t\t*Name:*\t${user.name}\n\t\t*Mobile No:*\t${user.phNo}\n\t\t*Email ID:*\t${user.email}\n\t\t*Resume URL:* ${user.resume}"
                    val uri = Uri.parse(
                        "http://api.whatsapp.com/send?phone=+91${dbModel.phNo}&text=${
                            Uri.encode(message)
                        }"
                    )
                    val intent = Intent(Intent.ACTION_VIEW, uri)
                    startActivity(intent)
                   if (!re){
                       dbModel.appliedJob(jobId) { success, error ->
                           if (success) {
                               Toast.makeText(
                                   context,
                                   "Thank you for applied. You wait for replayed",
                                   Toast.LENGTH_SHORT
                               ).show()
                           } else {
                               Toast.makeText(context, "Error: $error", Toast.LENGTH_SHORT).show()
                           }
                       }
                   }
                    bottomSheetDialog.dismiss()
                }
            } else
                dbModel.hideProgressBar()
        }

        bottomSheetDialog.show()
    }

    override fun onDestroy() {
        dbModel.life = false
        super.onDestroy()
    }

}