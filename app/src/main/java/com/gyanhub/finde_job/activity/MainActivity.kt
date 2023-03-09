package com.gyanhub.finde_job.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.common.reflect.TypeToken
import com.google.gson.Gson
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.comp.CustomSpinner
import com.gyanhub.finde_job.databinding.ActivityMainBinding
import com.gyanhub.finde_job.fragments.main.AppliedFragment
import com.gyanhub.finde_job.fragments.main.HomeFragment
import com.gyanhub.finde_job.fragments.main.ProfileFragment
import com.gyanhub.finde_job.fragments.main.YourPostFragment
import com.gyanhub.finde_job.model.State
import com.gyanhub.finde_job.viewModle.DbViewModel
import com.gyanhub.finde_job.viewModle.MainViewModel

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainModel: MainViewModel
    private lateinit var dbModel: DbViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mainModel = ViewModelProvider(this)[MainViewModel::class.java]
        dbModel = ViewModelProvider(this)[DbViewModel::class.java]
        setFragment(mainModel.fragment)



        binding.bottomNavigationView.setOnNavigationItemSelectedListener { item ->
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


    }


    private fun setFragment(fragment: Fragment) {
        val fragmentTransient = supportFragmentManager.beginTransaction()
        fragmentTransient.replace(R.id.frameLayout, fragment)
        fragmentTransient.commit()
    }


    override fun onBackPressed() {
        if (mainModel.fragment == HomeFragment()) {
            super.onBackPressed()
        } else {
            mainModel.fragment = HomeFragment()
        }

    }

}