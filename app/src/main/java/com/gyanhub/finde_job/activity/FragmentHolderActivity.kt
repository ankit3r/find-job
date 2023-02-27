package com.gyanhub.finde_job.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.fragments.LoginFragment
import com.gyanhub.finde_job.fragments.SignUpFragment
import com.gyanhub.finde_job.viewModle.AuthViewModel

class FragmentHolderActivity : AppCompatActivity() {
     lateinit var authViewModel: AuthViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_holder)
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        setFragment(authViewModel.fragment)
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransient = supportFragmentManager.beginTransaction()
        fragmentTransient.replace(R.id.frameLayout, fragment)
        fragmentTransient.commit()
    }
}