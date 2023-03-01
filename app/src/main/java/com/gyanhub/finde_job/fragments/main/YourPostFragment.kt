package com.gyanhub.finde_job.fragments.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.comp.CustomSpinner
import com.gyanhub.finde_job.adapters.HomeAdapter
import com.gyanhub.finde_job.databinding.FragmentYourPostBinding
import com.gyanhub.finde_job.databinding.PostJobBottomBinding
import com.gyanhub.finde_job.viewModle.JobViewModel


class YourPostFragment : Fragment() {
    private lateinit var binding: FragmentYourPostBinding
    private lateinit var jobType: String
    private lateinit var jobPostModel: JobViewModel
    private lateinit var bottomBinding: PostJobBottomBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYourPostBinding.inflate(layoutInflater, container, false)
        jobPostModel = ViewModelProvider(this)[JobViewModel::class.java]
        binding.btnPostJob.setOnClickListener {
            bottomSheet()
        }
        jobPostModel.getYourJobs(listOf("5BMvsr9Hn6VQ67Roi7IJ")){success,erroe ->
            if (success){
                jobPostModel.yourJob.observe(viewLifecycleOwner){
                    binding.rcYourPost.adapter = HomeAdapter(it)
                    binding.textView.visibility = View.GONE
                }
            }else{
                binding.textView.visibility = View.VISIBLE
                binding.textView.text = erroe
            }

        }




        return binding.root
    }

    private fun bottomSheet() {

        bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomBinding = PostJobBottomBinding.inflate(layoutInflater)
        bottomSheetDialog.setContentView(bottomBinding.root)
        dropdown(bottomBinding.type, listOf("Internship", "Job"))


        bottomBinding.btnPostJob.setOnClickListener {
            if (!checkFiled(bottomBinding.eTxtTitle)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtCyName)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtPay)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtTotalPost)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtDisc)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtSkill)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtWCA)) return@setOnClickListener
            if (!checkFiled(bottomBinding.eTxtWhNo)) return@setOnClickListener
            //  converting skill into list
            val skillList = mutableListOf<String>()
            val tags = bottomBinding.eTxtSkill.text.toString().split(Regex("\\s*,\\s*"))
            for (tag in tags) {
                skillList.add(tag)
            }
            // posting job in firebase

            jobPostModel.postJob(
                bottomBinding.eTxtTitle.text.toString(),
                bottomBinding.eTxtCyName.text.toString(),
                bottomBinding.eTxtTotalPost.text.toString(),
                bottomBinding.eTxtWhNo.text.toString(),
                bottomBinding.eTxtDisc.text.toString(),
                bottomBinding.eTxtWCA.text.toString(),
                skillList,
                bottomBinding.eTxtPay.text.toString(),
                jobType
            ) { success, error ->
                if (success) {
                    bottomSheetDialog.dismiss()
                } else {
                    Log.d("ANKIT", "Error $error")
                }

            }
        }
        bottomSheetDialog.show()
    }

    private fun dropdown(view: Spinner, list: List<String>) {
        val adapter = CustomSpinner(requireActivity(), list)
        view.adapter = adapter
        view.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                jobType = list[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // do nothing
            }
        }
    }

    private fun checkFiled(view: EditText): Boolean {
        if (view.text.toString().isEmpty()) {
            view.error = "Required"
            return false
        }
        return true
    }


}