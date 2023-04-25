package com.gyanhub.finde_job.fragments.holder

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.comp.LoaderClass
import com.gyanhub.finde_job.databinding.FragmentPdfLoadBinding
import com.gyanhub.finde_job.viewModle.AuthViewModel
import com.gyanhub.finde_job.viewModle.DbViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

class PdfLoadFragment : Fragment() {
    private var _binding: FragmentPdfLoadBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbModel: DbViewModel
    private lateinit var authModel: AuthViewModel
    private var resumeUrl :String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPdfLoadBinding.inflate(layoutInflater, container, false)
        resumeUrl = arguments?.getString("pdf")!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (isAdded) requireActivity().title = getString(R.string.resume)
        dbModel = ViewModelProvider(requireActivity())[DbViewModel::class.java]
        authModel = ViewModelProvider(requireActivity())[AuthViewModel::class.java]
        loadPdf(resumeUrl!!)
        binding.btnDownload.setOnClickListener {
            val notificationManagerCompat = NotificationManagerCompat.from(requireContext())
            val areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled()
            if (!areNotificationsEnabled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                    intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                    startActivity(intent)
                }
            } else CoroutineScope(Dispatchers.IO).launch {downloadResume(arguments?.getString("pdf")!!) }
        }
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

        binding.btnUpdate.setOnClickListener {
            updateResume()
        }

    }

    private fun loadPdf(url: String) {
      val loading = LoaderClass(requireContext())
        loading.loading("Loading Pdf...")
        loading.showLoder()

        lifecycleScope.launch {
            try {
                val pdfStream = dbModel.viewPdf(url)
                binding.pdfView.fromStream(pdfStream).load()
             loading.hideLoder()
            } catch (e: Exception) {
              loading.hideLoder()
                Toast.makeText(context, "Failed to load PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun downloadResume(uri: String) {
        val storageRef = Firebase.storage.getReferenceFromUrl(uri)
        val filename = "MyResume.pdf"
        val directory =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(directory, filename)
        val downloadTask = storageRef.getFile(file)
        downloadTask.addOnProgressListener { taskSnapshot ->
            val progress =
                (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationBuilder =
                NotificationCompat.Builder(requireContext(), "DownloadChannel")
                    .setSmallIcon(R.drawable.ic_download)
                    .setContentTitle("Downloading Resume")
                    .setContentText("Download in progress")
                    .setProgress(100, progress, false)
                    .setPriority(NotificationCompat.PRIORITY_LOW)

            notificationManager.notify(1, notificationBuilder.build())
        }.addOnSuccessListener {
            val notificationManager =
                requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.cancel(1)
            val notificationIntent = Intent(Intent.ACTION_VIEW)
            notificationIntent.setDataAndType(Uri.fromFile(file), "application/pdf")
            notificationIntent.flags =
                Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            val contentUri = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.fileprovider",
                file
            )

            notificationIntent.setDataAndType(contentUri, "application/pdf")
            notificationIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val pendingIntent = PendingIntent.getActivity(
                context,
                0,
                notificationIntent,
                PendingIntent.FLAG_MUTABLE
            )

            val notificationBuilder =
                NotificationCompat.Builder(requireContext(), "DownloadChannel")
                    .setSmallIcon(R.drawable.ic_download)
                    .setContentTitle("$filename Downloaded")
                    .setContentText("Location: $file")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
            notificationManager.notify(2, notificationBuilder.build())
            Toast.makeText(requireContext(), "Downloaded", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener {
            Toast.makeText(requireContext(), "Resume download failed", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateResume() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        startActivityForResult(Intent.createChooser(intent, "Select PDF"), 505)
    }
    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 505 && resultCode == Activity.RESULT_OK) {
             val loading = LoaderClass(requireContext())
            loading.loading("Updating Resume.... ")
            loading.showLoder()
            val fileUri = data?.data
          CoroutineScope(Dispatchers.IO).launch { authModel.updateResume(resumeUrl!!,fileUri!!) { success, error, url ->
              if (success) {
                  loading.hideLoder()
                  resumeUrl = url
                  loadPdf(resumeUrl!!)

              } else {
                  loading.hideLoder()
                  Toast.makeText(context, "Error $error", Toast.LENGTH_SHORT).show()
              }
          } }

        }
    }
}
