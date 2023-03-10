package com.gyanhub.finde_job.fragments


import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivities
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.MainActivity
import com.gyanhub.finde_job.databinding.FragmentLoginBinding
import com.gyanhub.finde_job.viewModle.AuthViewModel

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var imm: InputMethodManager
    private lateinit var dialog: AlertDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        binding.btnSignUpPage.setOnClickListener {
            viewModel.fragment = SignUpFragment()
            replaceFragment(viewModel.fragment)
        }
        hideKeyBoard(binding.editTextTextEmailAddress)
        hideKeyBoard(binding.passwordToggle)
        binding.btnLogin.setOnClickListener { checkFiled(it) }
        return binding.root
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

    private fun progressBar() {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.custome_progress_bar, null)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }


}