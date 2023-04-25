package com.gyanhub.finde_job.fragments.main


import android.app.*
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.FragmentHolderActivity
import com.gyanhub.finde_job.activity.HolderActivity
import com.gyanhub.finde_job.activity.comp.LoaderClass
import com.gyanhub.finde_job.databinding.FragmentMenuBinding
import com.gyanhub.finde_job.utils.UserResult
import com.gyanhub.finde_job.viewModle.AuthViewModel
import com.gyanhub.finde_job.viewModle.DbViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var auth: AuthViewModel
    private var resumeUrl: String? = null
    private lateinit var dbModel: DbViewModel



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater, container, false)
        auth = ViewModelProvider(this)[AuthViewModel::class.java]
        dbModel = ViewModelProvider(this)[DbViewModel::class.java]
        userData()


        binding.txtUserResume.setOnClickListener {
            if (resumeUrl.isNullOrEmpty()) {
                uploadResume()
            } else {
                val intent = Intent(context, HolderActivity::class.java)
                intent.putExtra("f", 3)
                intent.putExtra("pdfUri", resumeUrl)
                requireActivity().startActivity(intent)
            }
        }

        binding.btnLogout.setOnClickListener {
            auth.logoutUser(requireContext())
            requireActivity().startActivity(Intent(context, FragmentHolderActivity::class.java))
            requireActivity().finish()
        }

        return binding.root
    }



    private fun uploadResume() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), 505)

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 505 && resultCode == RESULT_OK) {
          val loader = LoaderClass(requireContext())
            loader.loading("Uploading Resume...")
            loader.showLoder()
            val fileUri = data?.data
           CoroutineScope(Dispatchers.IO).launch {
               auth.uploadResume(fileUri!!) { success, error, _ ->
                   if (success) {
                       binding.txtUserResume.text = getString(R.string.download_resume)
                       userData()
                       loader.hideLoder()
                   } else {
                       loader.hideLoder()
                       Toast.makeText(context, "Error $error", Toast.LENGTH_SHORT).show()
                   }
               }
           }

        }
    }

    private fun userData(){
        val user = auth.getUser(requireActivity())
        user.observe(viewLifecycleOwner) { u ->
            when (u) {
                is UserResult.Success -> {
                    binding.txtUserName.text = u.user.name
                    binding.txtUserEmail.text = u.user.email
                    if (u.user.phNo.isNotEmpty()) {
                        binding.txtUserPhNo.text = u.user.phNo
                    }
                    binding.txtFlatter.text = u.user.name[0].toString()
                    if (u.user.resume.isEmpty())
                        binding.txtUserResume.text = getString(R.string.upload_resume)
                    else {
                        resumeUrl = u.user.resume
                        if (isAdded)
                            binding.txtUserResume.text = getString(R.string.download_resume)
                    }

                }
                is UserResult.Error -> {
                    Toast.makeText(requireContext(), "User Data Error", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        userData()
    }

}