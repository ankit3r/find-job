package com.gyanhub.finde_job.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.MainActivity
import com.gyanhub.finde_job.activity.comp.CheckPhoNo.Companion.isValidMobileNumber
import com.gyanhub.finde_job.databinding.FragmentSignUpBinding
import com.gyanhub.finde_job.viewModle.AuthViewModel

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: AuthViewModel
    private lateinit var imm: InputMethodManager
    private lateinit var dialog : AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        hideKeyBoard(binding.eTxtNameUp)
        hideKeyBoard(binding.eTxtEmailUp)
        hideKeyBoard(binding.eTxtPassUp)
        hideKeyBoard(binding.eTxtPhNoUp)
        binding.btnBack.setOnClickListener {
            viewModel.fragment = LoginFragment()
            requireActivity().onBackPressed()
        }

        binding.btnSignUp.setOnClickListener {
            checkFiled(it)
        }
    }

    private fun checkFiled(view: View) {
        imm.hideSoftInputFromWindow(view.windowToken, 0)
        if (binding.eTxtNameUp.text.isEmpty()) {
            binding.eTxtNameUp.error = "Required"
            return
        }
        if (binding.eTxtPhNoUp.text.isEmpty()) {
            binding.eTxtPhNoUp.error = "Required"
            return
        }else{
            val isValid = isValidMobileNumber(binding.eTxtPhNoUp.text.toString())
            if (!isValid) {
                binding.eTxtPhNoUp.error = "Invalid Mobile No"
                return
            }
        }
        if (binding.eTxtEmailUp.text.isEmpty()) {
            binding.eTxtEmailUp.error = "Required"
            return
        }
        if (binding.eTxtPassUp.text.isEmpty()) {
            binding.eTxtPassUp.error = "Required"
            return
        }
       progressBar()
        viewModel.registerUser(
            binding.eTxtEmailUp.text.toString(),
            binding.eTxtPassUp.text.toString(),
            binding.eTxtNameUp.text.toString(),
            binding.eTxtPhNoUp.text.toString()

        ) { success, error ->
            if (success) {
                dialog.dismiss()
                val intent = Intent(activity, MainActivity::class.java)
                startActivity(intent)
                requireActivity().finish()
            } else {
                Toast.makeText(context, "error $error", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

    }

    fun hideKeyBoard(eTxt: EditText) {
        eTxt.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                imm.hideSoftInputFromWindow(eTxt.windowToken, 0)
                eTxt.clearFocus()
                return@setOnEditorActionListener true
            }
            return@setOnEditorActionListener false
        }
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
