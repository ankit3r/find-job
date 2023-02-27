package com.gyanhub.finde_job.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.databinding.FragmentLoginBinding
import com.gyanhub.finde_job.viewModle.AuthViewModel

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: AuthViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        binding.btnSignUpPage.setOnClickListener {
            viewModel.fragment = SignUpFragment()
            replaceFragment(viewModel.fragment)
        }
        binding.btnLogin.setOnClickListener { checkFiled() }
        return binding.root
    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun checkFiled() {
        if (binding.editTextTextEmailAddress.text.isEmpty()) {
            binding.editTextTextEmailAddress.error = "Required"
            return
        }
        if (binding.passwordToggle.text.isEmpty()) {
            binding.passwordToggle.error = "Required"
            return
        }
        viewModel.loginUser(
            binding.editTextTextEmailAddress.text.toString(),
            binding.passwordToggle.text.toString()
        ) { success, error ->
            if (success){
                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(context, "error $error", Toast.LENGTH_SHORT).show()
            }

        }

    }

}