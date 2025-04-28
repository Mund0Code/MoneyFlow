package com.mundocode.moneyflow.ui.screens.auth

import android.os.Build
import androidx.annotation.RequiresApi
import com.mundocode.moneyflow.R
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mundocode.moneyflow.LanguageViewModel
import com.mundocode.moneyflow.ui.screens.onBoarding.OnboardingViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.P)
@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onboardingViewModel: OnboardingViewModel = hiltViewModel(),
    navController: NavController,
    langViewModel: LanguageViewModel = hiltViewModel(),
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var error by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var estado by remember { mutableStateOf(context.getString(R.string.language)) }

    Scaffold { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            LoginContent(
                iniciarSesion = {
                    viewModel.iniciarSesion(email, password) { success ->
                        if (success) {
                            scope.launch {
                                val isCompleted = onboardingViewModel.isCompleted.first()
                                if (isCompleted) {
                                    navController.navigate("home") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                } else {
                                    navController.navigate("onboarding") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                }
                            }
                        } else {
                            error = context.getString(R.string.error_login_user)
                        }
                    }
                },
                email = email,
                password = password,
                error = error,
                onValueChangeE = { email = it },
                onValueChangeP = { password = it },
                navcontroller = { navController.navigate("register") },
                languageEsViewModel = {
                    langViewModel.changeLanguage("es")
                    langViewModel.restartApp(context)
                },
                languageEnViewModel = {
                    langViewModel.changeLanguage("en")
                    langViewModel.restartApp(context)
                },
                languageDeViewModel = {
                    langViewModel.changeLanguage("de")
                    langViewModel.restartApp(context)
                },
                estado = estado
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginContent(
    iniciarSesion: () -> Unit,
    email: String,
    password: String,
    error: String?,
    onValueChangeE: (String) -> Unit,
    onValueChangeP: (String) -> Unit,
    navcontroller: () -> Unit,
    languageEsViewModel: () -> Unit,
    languageEnViewModel: () -> Unit,
    languageDeViewModel: () -> Unit,
    estado: String,
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

            Box(
                modifier = Modifier
                    .width(150.dp)
                    .padding(bottom = 16.dp).align(Alignment.End),
            ) {

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    OutlinedTextField(
                        value = estado,
                        onValueChange = {}, // No editable manualmente
                        readOnly = true,
                        label = { Text(stringResource(R.string.lang)) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor() // IMPORTANTE: esto sí se necesita
                    )

                    DropdownMenu(
                        modifier = Modifier.width(150.dp),
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Español") },
                            onClick = {
                                languageEsViewModel()
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("English") },
                            onClick = {
                                languageEnViewModel()
                                expanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Deutsch") },
                            onClick = {
                                languageDeViewModel()
                                expanded = false
                            }
                        )
                    }
                }
            }

            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "Login",
                modifier = Modifier
                    .size(80.dp)
                    .padding(bottom = 16.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Text(stringResource(R.string.login), fontSize = 22.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = onValueChangeE,
                label = { Text(stringResource(R.string.email)) },
                leadingIcon = {
                    Icon(Icons.Default.Email, contentDescription = "Email Icon")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = password,
                onValueChange = onValueChangeP,
                label = { Text(stringResource(R.string.password)) },
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = "Password Icon")
                },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            error?.let {
                Text(it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                enabled = email.isNotBlank() && password.isNotBlank(),
                onClick = iniciarSesion,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(stringResource(R.string.login), fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row {
                Text(stringResource(R.string.no_account), fontSize = 14.sp)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    "Regístrate",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { navcontroller() }
                )
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    LoginContent(
        iniciarSesion = {},
        email = "",
        password = "",
        error = null,
        onValueChangeE = {},
        onValueChangeP = {},
        navcontroller = {},
        languageEsViewModel = {},
        languageEnViewModel = {},
        languageDeViewModel = {},
        estado = "Español"
    )
}