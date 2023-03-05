package com.gyanhub.finde_job.fragments.holder

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.databinding.FragmentViewResumeBinding

class ViewResumeFragment : Fragment() {

    private lateinit var binding: FragmentViewResumeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentViewResumeBinding.inflate(layoutInflater, container, false)


        return binding.root
    }


}