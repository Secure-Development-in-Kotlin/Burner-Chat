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
    var showLoginDialog by remember { mutableStateOf(true) }
    var username by remember { mutableStateOf("") }
    var showIncomingRequestDialog by remember {
        mutableStateOf(false)
    }

    // Lógica para manejar eventos de una sola vez
    LaunchedEffect(
        key1 = events,
        block = {
            events.collectLatest {
                when (it) {
                    is MainOneTimeEvents.GotInvite -> {
                        showIncomingRequestDialog = true
                    }
                }
            }
        },
    )

    // Mostrar Dialog de inicio de sesión si no se ha logueado
    if (showLoginDialog) {
        LoginDialog(
            username = username,
            onUsernameChange = { username = it },
            onLogin = {
                viewModel.dispatchAction(MainActions.ConnectAs(username))
                showLoginDialog = false
            }
        )
    }
    if (showIncomingRequestDialog) {
        DialogForIncomingRequest(
            onDismiss = {
                showIncomingRequestDialog = false
            },
            onAccept = {
                viewModel.dispatchAction(
                    MainActions.AcceptIncomingConnection
                )
                showIncomingRequestDialog = false
            },
            inviteFrom = state.inComingRequestFrom,
        )
    } else {
        // Mostrar el contenido principal
        HomeScreenContent(
            state = state,
            dispatchAction = {
                viewModel.dispatchAction(it)
            }
        )
    }
}

@Composable
fun LoginDialog(
    username: String,
    onUsernameChange: (String) -> Unit,
    onLogin: () -> Unit
) {
    Dialog(onDismissRequest = {}) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Black, RoundedCornerShape(16.dp))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Enter your name to connect",
                color = Color.White,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = username,
                onValueChange = onUsernameChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                label = { Text("Username") },
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                )
            )
            Button(
                onClick = onLogin,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Green,
                    contentColor = Color.White
                )
            ) {
                Text("Connect")
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
                .weight(1f) // Hace que el LazyColumn ocupe todo el espacio disponible
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
                                    text = state.inComingRequestFrom, // Mostrar isConnectToPeer o texto predeterminado,
                                    color = Color.White,
                                    modifier = Modifier.padding(start = 16.dp)
                                )
                            }
                            item {
                                Box(
                                    modifier = Modifier
                                        .padding(end = 16.dp, top = 8.dp)
                                        .background(
                                            color = if (state.isConnectedToServer) Green else SoftRed, // Cambia el color según isRtcEstablished
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
                    when (val current = state.messagesFromServer[it]) {
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
                            dispatchAction(MainActions.SendChatMessage(chatMessage))
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
                            focusedIndicatorColor = Black,
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
                        Text(text = "GO")
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