package com.gyanhub.finde_job.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.gyanhub.finde_job.model.Job
import com.gyanhub.finde_job.model.User

class AuthRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()


    fun registerUser(
        email: String,
        password: String,
        name: String,
        callback: (Boolean, String) -> Unit
    ) {
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = User(name, email, firebaseAuth.uid.toString(),listOf(), listOf())
                    firestore.collection("users").document(firebaseAuth.currentUser!!.uid)
                        .set(user)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                callback(true, "")
                            } else {
                                callback(false, task.exception?.message ?: "Unknown error occurred")
                            }
                        }
                } else {
                    callback(false, task.exception?.message ?: "Unknown error occurred")
                }
            }
    }

    fun loginUser(email: String, password: String, callback: (Boolean, String) -> Unit) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    callback(true, "")
                } else {
                    callback(false, task.exception?.message ?: "Unknown error occurred")
                }
            }
    }

    fun logoutUser() {
        firebaseAuth.signOut()
    }

}