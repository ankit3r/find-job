package com.gyanhub.finde_job.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.Fragment
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.databinding.ActivityHolderBinding
import com.gyanhub.finde_job.fragments.holder.PdfLoadFragment
import com.gyanhub.finde_job.fragments.holder.ViewApplicantFragment
import com.gyanhub.finde_job.fragments.holder.ViewJobFragment

class HolderActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHolderBinding
    private var position: Int = 0
    private lateinit var jobId: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHolderBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        position = intent.getIntExtra("f",0)
        jobId = intent.getStringExtra("id").toString()
       val pdfUrl = intent.getStringExtra("pdfUri").toString()
        when(position){
            0 -> setFragment(jobId,false,false,ViewJobFragment())
            1 -> setFragment(jobId,true,false,ViewJobFragment())
            2 -> setFragment(jobId,false,true,ViewJobFragment())
            3->viewPdfFragment(PdfLoadFragment(),pdfUrl)
            4->viewApplicantFragment(ViewApplicantFragment(),jobId)
        }
    }


    private fun setFragment(jobId:String,viewData:Boolean,reApply:Boolean,fragment: Fragment) {
        val bundle = Bundle()
        bundle.putString("jobId", jobId)
        bundle.putBoolean("view", viewData)
        bundle.putBoolean("reApply", reApply)
        fragment.arguments = bundle
        setFragment(fragment)
    }
    private fun viewPdfFragment(fragment: Fragment,url:String) {
        val bundle = Bundle()
        bundle.putString("pdf", url)
        fragment.arguments = bundle
        setFragment(fragment)
    }
    private fun viewApplicantFragment(fragment: Fragment,jobId: String) {
        val bundle = Bundle()
        bundle.putString("jobId", jobId)
        fragment.arguments = bundle
        setFragment(fragment)
    }
    private fun setFragment(fragment: Fragment){
        val fragmentTransient = supportFragmentManager.beginTransaction()
        fragmentTransient.replace(R.id.holderFragment, fragment)
        fragmentTransient.commit()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}