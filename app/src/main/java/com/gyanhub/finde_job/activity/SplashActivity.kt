package com.gyanhub.finde_job.activity


import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.ViewModelProvider
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.viewModle.PermissionViewModel


@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    private lateinit var permissionViewModel: PermissionViewModel
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        permissionViewModel = ViewModelProvider(this)[PermissionViewModel::class.java]
        permissionViewModel.requestPermissions(this) {
            handler.postDelayed({
                val intent = Intent(this, FragmentHolderActivity::class.java)
                startActivity(intent)
                finish()
            }, 1000)
        }

    }
}
