package com.gyanhub.finde_job.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.databinding.ActivityHolderBinding
import com.gyanhub.finde_job.fragments.holder.ViewJobFragment

class HolderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHolderBinding
    private var position: Int = 0
    private lateinit var jobId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        position = intent.getIntExtra("f",0)
        jobId = intent.getStringExtra("id").toString()
        Toast.makeText(this, "id = $jobId", Toast.LENGTH_SHORT).show()
        when(position){
            0 -> setFragment(ViewJobFragment(jobId))
        }
    }


    private fun setFragment(fragment: Fragment) {
        val fragmentTransient = supportFragmentManager.beginTransaction()
        fragmentTransient.replace(R.id.holderFragment, fragment)
        fragmentTransient.commit()
    }
}