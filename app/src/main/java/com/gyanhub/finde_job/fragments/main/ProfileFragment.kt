package com.gyanhub.finde_job.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.databinding.FragmentMenuBinding
import com.gyanhub.finde_job.viewModle.AuthViewModel
import com.gyanhub.finde_job.viewModle.DbViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var dbModel: DbViewModel
    private lateinit var auth: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater, container, false)
        dbModel = ViewModelProvider(this)[DbViewModel::class.java]
        auth = ViewModelProvider(this)[AuthViewModel::class.java]

        dbModel.userData.observe(viewLifecycleOwner){
            binding.txtUserName.text = it.name
            binding.txtUserEmail.text = it.email
            binding.txtUserPhNo.text = it.phNo
        }
        binding.btnLogout.setOnClickListener { auth.logoutUser() }


        return binding.root
    }


}