package com.mundocode.moneyflow.ui.screens.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController

@Composable
fun RegisterScreen(viewModel: AuthViewModel = viewModel(), navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("Usuario") }
    var error by remember { mutableStateOf<String?>(null) }
    val roles = listOf("Usuario", "Admin", "Invitado")

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            RegisterContent(
                registrarUsuario = {
                    if (password == confirmPassword) {
                        viewModel.registrarUsuario(email, password, selectedRole) { success ->
                            if (success) {
                                navController.navigate("home")
                            } else {
                                error = "Error al registrar usuario"
                            }
                        }
                    } else {
                        error = "Las contraseñas no coinciden"
                    }
                },
                email = email,
                password = password,
                confirmPassword = confirmPassword,
                selectedRole = selectedRole,
                error = error,
                onValueChangeEmail = { email = it },
                onValueChangePassword = { password = it },
                onValueChangeConfirmPassword = { confirmPassword = it },
                onRoleSelected = { selectedRole = it },
                roles = roles,
                navController = { navController.navigate("login") }
            )
        }
    }
}

@Composable
fun RegisterContent(
    registrarUsuario: () -> Unit,
    email: String,
    password: String,
    confirmPassword: String,
    selectedRole: String,
    error: String?,
    onValueChangeEmail: (String) -> Unit,
    onValueChangePassword: (String) -> Unit,
    onValueChangeConfirmPassword: (String) -> Unit,
    onRoleSelected: (String) -> Unit,
    roles: List<String>,
    navController: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 32.dp)
            .shadow(8.dp, shape = RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icono de la app o logo
            Icon(
                imageVector = Icons.Default.PersonAdd,
                contentDescription = "Registro",
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text("Crear Cuenta", fontSize = 22.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de correo con icono
            OutlinedTextField(
                value = email,
                onValueChange = onValueChangeEmail,
                label = { Text("Correo electrónico") },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "Email Icon")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de contraseña con icono
            OutlinedTextField(
                value = password,
                onValueChange = onValueChangePassword,
                label = { Text("Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Password Icon")
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Campo de confirmar contraseña con icono
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = onValueChangeConfirmPassword,
                label = { Text("Confirmar Contraseña") },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Confirm Password Icon")
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Selector de Rol de Usuario
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Seleccionar Rol", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    roles.forEach { role ->
                        OutlinedButton(
                            onClick = { onRoleSelected(role) },
                            border = BorderStroke(1.dp, if (selectedRole == role) Color.Blue else Color.Gray),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (selectedRole == role) Color.Blue else Color.Gray
                            )
                        ) {
                            Text(role)
                        }
                    }
                }
            }

            // Mensaje de error
            error?.let {
                Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón de Registro
            Button(
                enabled = email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank(),
                onClick = registrarUsuario,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Registrarse", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Opción para ir a Login
            Row {
                Text("¿Ya tienes una cuenta?", fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Iniciar Sesión",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable {
                        navController()
                    }
                )
            }
        }
    }
}
