package com.gyanhub.finde_job.fragments.holder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.adapters.ApplicantAdapter
import com.gyanhub.finde_job.adapters.onClickInterface.ApplicantInterFace
import com.gyanhub.finde_job.databinding.FragmentViewApplicantBinding
import com.gyanhub.finde_job.databinding.LayoutPdfvieBottmseetBinding
import com.gyanhub.finde_job.utils.ApplicantViewResult
import com.gyanhub.finde_job.viewModle.DbViewModel
import kotlinx.coroutines.launch

class ViewApplicantFragment : Fragment(), ApplicantInterFace {
    private var _binding: FragmentViewApplicantBinding? = null

    private val binding get() = _binding!!
    private var jobId: String? = null
    private lateinit var dbModel: DbViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewApplicantBinding.inflate(layoutInflater, container, false)
        jobId = arguments?.getString("jobId")!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) requireActivity().title = getString(R.string.applicant)
        dbModel = ViewModelProvider(this)[DbViewModel::class.java]
        dbModel.viewApplicantList(jobId!!, requireContext()).observe(viewLifecycleOwner) { d ->
            when (d) {
                is ApplicantViewResult.Success -> {
                    if (d.applicant.isNotEmpty())
                        binding.rcApplicant.adapter = ApplicantAdapter(d.applicant, this,requireContext())
                    else binding.txtMassage.visibility = View.VISIBLE
                }

                is ApplicantViewResult.Error -> {
                    binding.txtMassage.visibility = View.VISIBLE
                    binding.txtMassage.text = d.errorMessage
                }
            }
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    override fun applicantResumeView(resumeUrl: String) {
        bottomSheet(resumeUrl)
    }
    private fun bottomSheet(url:String) {
        val bottomSheetDialog = BottomSheetDialog(requireActivity())
       val bottomBinding = LayoutPdfvieBottmseetBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomBinding.root)
        bottomSheetDialog.setCancelable(false)
        loadPdf(url,bottomBinding.viewPdf1,bottomBinding.progressBar)
        bottomSheetDialog.show()
        bottomBinding.progressBar.visibility = View.VISIBLE
        bottomBinding.btnClose.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
    }
    private fun loadPdf(url: String, viewPdf: PDFView, progressBar: ProgressBar) {
        lifecycleScope.launch {
            try {
                val pdfStream = dbModel.viewPdf(url)
              viewPdf.fromStream(pdfStream).load()
               progressBar.visibility = View.GONE
            } catch (e: Exception) {
                Toast.makeText(context, "Failed to load PDF", Toast.LENGTH_SHORT).show()
                progressBar.visibility = View.GONE
            }
        }
    }
}