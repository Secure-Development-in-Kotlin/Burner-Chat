package com.example.burnerchat.views

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.burnerchat.backend.WebRTCViewModel
import com.example.burnerchat.business.MainActions
import com.example.burnerchat.business.MainOneTimeEvents
import com.example.burnerchat.business.MainScreenState
import com.example.burnerchat.webRTC.backend.webrtc.MessageType
import com.example.burnerchat.webRTC.views.theme.Black
import com.example.burnerchat.webRTC.views.theme.BurnerChatTheme
import com.example.burnerchat.webRTC.views.theme.Green
import com.example.burnerchat.webRTC.views.theme.SoftRed

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

private const val TAG = "WebRTCActivity"

class WebRTCActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BurnerChatTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun <T> rememberFlowWithLifecycle(
    flow: Flow<T>,
    lifecycle: Lifecycle = androidx.lifecycle.compose.LocalLifecycleOwner.current.lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
): Flow<T> = remember(flow, lifecycle, minActiveState) {
    flow.flowWithLifecycle(
        lifecycle = lifecycle,
        minActiveState = minActiveState,
    )
}

@Composable
fun MainScreen() {
    val viewModel = viewModel(modelClass = WebRTCViewModel::class.java)
    val state by viewModel.state.collectAsState()
    val events = rememberFlowWithLifecycle(flow = viewModel.oneTimeEvents)

    var showUserInputDialog by remember { mutableStateOf(true) } // Estado para mostrar el diálogo inicial
    var showConnectionDialog by remember { mutableStateOf(false) } // Estado para mostrar el diálogo de conexión
    var userName by remember { mutableStateOf("") } // Estado para almacenar el nombre del usuario
    var connectToName by remember { mutableStateOf("") } // Nombre de la persona a la que se quiere conectar

    // Obtener el contexto actual para usar en cancelación
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Black) // Fondo negro para toda la pantalla
    ) {
        if (showUserInputDialog) {
            UserNameInputDialog(
                onConfirm = { enteredName ->
                    userName = enteredName
                    viewModel.dispatchAction(MainActions.ConnectAs(userName))
                    showUserInputDialog = false
                    showConnectionDialog =
                        true // Mostrar el diálogo de conexión después de ingresar el nombre
                },
                onCancel = {
                    // Usar el contexto para finalizar la actividad
                    (context as? ComponentActivity)?.finish()
                }
            )
        } else if (showConnectionDialog) {
            ConnectToUserDialog(
                onConfirm = { userToConnect ->
                    connectToName = userToConnect
                    viewModel.dispatchAction(MainActions.ConnectToUser(connectToName))
                    showConnectionDialog = false
                },
                onCancel = {
                    showConnectionDialog = false // Cerrar el diálogo de conexión si se cancela
                }
            )
        } else {
            HomeScreenContent(
                state = state,
                dispatchAction = { viewModel.dispatchAction(it) }
            )
        }
    }
}

@Composable
fun ConnectToUserDialog(
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    var tempConnectTo by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { /* No se puede cerrar sin confirmar */ }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Black, shape = RoundedCornerShape(10.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Introduce el nombre de la persona con la que deseas conectar:",
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            TextField(
                value = tempConnectTo,
                onValueChange = { tempConnectTo = it },
                placeholder = { Text(text = "Nombre de usuario a conectar") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onCancel, // Llamada a función de cancelación
                    colors = ButtonDefaults.buttonColors(containerColor = SoftRed)
                ) {
                    Text(text = "Cancelar", color = Color.Black)
                }
                Button(
                    onClick = {
                        if (tempConnectTo.isNotBlank()) {
                            onConfirm(tempConnectTo.trim()) // Llamada a función para confirmar
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Green)
                ) {
                    Text(text = "Conectar", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun UserNameInputDialog(
    onConfirm: (String) -> Unit,
    onCancel: () -> Unit
) {
    var tempUserName by remember { mutableStateOf("") }

    Dialog(onDismissRequest = { /* No se puede cerrar sin confirmar */ }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = Black, shape = RoundedCornerShape(10.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Introduce tu nombre de usuario",
                color = Color.White,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            TextField(
                value = tempUserName,
                onValueChange = { tempUserName = it },
                placeholder = { Text(text = "Nombre de usuario") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onCancel, // Llamada a función normal
                    colors = ButtonDefaults.buttonColors(containerColor = SoftRed)
                ) {
                    Text(text = "Cancelar", color = Color.Black)
                }
                Button(
                    onClick = {
                        if (tempUserName.isNotBlank()) {
                            onConfirm(tempUserName.trim()) // Llamada a función normal
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Green)
                ) {
                    Text(text = "Confirmar", color = Color.Black)
                }
            }
        }
    }
}

@Composable
fun HomeScreenContent(
    state: MainScreenState,
    dispatchAction: (MainActions) -> Unit = {},
) {
    var yourName by remember { mutableStateOf("") }
    var connectTo by remember { mutableStateOf("") }
    var chatMessage by remember { mutableStateOf("") }

    fun changeConnect(name: String) {
        connectTo = name
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Black)
    ) {
        // La zona de mensajes con LazyColumn ocupará el espacio restante
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f) // Hace que el LazyColumn ocupe all el espacio disponible
                .padding(bottom = 8.dp), // Añade un pequeño margen inferior
            content = {
                // Tu contenido de LazyColumn como antes
                item {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        content = {
                            item {
                                Text(
                                    text = state.inComingRequestFrom ?: "Nombre del pibardo",
                                    color = Color.White,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                            item {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 16.dp, top = 8.dp)
                                        .background(
                                            color = if (state.isRtcEstablished) Green else SoftRed, // Cambia el color según isRtcEstablished
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(10.dp)
                                ) {
                                    Text(
                                        text = "Server",
                                        color = Color.Black,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    )
                }
                items(state.messagesFromServer.size) {
                    val current = state.messagesFromServer[it]
                    when (current) {
                        is MessageType.Info -> {
                            Text(
                                color = Color.White,
                                text = current.msg,
                                modifier = Modifier
                                    .padding(top = 10.dp, start = 10.dp)
                                    .fillMaxWidth()
                            )
                        }

                        is MessageType.MessageByMe -> {
                            Row(
                                modifier = Modifier
                                    .padding(top = 10.dp, start = 10.dp)
                                    .fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                                Text(
                                    text = current.msg,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .background(
                                            color = Green,
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(8.dp),
                                    color = Color.Black
                                )
                            }
                        }

                        is MessageType.MessageByPeer -> {
                            Row(
                                modifier = Modifier
                                    .padding(top = 10.dp, start = 10.dp)
                                    .fillMaxWidth()
                            ) {
                                Text(
                                    text = current.msg,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .background(
                                            color = Color(0xFF58BDDB),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(8.dp),
                                    color = Color.Black
                                )
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                )
                            }
                        }

                        else -> {}
                    }
                }
            }
        )

        // La zona de chat en la parte inferior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Gray)
                .padding(8.dp)
        ) {
            if (state.isRtcEstablished) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 10.dp, bottom = 10.dp, start = 10.dp)
                ) {
                    TextField(
                        modifier = Modifier.weight(1f),
                        value = chatMessage,
                        onValueChange = { chatMessage = it },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color(0xFFE5E4E2),
                            unfocusedContainerColor = Color(0xFFE5E4E2)
                        ),
                        shape = RoundedCornerShape(15.dp)
                    )
                    Button(
                        onClick = {
                            dispatchAction(MainActions.SendChatMessage(chatMessage));
                            chatMessage = ""
                        },
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Green
                        )
                    ) {
                        Text(text = "Chat")
                    }
                }
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    TextField(
                        modifier = Modifier.weight(1f),
                        value = if (state.connectedAs.isNotEmpty()) connectTo else yourName,
                        onValueChange = {
                            if (state.connectedAs.isNotEmpty()) {
                                connectTo = it
                            } else {
                                yourName = it
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            unfocusedContainerColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(15.dp)
                    )
                    Button(
                        onClick = {
                            if (state.connectedAs.isNotEmpty()) {
                                dispatchAction(MainActions.ConnectToUser(connectTo))
                            } else {
                                dispatchAction(MainActions.ConnectAs(yourName))
                            }
                        },
                        modifier = Modifier.padding(start = 10.dp, end = 10.dp),
                        colors = ButtonDefaults.buttonColors(
                            contentColor = Color.Black,
                            containerColor = Green
                        )
                    ) {
                        Text(text = "Connect")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun DialogForIncomingRequestPreview() {
    BurnerChatTheme {
        DialogForIncomingRequest(
            onAccept = {},
            onDismiss = {},
            inviteFrom = "Mr. X"
        )
    }
}

@Composable
fun DialogForIncomingRequest(
    onDismiss: () -> Unit = {},
    onAccept: () -> Unit = {},
    inviteFrom: String,
) {

    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = Color.Black
                )
                .padding(
                    8.dp,
                ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "You got invite from $inviteFrom",
                color = Color.White
            )
            Row(
                modifier = Modifier
                    .padding(
                        horizontal = 20.dp,
                    )
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Button(
                    onClick = onDismiss,
                    modifier = Modifier.padding(
                        vertical = 10.dp,
                    ),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = SoftRed
                    ),
                ) {
                    Text(text = "Reject")
                }
                Button(
                    onClick = onAccept,
                    modifier = Modifier.padding(

                        vertical = 10.dp,
                    ),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = Green
                    ),
                ) {
                    Text(text = "Accept")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BurnerChatTheme {
        val state by remember {
            mutableStateOf(MainScreenState.forPreview())
        }
        HomeScreenContent(
            state = state,
        )
    }
}