package com.gyanhub.finde_job.viewModle

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import com.gyanhub.finde_job.fragments.main.HomeFragment

class MainViewModel : ViewModel() {
     var fragment: Fragment = HomeFragment()
}