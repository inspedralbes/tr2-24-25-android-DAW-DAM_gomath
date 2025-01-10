package com.example.gomath.ui.screens

import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PauseCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gomath.R
import com.example.gomath.model.User
import com.example.gomath.model.Users
import com.example.gomath.ui.GoMathApp
import com.example.gomath.ui.GoMathViewModel

@Composable
fun MandoScreen(viewModel: GoMathViewModel, navController: NavHostController) {
    var clickedButton by remember { mutableStateOf<ButtonType?>(null) }
    val users by viewModel.users.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.user_control),
            fontSize = 32.sp,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            // Botón para expulsar al usuario
            IconButton(
                onClick = {
                    viewModel.resetCode()
                    navController.navigate(GoMathApp.Code.name)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Sortir de la Sala",
                    tint = Color.Blue // Color rojo para indicar acción de expulsión
                )
            }
            Text(
                text = "Control de Usuarios",
                fontSize = 32.sp,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        LlistaRoom(users = users, viewModel = viewModel, modifier = Modifier.weight(1f))

        Spacer(modifier = Modifier.height(16.dp))

        ControlButton(
            text = stringResource(R.string.stop),
            icon = Icons.Filled.PauseCircle,
            isClicked = clickedButton == ButtonType.PAUSE
        ) {
            clickedButton = ButtonType.PAUSE
            viewModel.pause()
        }
    }
}

@Composable
fun LlistaRoom(users: Users, viewModel: GoMathViewModel, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp), // Mejor espaciado en la lista
            verticalArrangement = Arrangement.spacedBy(12.dp) // Espacio mayor entre usuarios
        ) {
            items(users.members) { user ->
                UserIndividual(
                    user = user,
                    users = users,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun UserIndividual(user: User, users: Users, viewModel: GoMathViewModel, modifier: Modifier = Modifier) {
    Log.d("users", "user:" + user.name)

    // Cambié los colores a un esquema más armónico con gradientes suaves
    val colors = listOf(
        Color(0xFF00459A)  // Light Orange
    )
    val backgroundColor = colors[user.name.hashCode() % colors.size]

    Box(
        modifier = Modifier
            .padding(8.dp) // Se aumentó el espaciado entre las tarjetas
            .fillMaxWidth()
            .height(90.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor, shape = MaterialTheme.shapes.large), // Forma más redondeada
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp // Sombras suaves para más profundidad
            ),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor,
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium // Bordes redondeados para un diseño más moderno
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp), // Padding interno más amplio
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleLarge, // Usando titleLarge como alternativa
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "¡Hola!",
                    style = MaterialTheme.typography.bodyMedium, // Usando bodyMedium como alternativa
                    color = Color.White.copy(alpha = 0.7f) // Menor opacidad para el saludo
                )
            }
        }

        // Botón para expulsar al usuario
        IconButton(
            onClick = {
                viewModel.kickUserFromRoom(user)
            },
            modifier = Modifier
                .align(Alignment.TopEnd) // Alinear en la esquina superior derecha
                .padding(8.dp) // Separación del borde
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Expulsar usuario",
                tint = Color.Red // Color rojo para indicar acción de expulsión
            )
        }
    }
}



@Composable
fun ControlButton(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isClicked: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (isClicked) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.primary
    )

    val scale by animateFloatAsState(
        targetValue = if (isClicked) 1.2f else 1f
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .background(backgroundColor, shape = MaterialTheme.shapes.medium)
            .padding(20.dp) // Espaciado más amplio en el botón
            .scale(scale),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(28.dp) // Icono más grande para mejor visibilidad
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 20.sp, // Mayor tamaño de texto
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
        )
    }
}

enum class ButtonType {
    REWIND, PAUSE, FORWARD
}
