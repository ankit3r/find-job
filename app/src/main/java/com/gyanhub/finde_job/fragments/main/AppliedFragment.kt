package com.gyanhub.finde_job.fragments.main

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.adapters.AppliedAdapter
import com.gyanhub.finde_job.adapters.onClickInterface.AppliedClick
import com.gyanhub.finde_job.databinding.FragmentAppliedBinding
import com.gyanhub.finde_job.viewModle.AuthViewModel
import com.gyanhub.finde_job.viewModle.DbViewModel

class AppliedFragment : Fragment(), AppliedClick {
    private lateinit var binding: FragmentAppliedBinding
    private lateinit var jobModel: DbViewModel
    private lateinit var authModel: AuthViewModel
    private lateinit var progressBar: AlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAppliedBinding.inflate(layoutInflater, container, false)
        jobModel = ViewModelProvider(this)[DbViewModel::class.java]
        authModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        progressBar()
        if (jobModel.life) {
            jobModel.showProgressBar()
            jobModel.progressBarVisible.observe(viewLifecycleOwner) { visible ->
                if (visible) progressBar.show() else progressBar.dismiss()
            }
            authModel.getUser { success, user, _ ->
                if (success && user != null) {
                    jobModel.getYourJobs(user.youApply) { s, _ ->
                        if (s) {
                            jobModel.yourJob.observe(viewLifecycleOwner) {
                                binding.rcApplied.adapter = AppliedAdapter(requireContext(), it, this)
                                jobModel.hideProgressBar()
                            }
                        }else
                            jobModel.hideProgressBar()
                    }
                }else
                    jobModel.hideProgressBar()
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        jobModel.life = false
        super.onDestroy()
    }
    private fun progressBar() {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.custome_progress_bar, null)
        builder.setView(view)
        builder.setCancelable(false)
        progressBar = builder.create()

    }


    override fun onClick(phNo: String) {
        val uri = Uri.parse("http://api.whatsapp.com/send?phone=+91$phNo&text=")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}