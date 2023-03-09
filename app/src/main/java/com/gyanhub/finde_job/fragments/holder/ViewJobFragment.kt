package com.gyanhub.finde_job.fragments.holder

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.databinding.FragmentViewJobBinding
import com.gyanhub.finde_job.databinding.LayoutApplyBinding
import com.gyanhub.finde_job.databinding.PostJobBottomBinding
import com.gyanhub.finde_job.model.State
import com.gyanhub.finde_job.viewModle.DbViewModel

class ViewJobFragment(private val jobId: String) : Fragment() {
    private lateinit var binding: FragmentViewJobBinding
    private lateinit var bottomBinding: LayoutApplyBinding
    private lateinit var dbModel: DbViewModel
    private lateinit var dialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewJobBinding.inflate(layoutInflater, container, false)
        dbModel = ViewModelProvider(this)[DbViewModel::class.java]
        progressBar()
        dbModel.getJobById(jobId) { success, data, error ->
            if (success && data != null) {
                data.apply {
                    binding.apply {
                        txtJobTitle.text = jobTitle
                        txtJobType.text = jobType
                        txtJobDis.text = jobDisc
                        txtPay.text = pay + " /Month"
                        txtCyName.text = jobCyName
                        txtSkill.text = skils.toString()
                        txtOpportunities.text = jobPostOpportunitity
                        txtWhoCanApply.text = whoCanApply
                        txtlocation.text = city + ", " + state
                    }
                }
                dialog.dismiss()

            } else
                dialog.dismiss()

        }

        binding.btnApplied.setOnClickListener {
            bottomSheet()
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
    private fun bottomSheet() {
       val bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomBinding = LayoutApplyBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomBinding.root)

        bottomSheetDialog.show()
    }
}