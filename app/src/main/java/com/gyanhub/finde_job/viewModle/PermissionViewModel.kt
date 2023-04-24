package com.gyanhub.finde_job.viewModle

import android.Manifest
import android.app.Activity
import android.os.Build
import androidx.lifecycle.ViewModel
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener

class PermissionViewModel : ViewModel() {
     fun requestPermissions(activity: Activity, onPermissionsGranted: (Boolean) -> Unit) {
        Dexter.withContext(activity)
            .withPermissions(getPermissions())
            .withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    if (report.areAllPermissionsGranted()) {
                        onPermissionsGranted(true)
                    } else {
                        // Permission is denied permanently, navigate user to app settings
                        onPermissionsGranted(false)
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: MutableList<PermissionRequest>,
                    token: PermissionToken
                ) {
                    token.continuePermissionRequest()
                }
            })
            .check()
    }
    private fun getPermissions(): List<String> {
        val permissions = mutableListOf<String>()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q ) {
            permissions.add(Manifest.permission.ACCESS_MEDIA_LOCATION)
            permissions.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.ACCESS_MEDIA_LOCATION)
            permissions.add(Manifest.permission.POST_NOTIFICATIONS)
            permissions.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            permissions.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            permissions.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            permissions.add(Manifest.permission.ACCESS_NOTIFICATION_POLICY)
        }
        permissions.add("android.permission.POST_NOTIFICATION")
        return permissions
    }
}