package com.gyanhub.finde_job.fragments.main

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.FragmentHolderActivity
import com.gyanhub.finde_job.databinding.FragmentMenuBinding
import com.gyanhub.finde_job.viewModle.AuthViewModel
import com.gyanhub.finde_job.viewModle.DbViewModel

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var auth: AuthViewModel
    private lateinit var dialog : AlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater, container, false)
        auth = ViewModelProvider(this)[AuthViewModel::class.java]
        progressBar()

        auth.getUser { success, user, error ->
            if (success && user != null){
                binding.txtUserName.text = user.name
                binding.txtUserEmail.text = user.email
                binding.txtUserPhNo.text = user.phNo
                dialog.dismiss()
            }else{
                Toast.makeText(context, "Error $error", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }


        binding.btnLogout.setOnClickListener {
            auth.logoutUser()
            requireActivity().startActivity(Intent(context,FragmentHolderActivity::class.java))
            requireActivity().finish()
        }

        return binding.root
    }
    private fun progressBar(){
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.custome_progress_bar, null)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }


}