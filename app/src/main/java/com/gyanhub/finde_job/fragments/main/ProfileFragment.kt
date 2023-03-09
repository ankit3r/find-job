package com.gyanhub.finde_job.fragments.main


import android.app.*
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.FragmentHolderActivity
import com.gyanhub.finde_job.databinding.FragmentMenuBinding
import com.gyanhub.finde_job.viewModle.AuthViewModel
import java.io.File

@Suppress("DEPRECATION")
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentMenuBinding
    private lateinit var auth: AuthViewModel
    private lateinit var dialog: AlertDialog
    private lateinit var resumeUrl: String



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBinding.inflate(layoutInflater, container, false)
        auth = ViewModelProvider(this)[AuthViewModel::class.java]
        progressBar()
        resumeUrl = ""

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "DownloadChannel",
                "Downloads",
                NotificationManager.IMPORTANCE_LOW
            )
            val notificationManager =
                requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

        }
        auth.getUser { success, user, error ->

            if (success && user != null) {
                binding.txtUserName.text = user.name
                binding.txtUserEmail.text = user.email
                binding.txtUserPhNo.text = user.phNo
                if (user.resume.isEmpty())
                    binding.txtUserResume.text = getString(R.string.upload_resume)
                else {
                    resumeUrl = user.resume
                    binding.txtUserResume.text = getString(R.string.download_resume)
                }
                dialog.dismiss()
            } else {
                Toast.makeText(context, "Error $error", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        binding.txtUserResume.setOnClickListener {
            if (resumeUrl.isEmpty()) {
                uploadResume()
            } else {
                val notificationManagerCompat = NotificationManagerCompat.from(requireContext())
                val areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled()
                if (!areNotificationsEnabled) {
                    // Request permission to show notifications
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    startActivity(intent)
                }else
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        downloadResume(resumeUrl)
                    }
            }

        }


        binding.btnLogout.setOnClickListener {
            auth.logoutUser()
            requireActivity().startActivity(Intent(context, FragmentHolderActivity::class.java))
            requireActivity().finish()
        }

        return binding.root
    }

    private fun progressBar() {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val view = inflater.inflate(R.layout.custome_progress_bar, null)
        builder.setView(view)
        builder.setCancelable(false)
        dialog = builder.create()
        dialog.show()
    }


    private fun uploadResume() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), 505)

    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 505 && resultCode == RESULT_OK) {
            // Get the selected file URI
            val fileUri = data?.data

            auth.uploadResume(fileUri!!) { success, error, _ ->
                if (success) {
                    binding.txtUserResume.text = getString(R.string.download_resume)

                } else
                    Toast.makeText(context, "Error $error", Toast.LENGTH_SHORT).show()
            }

        }
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun downloadResume(uri: String) {
        val storageRef = Firebase.storage.getReferenceFromUrl(uri)

        val filename = "MyResume.pdf"
        val file = File(requireContext().getExternalFilesDir(null), filename)

        val downloadTask = storageRef.getFile(file)

        downloadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()

            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationBuilder = NotificationCompat.Builder(requireContext(), "DownloadChannel")
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle("Downloading Resume")
                .setContentText("Download in progress")
                .setProgress(100, progress, false)
                .setPriority(NotificationCompat.PRIORITY_LOW)

            notificationManager.notify(1, notificationBuilder.build())
        }.addOnSuccessListener {
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1)

            val notificationIntent = Intent(Intent.ACTION_VIEW)
            notificationIntent.setDataAndType(Uri.fromFile(file), "application/pdf")
            notificationIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION

            val contentUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.file-provider", file)
            notificationIntent.setDataAndType(contentUri, "application/pdf")
            notificationIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

            val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)

            val notificationBuilder = NotificationCompat.Builder(requireContext(), "DownloadChannel")
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle("Resume Downloaded")
                .setContentText("Tap to open")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
            notificationManager.notify(2, notificationBuilder.build())
            val snackbar = Snackbar.make(requireView(), "Resume downloaded successfully", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("Open") {
                val intent = Intent(Intent.ACTION_VIEW)
                val contentUris = FileProvider.getUriForFile(
                    requireContext(),
                    "${requireContext().packageName}.file-provider",
                    file
                )
                intent.setDataAndType(contentUris, "application/pdf")
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                startActivity(intent)
            }

            snackbar.show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Resume download failed", Toast.LENGTH_SHORT).show()
        }

    }

}
