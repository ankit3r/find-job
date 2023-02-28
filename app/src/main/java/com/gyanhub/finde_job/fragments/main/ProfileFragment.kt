package com.gyanhub.finde_job.fragments.main

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.gyanhub.finde_job.databinding.FragmentMenuBinding

class ProfileFragment : Fragment() {
   private lateinit var binding:FragmentMenuBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        binding = FragmentMenuBinding.inflate(layoutInflater,container,false)


        return binding.root
    }


}