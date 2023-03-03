package com.gyanhub.finde_job.fragments.main

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.activity.FragmentHolderActivity
import com.gyanhub.finde_job.databinding.FragmentMenuBinding
import com.gyanhub.finde_job.viewModle.AuthViewModel
import com.gyanhub.finde_job.viewModle.DbViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var auth: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater, container, false)

        auth = ViewModelProvider(this)[AuthViewModel::class.java]

        auth.getUser { success, user, error ->
            if (success && user != null){
                binding.txtUserName.text = user.name
                binding.txtUserEmail.text = user.email
                binding.txtUserPhNo.text = user.phNo
            }else{
                Toast.makeText(context, "Error $error", Toast.LENGTH_SHORT).show()
            }
        }


        binding.btnLogout.setOnClickListener {
            auth.logoutUser()
            requireActivity().startActivity(Intent(context,FragmentHolderActivity::class.java))
            requireActivity().finish()
        }

        return binding.root
    }


}