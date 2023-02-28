package com.gyanhub.finde_job.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.databinding.FragmentYourPostBinding


class YourPostFragment : Fragment() {
    private lateinit var binding: FragmentYourPostBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentYourPostBinding.inflate(layoutInflater, container, false)
        binding.btnPostJob.setOnClickListener {
            bottomSheet()
        }



        return binding.root
    }
    private fun bottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(requireActivity())
        bottomSheetDialog.setContentView(R.layout.post_job_bottom)
        val btn = bottomSheetDialog.findViewById<Button>(R.id.btnPostJob)
        val whNo = bottomSheetDialog.findViewById<EditText>(R.id.eTxtWhNo)
        val WCA = bottomSheetDialog.findViewById<EditText>(R.id.eTxtWCA)
        val Skill = bottomSheetDialog.findViewById<EditText>(R.id.eTxtSkill)
        val Disc = bottomSheetDialog.findViewById<EditText>(R.id.eTxtDisc)
        val TotalPost = bottomSheetDialog.findViewById<EditText>(R.id.eTxtTotalPost)
        val Pay = bottomSheetDialog.findViewById<EditText>(R.id.eTxtPay)
        val CyName = bottomSheetDialog.findViewById<EditText>(R.id.eTxtCyName)
        val Title = bottomSheetDialog.findViewById<EditText>(R.id.eTxtTitle)
        val type = bottomSheetDialog.findViewById<Spinner>(R.id.type)
        btn?.setOnClickListener {
            bottomSheetDialog.dismiss()
        }
        bottomSheetDialog.show()
    }

}