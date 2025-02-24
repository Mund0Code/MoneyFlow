package com.mundocode.moneyflow.ui.screens.auth

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mundocode.moneyflow.core.User


class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    private val _userRole = MutableLiveData<String?>()
    val userRole: LiveData<String?> = _userRole

    private val sharedPreferences = application.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun obtenerRolUsuario() {
        val userId = auth.currentUser?.uid
        userId?.let {
            db.collection("users").document(it).get()
                .addOnSuccessListener { document ->
                    _userRole.value = document.getString("role") ?: "Usuario"
                }
                .addOnFailureListener {
                    _userRole.value = "Usuario"
                }
        }
    }



    fun cambiarRol(userId: String, newRole: String) {
        db.collection("users").document(userId)
            .update("role", newRole)
            .addOnSuccessListener {
                obtenerRolUsuario()
            }
    }

    fun obtenerUsuarios(onResult: (List<User>) -> Unit) {
        db.collection("users").get().addOnSuccessListener { result ->
            val users = result.documents.map { doc ->
                User(
                    id = doc.id,
                    email = doc.getString("email") ?: "",
                    role = doc.getString("role") ?: "user"
                )
            }
            onResult(users)
        }
    }

    fun iniciarSesion(email: String, password: String, onResult: (Boolean) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    obtenerRolUsuario() // ✅ Obtener el rol después del login
                    onResult(true)
                } else {
                    onResult(false)
                }
            }
    }

    fun registrarUsuario(
        email: String,
        password: String,
        role: String,
        onResult: (Boolean) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    userId?.let {
                        val userData = mapOf("role" to role, "email" to email)
                        db.collection("users").document(it).set(userData)
                            .addOnSuccessListener { onResult(true) }
                            .addOnFailureListener { onResult(false) }
                    }
                } else {
                    onResult(false)
                }
            }
    }

    fun cerrarSesion() {
        auth.signOut()
    }
}