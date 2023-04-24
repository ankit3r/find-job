package com.gyanhub.finde_job.fragments.main

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.adapters.AppliedAdapter
import com.gyanhub.finde_job.adapters.onClickInterface.AppliedClick
import com.gyanhub.finde_job.databinding.FragmentAppliedBinding
import com.gyanhub.finde_job.utils.UserResult
import com.gyanhub.finde_job.viewModle.AuthViewModel
import com.gyanhub.finde_job.viewModle.DbViewModel

class AppliedFragment : Fragment(), AppliedClick {

    private var _binding: FragmentAppliedBinding? = null
    private val binding get() = _binding!!
    private lateinit var jobModel: DbViewModel
    private lateinit var authModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAppliedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        jobModel = ViewModelProvider(this)[DbViewModel::class.java]
        authModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        // progress bar
        if (jobModel.life) {
            val user = authModel.getUser(requireActivity())
            user.observe(viewLifecycleOwner) { u ->
                when (u) {
                    is UserResult.Success -> {
                        if (u.user.youApply.isNotEmpty()){
                            binding.txtMessage.visibility = View.GONE
                            jobModel.getYourJobs(u.user.youApply) { s, _ ->
                                if (s) {
                                    jobModel.yourJob.observe(viewLifecycleOwner) {
                                        binding.rcApplied.adapter =
                                            AppliedAdapter(requireContext(), it, this)
                                        // progress bar hide
                                    }
                                } else
                                // progress bar hide
                                    true
                            }
                        }else {
                            binding.txtMessage.visibility = View.VISIBLE
                            if (isAdded)
                                binding.txtMessage.text = getString(R.string.error_massage)
                        }
                    }
                    is UserResult.Error -> {
                        Toast.makeText(requireContext(), "User Data Error", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }


        }
    }

    override fun onDestroy() {
        jobModel.life = false
        super.onDestroy()
    }


    override fun onClick(phNo: String) {
        val uri = Uri.parse("http://api.whatsapp.com/send?phone=+91$phNo&text=")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }
}