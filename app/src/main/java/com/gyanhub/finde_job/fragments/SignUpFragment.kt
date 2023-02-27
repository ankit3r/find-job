package com.gyanhub.finde_job.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.databinding.FragmentSignUpBinding
import com.gyanhub.finde_job.viewModle.AuthViewModel


class SignUpFragment : Fragment() {
    private lateinit var binding: FragmentSignUpBinding
    private lateinit var viewModel: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        binding.btnBack.setOnClickListener {
            viewModel.fragment = LoginFragment()
            requireActivity().onBackPressed()
        }

        binding.btnSignUp.setOnClickListener {
            checkFiled()
        }


        return binding.root
    }

    private fun checkFiled() {
        if (binding.eTxtNameUp.text.isEmpty()) {
            binding.eTxtNameUp.error = "Required"
            return
        }
        if (binding.eTxtEmailUp.text.isEmpty()) {
            binding.eTxtEmailUp.error = "Required"
            return
        }
        if (binding.eTxtPassUp.text.isEmpty()) {
            binding.eTxtPassUp.error = "Required"
            return
        }
        viewModel.registerUser(
            binding.eTxtEmailUp.text.toString(),
            binding.eTxtPassUp.text.toString(),
            binding.eTxtNameUp.toString()
        ) { success, error ->
        if (success){
            Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(context, "error $error", Toast.LENGTH_SHORT).show()
        }
        }

    }
}
