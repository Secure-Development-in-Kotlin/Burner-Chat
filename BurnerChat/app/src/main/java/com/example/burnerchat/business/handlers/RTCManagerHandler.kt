package com.example.burnerchat.business.handlers

import android.util.Log
import com.example.burnerchat.backend.socket.MessageModel
import com.example.burnerchat.backend.socket.SocketConnection
import com.example.burnerchat.backend.webrtc.IceCandidateModel
import com.example.burnerchat.backend.webrtc.MessageType
import com.example.burnerchat.backend.webrtc.WebRTCManager
import com.example.burnerchat.business.MainActions
import com.example.burnerchat.business.MainScreenState
import com.example.burnerchat.business.State
import com.example.burnerchat.model.users.KeyPair
import com.example.burnerchat.model.users.User
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class RTCManagerHandler(
    private val socketHandler: SocketHandler
) {

    private val TAG = "RTCManagerHandler"

    private lateinit var rtcManager: WebRTCManager

    // Creo que esto no hace falta, porque se maneja en inComingRequestFrom
//    private lateinit var newOfferMessage: MessageModel
    private val gson = Gson()

    fun initializeRTCManager(userName: String, target: String) {
        rtcManager = WebRTCManager(
            socketConnection = socketHandler.getSocketConnection(),
            userName = userName,
            target = target
        )
    }

    fun consumeEvents(scope: CoroutineScope, onEvent: (MessageType) -> Unit) {
        if (!::rtcManager.isInitialized) return
        scope.launch {
            rtcManager.messageStream.collectLatest { event ->
                onEvent(event)
            }
        }
    }

    fun createOffer(userName: String, target: String) {
        if (::rtcManager.isInitialized) {
            rtcManager.createOffer(userName, target)
        }
    }

    fun answerToOffer(userName: String, session: SessionDescription) {
        if (::rtcManager.isInitialized) {
            rtcManager.onRemoteSessionReceived(session)
            rtcManager.answerToOffer(userName)
        }
    }

    fun sendMessage(message: String) {
        if (::rtcManager.isInitialized) {
            rtcManager.sendMessage(message)
        }
    }

    fun handleIceCandidate(data: Any) {
        val candidate = gson.fromJson(
            gson.toJson(data),
            IceCandidateModel::class.java
        )
        rtcManager.addIceCandidate(
            IceCandidate(
                candidate.sdpMid,
                candidate.sdpMLineIndex.toInt(),
                candidate.sdpCandidate
            )
        )
    }

    suspend fun consumeEventsFromRTC(
        currentState: State
    ) {
        if (!::rtcManager.isInitialized) return

        rtcManager.messageStream.collectLatest { event ->
            when (event) {
                // Si el mensaje es de tipo ConnectedToPeer, actualizamos el estado de la conexión
                is MessageType.ConnectedToPeer -> {
                    // Actualizamos el estado con la información de la conexión
                    currentState.isRtcEstablished = true
                    currentState.peerConnectionString =
                        "Connected to peer ${currentState.isConnectToPeer}"
                }
                // Si el mensaje es enviado por el usuario, lo mostramos en el log
                is MessageType.MessageByMe -> {
                    Log.d("RTC", "Message sent by me: ${event.msg}")
                }

                // Se pueden manejar más tipos de eventos si es necesario
                else -> {
                    Log.d("RTC", "Unhandled RTC event: $event")
                }
            }
        }
    }

    suspend fun dispatchAction(
        actions: MainActions,
        currentState: State
    ): State {
        return when (actions) {
            // Acción para conectarse al servidor
            is MainActions.ConnectAs -> {
                // Se inicializa el socket
                socketHandler.getSocketConnection().initSocket(actions.name)
                // Se cambia el nombre de conexión por el del usuario
                currentState.connectedAs = User(KeyPair("a", "b"), actions.name)
                // Se indica que está conectado al servidor
                currentState.isConnectedToServer = true
                Log.d(TAG, "Connected as ${actions.name}")
                currentState
            }

            // Acción para aceptar una conexión entrante y entablar una conversación
            is MainActions.AcceptIncomingConnection -> {
                Log.d(TAG, "Accepting incoming connection from ${currentState.inComingRequestFrom}")
                // Creo que esto no hace falta, porque se maneja en inComingRequestFrom
                if (!::rtcManager.isInitialized) {
                    rtcManager = WebRTCManager(
                        socketConnection = socketHandler.getSocketConnection(),
                        userName = currentState.connectedAs.username,
                        target = currentState.inComingRequestFrom
                    )
                    // Consumimos los eventos relacionados con WebRTC
                    consumeEventsFromRTC(
                        currentState
                    )
                }
                answerToOffer(
                    userName = currentState.connectedAs.username,
                    session = SessionDescription(
                        SessionDescription.Type.OFFER,
                        currentState.inComingRequestFrom
                    )
                )
                currentState
            }

            // Acción para conectarse al otro usuario
            is MainActions.ConnectToUser -> {
                Log.d(TAG, "Connecting to ${actions.name}")
                socketHandler.getSocketConnection().sendMessageToSocket(
                    MessageModel(
                        type = "start_transfer",
                        name = currentState.connectedAs.username,
                        target = actions.name,
                        data = null
                    )
                )
                Log.d(TAG, "Message sent to socket for connection request")
                currentState
            }

            // Acción para enviar un mensaje de chat
            is MainActions.SendChatMessage -> {
                Log.d(TAG, "Sending chat message: ${actions.msg}")
                if (::rtcManager.isInitialized) {
                    rtcManager.sendMessage(actions.msg)
                }
                currentState
            }
        }

    }
}