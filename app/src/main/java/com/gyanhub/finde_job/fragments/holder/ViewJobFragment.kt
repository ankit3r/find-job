package com.gyanhub.finde_job.fragments.holder

import android.app.AlertDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.databinding.FragmentViewJobBinding
import com.gyanhub.finde_job.viewModle.DbViewModel

class ViewJobFragment(private val jobId: String) : Fragment() {
    private lateinit var binding: FragmentViewJobBinding
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
                    binding.txtJobTitle.text = jobTitle
                    binding.txtJobType.text = jobType
                    binding.txtJobDis.text = jobDisc
                    binding.txtPay.text = pay + " /Month"
                    binding.txtCyName.text = jobCyName
                    binding.txtSkill.text = skils.toString()
                    binding.txtOpportunities.text = jobPostOpportunitity
                    binding.txtWhoCanApply.text = whoCanApply
                    binding.txtlocation.text = city + ", " + state
                }
                dialog.dismiss()

            } else
                dialog.dismiss()

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
}