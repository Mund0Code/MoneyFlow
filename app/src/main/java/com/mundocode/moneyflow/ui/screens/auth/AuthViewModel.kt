package com.mundocode.moneyflow.ui.screens.auth

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : ViewModel() {

    fun obtenerRolUsuario() {
        val userId = auth.currentUser?.uid
        userId?.let {
            firestore.collection("users").document(it).get()
        }
    }

    fun iniciarSesion(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    obtenerRolUsuario()
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }

    fun registrarUsuario(
        email: String,
        password: String,
        onResult: (Boolean) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    userId?.let {
                        val userData = mapOf("email" to email)
                        firestore.collection("users").document(it).set(userData)
                            .addOnSuccessListener { onResult(true) }
                            .addOnFailureListener { onResult(false) }
                    }
                } else {
                    onResult(false)
                }
            }
    }
}
