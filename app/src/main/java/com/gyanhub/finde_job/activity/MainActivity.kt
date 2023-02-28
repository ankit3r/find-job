package com.gyanhub.finde_job.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.databinding.ActivityMainBinding
import com.gyanhub.finde_job.fragments.main.AppliedFragment
import com.gyanhub.finde_job.fragments.main.HomeFragment
import com.gyanhub.finde_job.fragments.main.ProfileFragment
import com.gyanhub.finde_job.fragments.main.YourPostFragment

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setFragment(HomeFragment())
        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                   setFragment(HomeFragment())
                    true
                }
                R.id.navigation_applied -> {
                    setFragment(AppliedFragment())
                    true
                }
                R.id.navigation_job -> {
                    setFragment(YourPostFragment())
                    true
                }
                R.id.navigation_Profile -> {
                    setFragment(ProfileFragment())
                    true
                }
                else -> false
            }
        }

    }


    private fun setFragment(fragment: Fragment) {
        val fragmentTransient = supportFragmentManager.beginTransaction()
        fragmentTransient.replace(R.id.frameLayout, fragment)
        fragmentTransient.commit()
    }

}