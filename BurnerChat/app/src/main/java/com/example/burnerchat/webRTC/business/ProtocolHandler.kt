package com.example.burnerchat.webRTC.business

import android.util.Log
import androidx.lifecycle.LifecycleCoroutineScope
import com.example.burnerchat.webRTC.backend.webrtc.MessageType
import com.example.burnerchat.webRTC.business.handlers.MessageHandler
import com.example.burnerchat.webRTC.business.handlers.RTCManagerHandler
import com.example.burnerchat.webRTC.business.handlers.SocketHandler
import com.example.burnerchat.webRTC.model.users.KeyPair
import com.google.firebase.auth.FirebaseUser

// Esta es la clase a la que hay que llamar desde los viewmodels para comunicarnos con el protocolo WebRTC

class ProtocolHandler {

    private val TAG = "ProtocolHandler"

    private var messageHandler: MessageHandler
    private var rtcManagerHandler: RTCManagerHandler
    private lateinit var socketHandler: SocketHandler
    private val socketConnection = com.example.burnerchat.webRTC.backend.socket.SocketConnection()

    //private lateinit var scope: LifecycleCoroutineScope

    init {
        initializeSocketHandler()
        // Inicializamos RTCManagerHandler después de inicializar socketHandler
        rtcManagerHandler = RTCManagerHandler(socketHandler)
        // Inicializamos MessageHandler después de inicializar rtcManagerHandler (paso de mensajes)
        messageHandler = MessageHandler(rtcManagerHandler)
    }

    private fun initializeSocketHandler() {
        // Inicializamos el SocketHandler con las funciones que manejan los eventos
        socketHandler = SocketHandler(
            socketConnection = socketConnection
        )

        // Comenzamos a escuchar los eventos de socket
//        // Esto no me queda claro
//        socketHandler.startListening(scope)
    }

    fun handleNewMessage(message: com.example.burnerchat.webRTC.backend.socket.MessageModel) {
        Log.d(TAG, "handleNewMessage in VM")
        // Aquí, procesamos el mensaje recibido y actualizamos el estado
//        BurnerChatApp.appModule.update { currentState ->
//            messageHandler.handleMessage(message, currentState)
//        }
        messageHandler.handleMessage(message, State)

        // Se lanza el evento de que se ha recibido una invitación
//        if (message.type == "offer_received") {
//            viewModelScope.launch {
//                _oneTimeEvents.emit(
//                    MainOneTimeEvents.GotInvite
//                )
//            }
//        }
    }

    private fun handleConnectionChange(isConnected: Boolean) {
        Log.d(TAG, "Connection Change: $isConnected")
        State.isConnectedToServer = isConnected
        State.connectedAs =
            if (isConnected) State.connectedAs else TODO()
        // Aquí deberías definir el nombre si está conectado
    }

    // Despachar acciones: Maneja las acciones que se envían al `RTCManagerHandler`
    suspend fun dispatchAction(action: MainActions) {
//        _state.update { currentState ->
//            rtcManagerHandler.dispatchAction(viewModelScope, action, currentState)
//        }
        rtcManagerHandler.dispatchAction(action, State)
    }

    // Enviar el mensaje de la UI
    private fun sendMessageToUi(msg: MessageType) {
//        viewModelScope.launch(Dispatchers.IO) {
//            _state.update {
//                state.value.copy(
//                    messagesFromServer = state.value.messagesFromServer + msg,
//                )
//            }
//        }
        State.messagesFromServer += msg
    }

    // Iniciar WebRTC si es necesario
    suspend fun initializeWebRTC(userName: String, target: String) {
        rtcManagerHandler.initializeRTCManager(userName, target)
//        rtcManagerHandler.consumeEvents(viewModelScope) { message ->
//            // Manejar los eventos de WebRTC aquí y actualizar el estado de la UI
//            _state.update { currentState ->
//                when (message) {
//                    is MessageType.ConnectedToPeer -> {
//                        currentState.copy(isRtcEstablished = true)
//                    }
//
//                    else -> currentState
//                }
//            }
//        }
        rtcManagerHandler.consumeEventsFromRTC(State)
    }

    // ------------------Funciones públicas de acceso------------------
// Función para obtener el manejador del socket
    fun getSocketHandler(): SocketHandler {
        return socketHandler
    }

    // Función para obtener el manejador de mensajes del Signaling Server
    fun getMessageHandler(): MessageHandler {
        return messageHandler
    }

    // Función para obtener el manejador de WebRTC
    fun getRTCManagerHandler(): RTCManagerHandler {
        return rtcManagerHandler
    }

    // Función para establecer el scope del ciclo de vida del socket
//    fun setScope(newScope: LifecycleCoroutineScope) {
//        scope = newScope
//        socketHandler.startListening(scope)
//    }
}