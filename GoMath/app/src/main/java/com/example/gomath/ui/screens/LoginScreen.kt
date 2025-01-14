package com.example.gomath.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gomath.ui.GoMathApp
import com.example.gomath.ui.GoMathViewModel

@Composable
fun LoginScreen(viewModel: GoMathViewModel, navController: NavHostController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var showError by remember { mutableStateOf(false) }
    var loginSuccess by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Fondo animado
    val infiniteTransition = rememberInfiniteTransition()
    val offsetAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(6000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.primary
                    ),
                    startY = offsetAnimation
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = !loginSuccess,
            enter = fadeIn(animationSpec = tween(1000)) + expandVertically(),
            exit = fadeOut(animationSpec = tween(800)) + shrinkVertically()
        ) {
            Card(
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(8.dp),
                modifier = Modifier
                    .padding(horizontal = 24.dp)
                    .fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Benvingut/da",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Campo de correo electrónico
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Correu electrònic") },
                        placeholder = { Text("exemple@correu.com") },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email")
                        },
                        singleLine = true,
                        isError = showError && email.isEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Campo de contraseña
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Contrasenya") },
                        placeholder = { Text("******") },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Contrasenya")
                        },
                        visualTransformation = if (passwordVisible)
                            VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = "Mostra o amaga la contrasenya"
                                )
                            }
                        },
                        singleLine = true,
                        isError = showError && password.isEmpty(),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botón de inicio de sesión
                    Button(
                        onClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                showError = false
                                viewModel.login(email, password, context) { isAllowed ->
                                    if (isAllowed) {
                                        navController.navigate(GoMathApp.Code.name)
                                    } else {
                                        Toast.makeText(context, "Accés denegat. Aquest compte no té permisos suficients.", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            } else {
                                showError = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .animateContentSize(animationSpec = tween(500)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "Inicia Sessió",
                            style = MaterialTheme.typography.labelLarge,
                            color = Color.White
                        )
                    }

                    // Mensaje de error
                    AnimatedVisibility(
                        visible = showError,
                        enter = fadeIn(animationSpec = tween(500)) + slideInVertically(),
                        exit = fadeOut(animationSpec = tween(500)) + slideOutVertically()
                    ) {
                        Text(
                            text = "Si us plau, completa tots els camps",
                            color = MaterialTheme.colorScheme.error,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 16.dp)
                        )
                    }
                }
            }
        }

        // Animación de éxito
        AnimatedVisibility(
            visible = loginSuccess,
            enter = scaleIn(animationSpec = tween(1000)) + fadeIn(animationSpec = tween(1000)),
            exit = fadeOut(animationSpec = tween(1000))
        ) {
            Text(
                text = "Inici de sessió completat! 🎉",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

// Función para manejar el inicio de sesión
fun handleLogin(context: Context) {
    Toast.makeText(context, "Iniciant sessió...", Toast.LENGTH_SHORT).show()
}