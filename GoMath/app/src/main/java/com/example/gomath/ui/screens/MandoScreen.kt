package com.example.gomath.ui.screens

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.gomath.R
import com.example.gomath.model.Game
import com.example.gomath.model.User
import com.example.gomath.model.Users
import com.example.gomath.ui.GoMathApp
import com.example.gomath.ui.GoMathViewModel
import kotlinx.coroutines.delay

@Composable
fun MandoScreen(viewModel: GoMathViewModel, navController: NavHostController) {
    var clickedButton by remember { mutableStateOf<ButtonType?>(null) }
    val users by viewModel.users.collectAsState()
    val gameData by viewModel.gameData.collectAsState()

    var timeRemaining by remember { mutableStateOf(gameData?.cantidad ?: 30) }
    var isPaused by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = gameData?.cantidad, key2 = isPaused) {
        if (gameData?.modo == "crono" && !isPaused) {
            if (timeRemaining == gameData?.cantidad) {
                timeRemaining = gameData?.cantidad ?: 30
            }

            while (timeRemaining > 0 && !isPaused) {
                delay(1000L)
                timeRemaining -= 1
            }
        }
    }

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
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Row(modifier = Modifier.align(Alignment.CenterHorizontally)) {
            IconButton(
                onClick = {
                    viewModel.resetCode()
                    navController.navigate(GoMathApp.Code.name)
                }
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Sortir de la Sala",
                    tint = Color.Blue
                )
            }
            Text(
                text = "Control de Usuarios",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (gameData?.modo == "crono") {
            Text(
                text = "Tiempo restante: $timeRemaining segundos",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        gameData?.let { LlistaRoom(users = users, game = it, viewModel = viewModel, modifier = Modifier.weight(1f)) }

        Spacer(modifier = Modifier.height(16.dp))

        ControlButton(
            text = stringResource(R.string.stop),
            icon = Icons.Filled.PauseCircle,
            isClicked = isPaused
        ) {
            isPaused = !isPaused
            clickedButton = if (isPaused) ButtonType.PAUSE else null
        }
    }
}

@Composable
fun LlistaRoom(users: Users, game: Game, viewModel: GoMathViewModel, modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(users.members) { user ->
                UserIndividual(
                    user = user,
                    users = users,
                    game = game,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun UserIndividual(user: User, users: Users, game: Game, viewModel: GoMathViewModel, modifier: Modifier = Modifier) {
    val colors = listOf(
        Color(0xFF00459A)
    )
    val backgroundColor = colors[user.name.hashCode() % colors.size]

    Box(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .height(90.dp)
    ) {
        Card(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor, shape = MaterialTheme.shapes.large),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 8.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = backgroundColor,
                contentColor = Color.White
            ),
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = user.name,
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (game.modo == "numero") {
                        stringResource(R.string.calculation, game.cantidad)
                    } else if (game.modo == "fallos") {
                        stringResource(R.string.opportunities, game.cantidad)
                    } else {
                        ""
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.7f)
                )

            }
        }

        IconButton(
            onClick = {
                viewModel.kickUserFromRoom(user)
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Expulsar usuario",
                tint = Color.Red
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
            .padding(20.dp)
            .scale(scale),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = text,
            color = Color.White,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

enum class ButtonType {
    PAUSE
}
