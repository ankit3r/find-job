package com.gyanhub.finde_job.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
   private lateinit var binding: FragmentHomeBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
       binding = FragmentHomeBinding.inflate(layoutInflater,container,false)


        return binding.root
    }


}