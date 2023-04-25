package com.gyanhub.finde_job.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
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

    public override fun onStart() {
        super.onStart()
        val currentUser = FirebaseAuth.getInstance().currentUser
        if(currentUser != null){
            if(currentUser.isEmailVerified){
                startActivity(Intent(this@FragmentHolderActivity, MainActivity::class.java))
                finish()
            }

        }
    }
}