package com.gyanhub.finde_job.repository

import android.app.ProgressDialog
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.gyanhub.finde_job.R
import com.gyanhub.finde_job.activity.MainActivity
import com.gyanhub.finde_job.model.User
import java.util.UUID

class LoginWithGoogle(private val fragment: Fragment) {

    private lateinit var signInClient: GoogleSignInClient
    private lateinit var signInOptions: GoogleSignInOptions
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    init {
        setupGoogleLogin()
    }

    private fun setupGoogleLogin() {
        signInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(fragment.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        signInClient = GoogleSignIn.getClient(fragment.requireActivity(), signInOptions)
    }

    fun login() {
        val loginIntent: Intent = signInClient.signInIntent
        fragment.startActivityForResult(loginIntent, 1)
    }

    fun handleActivityResult(requestCode: Int, data: Intent?) {
        if (requestCode == 1) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null) {
                    googleFirebaseAuth(account)
                }
            } catch (e: ApiException) {
                Log.e("ANKIT", "error $e")
                Toast.makeText(
                    fragment.requireContext(),
                    "Google sign in failed:(",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun forgotPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        fragment.requireContext(),
                        "Email sent to reset password",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        fragment.requireContext(),
                        "Failed to send email to reset password",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    private fun googleFirebaseAuth(acct: GoogleSignInAccount) {
        val progressDialog = ProgressDialog(fragment.requireContext())
        progressDialog.setMessage("Login...")
        progressDialog.setCancelable(false)
        progressDialog.show()
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val currentUser = auth.currentUser
                currentUser?.linkWithCredential(credential)
                auth.signInWithCredential(credential).addOnCompleteListener {
                    if (it.isSuccessful) {
                        firestore.collection("users").document(auth.uid.toString()).get().addOnSuccessListener { document ->
                            if (document.exists()) {
                                fragment.startActivity(Intent(fragment.requireActivity(), MainActivity::class.java))
                                progressDialog.dismiss()
                            } else {
                                val user = User(acct.givenName.toString(), acct.email.toString(), auth.uid.toString(), "", "", listOf(), listOf(), UUID.randomUUID().toString())
                                firestore.collection("users").document(auth.currentUser!!.uid)
                                    .set(user)
                                    .addOnCompleteListener { tasks ->
                                        if (tasks.isSuccessful) {
                                            progressDialog.dismiss()
                                            fragment.startActivity(Intent(fragment.requireActivity(), MainActivity::class.java))

                                        } }

                            }
                        }
                    } else {
                        Log.e("ANKIT", "error ${it.exception}")
                        progressDialog.dismiss()
                        Toast.makeText(
                            fragment.requireContext(),
                            "Google sign in failed:(",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Log.e("ANKIT", "error ${task.exception}")
                Toast.makeText(
                    fragment.requireContext(),
                    "Google sign in failed:(",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}
