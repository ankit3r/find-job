package com.gyanhub.finde_job.activity

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.databinding.ActivityMainBinding
import com.gyanhub.finde_job.fragments.main.AppliedFragment
import com.gyanhub.finde_job.fragments.main.HomeFragment
import com.gyanhub.finde_job.fragments.main.ProfileFragment
import com.gyanhub.finde_job.fragments.main.YourPostFragment
import com.gyanhub.finde_job.viewModle.DbViewModel
import com.gyanhub.finde_job.viewModle.MainViewModel


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainModel: MainViewModel
    private lateinit var dbModel: DbViewModel
    private lateinit var backPressCallback: OnBackPressedCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainModel = ViewModelProvider(this)[MainViewModel::class.java]
        dbModel = ViewModelProvider(this)[DbViewModel::class.java]
        setFragment(mainModel.fragment)

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    mainModel.fragment = HomeFragment()
                    setFragment(mainModel.fragment)
                    true
                }
                R.id.navigation_applied -> {
                    mainModel.fragment = AppliedFragment()
                    setFragment(mainModel.fragment)
                    true
                }
                R.id.navigation_job -> {
                    mainModel.fragment = YourPostFragment()
                    setFragment(mainModel.fragment)
                    true
                }
                R.id.navigation_Profile -> {
                    mainModel.fragment = ProfileFragment()
                    setFragment(mainModel.fragment)
                    true
                }
                else -> false
            }
        }
        backPressCallback = object: OnBackPressedCallback(false){
            override fun handleOnBackPressed() {
                val currentFragment = supportFragmentManager.findFragmentById(R.id.frameLayout)
                if (currentFragment is HomeFragment) {
                    finish()
                } else {
                    val homeFragment = HomeFragment()
                    supportFragmentManager.beginTransaction().apply {
                        replace(R.id.frameLayout, homeFragment)
                        commit()
                    }
                    binding.bottomNavigationView.selectedItemId = R.id.navigation_home
                }
            }

        }
        onBackPressedDispatcher.addCallback(this, backPressCallback)
    }

    private fun setFragment(fragment: Fragment) {
        val fragmentTransient = supportFragmentManager.beginTransaction()
        fragmentTransient.replace(R.id.frameLayout, fragment)
        fragmentTransient.commit()
    }

    override fun onDestroy() {
        backPressCallback.remove()
        super.onDestroy()
    }


}