package com.example.burnerchat.backend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.backend.socket.MessageModel
import com.example.burnerchat.backend.socket.SocketConnection
import com.example.burnerchat.backend.socket.SocketEvents
import com.example.burnerchat.backend.webrtc.IceCandidateModel
import com.example.burnerchat.backend.webrtc.MessageType
import com.example.burnerchat.backend.webrtc.WebRTCManager
import com.example.burnerchat.business.MainActions
import com.example.burnerchat.business.MainOneTimeEvents
import com.example.burnerchat.business.MainScreenState
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

private const val TAG = "MainViewModel"

class MainViewModel : ViewModel() {

    private val _state = MutableStateFlow(
        MainScreenState()
    )
    val state: StateFlow<MainScreenState>
        get() = _state

    lateinit var newOfferMessage: MessageModel

    private val _oneTimeEvents = MutableSharedFlow<MainOneTimeEvents>()
    val oneTimeEvents: Flow<MainOneTimeEvents>
        get() = _oneTimeEvents.asSharedFlow()

    private val socketConnection = SocketConnection()
    private val gson = Gson()
    private lateinit var rtcManager: WebRTCManager

    init {
        listenToSocketEvents()
    }

    private fun listenToSocketEvents() {
        viewModelScope.launch {
            socketConnection.event.collectLatest {
                when (it) {
                    is SocketEvents.ConnectionChange -> {
                        if (!it.isConnected) {
                            _state.update {
                                state.value.copy(
                                    isConnectedToServer = false,
                                    connectedAs = "",
                                )
                            }
                        }
                    }

                    is SocketEvents.OnSocketMessageReceived -> {
                        handleNewMessage(it.message)
                    }

                    is SocketEvents.ConnectionError -> {
                        Log.d(TAG, "socket ConnectionError ${it.error}")
                    }
                }
            }
        }
    }

    private fun handleNewMessage(message: MessageModel) {
        Log.d(TAG, "handleNewMessage in VM")
        when (message.type) {
            "user_already_exists" -> {
                sendMessageToUi(MessageType.Info("User already exists"))
            }

            "user_stored" -> {
                Log.d(TAG, "User stored in socket")
                sendMessageToUi(MessageType.Info("User stored in socket"))
                _state.update {
                    state.value.copy(
                        isConnectedToServer = true,
                        connectedAs = message.data.toString(),
                    )
                }
            }

            "transfer_response" -> {
                Log.d(TAG, "transfer_response: ")
                // user is online / offline
                if (message.data == null) {
                    sendMessageToUi(MessageType.Info("User is not available"))
                    return
                }
                // important to update target
                rtcManager = WebRTCManager(
                    socketConnection = socketConnection,
                    userName = state.value.connectedAs,
                    target = message.data.toString(),
                )
                consumeEventsFromRTC()
                rtcManager.updateTarget(message.data.toString())
                sendMessageToUi(MessageType.Info("User is Connected to ${message.data}"))
                _state.update {
                    state.value.copy(
                        isConnectToPeer = message.data.toString(),
                    )
                }
                rtcManager.createOffer(
                    from = state.value.connectedAs,
                    target = message.data.toString(),
                )
            }

            "offer_received" -> {
                newOfferMessage = message
                Log.d(TAG, "offer_received ")
                _state.update {
                    state.value.copy(
                        inComingRequestFrom = message.name.orEmpty(),
                    )
                }
                viewModelScope.launch {
                    _oneTimeEvents.emit(
                        MainOneTimeEvents.GotInvite
                    )
                }
            }

            "answer_received" -> {
                val session = SessionDescription(
                    SessionDescription.Type.ANSWER,
                    message.data.toString()
                )
                Log.d(TAG, "onNewMessage: answer received $session")
                rtcManager.onRemoteSessionReceived(session)
            }

            "ice_candidate" -> {
                try {
                    val receivingCandidate = gson.fromJson(
                        gson.toJson(message.data),
                        IceCandidateModel::class.java
                    )
                    Log.d(TAG, "onNewMessage: ice candidate $receivingCandidate")
                    rtcManager.addIceCandidate(
                        IceCandidate(
                            receivingCandidate.sdpMid,
                            Math.toIntExact(receivingCandidate.sdpMLineIndex.toLong()),
                            receivingCandidate.sdpCandidate
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }


    private fun consumeEventsFromRTC() {
        viewModelScope.launch {
            rtcManager.messageStream.collectLatest {
                if (it is MessageType.ConnectedToPeer) {
                    _state.update {
                        state.value.copy(
                            isRtcEstablished = true,
                            peerConnectionString = "Is connected to peer ${state.value.isConnectToPeer}",
                        )
                    }
                }
                if (it is MessageType.MessageByMe) {
                    Log.d(TAG, "consumeEventsFromRTC: ${it.msg}")
                }
                sendMessageToUi(msg = it)
            }
        }
    }

    // This function sends the message of the user to the User Interface
    private fun sendMessageToUi(msg: MessageType) {
        viewModelScope.launch(Dispatchers.IO) {
            _state.update {
                state.value.copy(
                    messagesFromServer = state.value.messagesFromServer + msg,
                )
            }
        }
    }

    fun dispatchAction(actions: MainActions) {
        when (actions) {
            is MainActions.ConnectAs -> {
                // Inicia la conexión con el socket usando el nombre proporcionado
                socketConnection.initSocket(actions.name)
            }

            is MainActions.AcceptIncomingConnection -> {
                // Se crea una sesión de tipo OFFER usando el mensaje recibido de la oferta
                val session = SessionDescription(
                    SessionDescription.Type.OFFER,
                    newOfferMessage.data.toString() // Obtienes la descripción de la oferta
                )

                // Verifica si rtcManager no está inicializado
                if (!::rtcManager.isInitialized) {
                    // Si no está inicializado, creamos una nueva instancia de WebRTCManager
                    rtcManager = WebRTCManager(
                        socketConnection = socketConnection,
                        userName = state.value.connectedAs, // El usuario conectado
                        target = newOfferMessage.name.toString() // Nombre del usuario con el que estamos haciendo la conexión
                    )
                    // Consume los eventos de WebRTC
                    consumeEventsFromRTC()
                }

                // Establecemos la sesión remota con la oferta
                rtcManager.onRemoteSessionReceived(session)

                // Responde a la oferta con el nombre del target (usuario con el que estás haciendo la conexión)
                rtcManager.answerToOffer(newOfferMessage.name.toString())
            }

            is MainActions.ConnectToUser -> {
                // Envía un mensaje de inicio de transferencia usando WebRTC
                socketConnection.sendMessageToSocket(
                    MessageModel(
                        type = "start_transfer",
                        name = state.value.connectedAs, // El usuario que está conectado
                        target = actions.targetName, // El usuario al que deseas conectarte
                        data = null
                    )
                )
            }

            is MainActions.SendChatMessage -> {
                // Envía un mensaje de chat a través de WebRTC
                rtcManager.sendMessage(actions.msg)
            }

            is MainActions.AddIceCandidate -> TODO()
            is MainActions.AnswerOffer -> TODO()
            is MainActions.CreateOffer -> TODO()
        }
    }


}