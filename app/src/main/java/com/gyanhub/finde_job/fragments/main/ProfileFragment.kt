package com.gyanhub.finde_job.fragments.main

import android.app.Activity.RESULT_OK
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


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var auth: AuthViewModel
    private lateinit var dialog: AlertDialog
    private lateinit var resumeUrl :String
    private val PDF_REQUEST_CODE = 505
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater, container, false)
        auth = ViewModelProvider(this)[AuthViewModel::class.java]
        progressBar()
        resumeUrl = ""

        auth.getUser { success, user, error ->
            if (success && user != null) {
                binding.txtUserName.text = user.name
                binding.txtUserEmail.text = user.email
                binding.txtUserPhNo.text = user.phNo
                if (user.resume.isEmpty())
                    binding.txtUserResume.text = getString(R.string.upload_resume)
                else {
                    resumeUrl = user.resume
                    binding.txtUserResume.text = getString(R.string.download_resume)
                }
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Error $error", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        binding.txtUserResume.setOnClickListener {
          if (resumeUrl.isEmpty()){
              uploadResume()
          }else{
             // download resume
          }
        }

        binding.btnLogout.setOnClickListener {
            auth.logoutUser()
            requireActivity().startActivity(Intent(context, FragmentHolderActivity::class.java))
            requireActivity().finish()
        }

        return binding.root
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


    private fun uploadResume() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), PDF_REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_REQUEST_CODE && resultCode == RESULT_OK) {
            // Get the selected file URI
            val fileUri = data?.data

            auth.uploadResume(fileUri!!) { success, error ,url->
                if (success) {
                    binding.txtUserResume.text = getString(R.string.download_resume)

                }else
                    Toast.makeText(context, "Error $error", Toast.LENGTH_SHORT).show()
            }

        }
    }




}


