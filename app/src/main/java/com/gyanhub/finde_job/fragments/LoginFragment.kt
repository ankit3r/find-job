package com.gyanhub.finde_job.fragments


import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.internal.ViewUtils.hideKeyboard
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.MainActivity
import com.gyanhub.finde_job.databinding.FragmentLoginBinding
import com.gyanhub.finde_job.repository.LoginWithGoogle
import com.gyanhub.finde_job.viewModle.AuthViewModel

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel
    private lateinit var imm: InputMethodManager
    private lateinit var dialog: AlertDialog
    private lateinit var loginWithGoogle: LoginWithGoogle



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    @SuppressLint("RestrictedApi")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.btnSignUpPage.setOnClickListener {
            viewModel.fragment = SignUpFragment()
            replaceFragment(viewModel.fragment)
        }

        loginWithGoogle = LoginWithGoogle(this)
        binding.btnGoogle.setOnClickListener {
            loginWithGoogle.login()
        }


        hideKeyboard(binding.editTextTextEmailAddress)
        hideKeyboard(binding.passwordToggle)
        binding.btnLogin.setOnClickListener { checkFiled(it) }


        binding.btnForgot.setOnClickListener {
            if (binding.editTextTextEmailAddress.text.isNotEmpty()){
                loginWithGoogle.forgotPassword(binding.editTextTextEmailAddress.text.toString())
            }else{
                binding.editTextTextEmailAddress.error = "Enter Register Email"
            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameLayout, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    private fun checkFiled(view: View) {
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        if (binding.editTextTextEmailAddress.text.isEmpty()) {
            binding.editTextTextEmailAddress.error = "Required"
            return
        }
        if (binding.passwordToggle.text.isEmpty()) {
            binding.passwordToggle.error = "Required"
            return
        }
        progressBar()
        viewModel.loginUser(
            binding.editTextTextEmailAddress.text.toString(),
            binding.passwordToggle.text.toString()
        ) { success, error ->

            if (success) {
                dialog.dismiss()
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(context, "error $error", Toast.LENGTH_SHORT).show()
                binding.btnForgot.visibility = View.VISIBLE
                Log.d("ANKIT","error = $error")
                if(error==getString(R.string.passError))
                    binding.passwordToggle.setTextColor(ColorStateList.valueOf(Color.RED))
                dialog.dismiss()
            }

        }

    }

    private fun progressBar() {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.custome_progress_bar, null)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        loginWithGoogle.handleActivityResult(requestCode, data)
    }


}