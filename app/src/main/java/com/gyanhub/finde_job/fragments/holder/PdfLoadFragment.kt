package com.gyanhub.finde_job.fragments.holder

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.util.Log
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.databinding.FragmentPdfLoadBinding
import com.gyanhub.finde_job.viewModle.DbViewModel
import kotlinx.coroutines.launch
import java.io.File

class PdfLoadFragment : Fragment() {
    private var _binding: FragmentPdfLoadBinding? = null
    private val binding get() = _binding!!
    private lateinit var dbModel: DbViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPdfLoadBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbModel = ViewModelProvider(requireActivity())[DbViewModel::class.java]
        loadPdf(arguments?.getString("pdf")!!)
        binding.btnDownload.setOnClickListener {
            val notificationManagerCompat = NotificationManagerCompat.from(requireContext())
            val areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled()
            if (!areNotificationsEnabled) {
              if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                  val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                  intent.putExtra(Settings.EXTRA_APP_PACKAGE, requireContext().packageName)
                  startActivity(intent)
              }
            } else downloadResume(arguments?.getString("pdf")!!)
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("DownloadChannel", "Downloads", NotificationManager.IMPORTANCE_LOW)
            val notificationManager = requireActivity().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

    }

    private fun loadPdf(url: String) {
        val progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Loading PDF...")
        progressDialog.setCancelable(false)
        progressDialog.show()

        lifecycleScope.launch {
            try {
                val pdfStream = dbModel.viewPdf(url)
                binding.pdfView.fromStream(pdfStream).load()
                progressDialog.dismiss()
            } catch (e: Exception) {
                progressDialog.dismiss()
                Toast.makeText(context, "Failed to load PDF", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun downloadResume(uri: String) {
        val storageRef = Firebase.storage.getReferenceFromUrl(uri)
        val filename = "MyResume.pdf"
        val directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(directory, filename)
        val downloadTask = storageRef.getFile(file)
        downloadTask.addOnProgressListener { taskSnapshot ->
            val progress = (100.0 * taskSnapshot.bytesTransferred / taskSnapshot.totalByteCount).toInt()
            val notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val notificationBuilder =
                NotificationCompat.Builder(requireContext(), "DownloadChannel")
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
            val contentUri = FileProvider.getUriForFile(requireContext(), "${requireContext().packageName}.fileprovider", file)

            notificationIntent.setDataAndType(contentUri, "application/pdf")
            notificationIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            val pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_MUTABLE)

            val notificationBuilder =
                NotificationCompat.Builder(requireContext(), "DownloadChannel")
                    .setSmallIcon(R.drawable.ic_download)
                    .setContentTitle("$filename Downloaded")
                    .setContentText("Location: $file")
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
            notificationManager.notify(2, notificationBuilder.build())
            val snackbar = Snackbar.make(requireView(), "Resume downloaded successfully", Snackbar.LENGTH_INDEFINITE)
            snackbar.setAction("Open") { val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(contentUri, "application/pdf")
                intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                startActivity(intent)
            }
            snackbar.show()
        }.addOnFailureListener { Toast.makeText(requireContext(), "Resume download failed", Toast.LENGTH_SHORT).show() }
    }

}
