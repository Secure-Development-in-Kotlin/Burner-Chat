package com.example.burnerchat.business

import com.example.burnerchat.backend.socket.MessageModel
import com.example.burnerchat.backend.socket.SocketConnection
import com.example.burnerchat.backend.webrtc.WebRTCManager
import kotlinx.coroutines.flow.StateFlow
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

object MainActions {
    // ------------------------- Métodos del dispatchActions -------------------------
    // Conecta al usuario con el nombre dado
    suspend fun connectAs(name: String, socketConnection: SocketConnection) {
        socketConnection.initSocket(name)
    }

    // Acepta la conexión entrante
    suspend fun acceptIncomingConnection(
        offerMessage: MessageModel,
        socketConnection: SocketConnection,
        rtcManager: WebRTCManager?,
        state: StateFlow<MainScreenState>
    ) {
        val session = SessionDescription(
            SessionDescription.Type.OFFER,
            offerMessage.data.toString()
        )

        // Si el WebRTCManager no está inicializado, lo creamos
        if (rtcManager == null) {
            val newRtcManager = WebRTCManager(
                target = offerMessage.name.toString(),
                socketConnection = socketConnection,
                userName = state.value.connectedAs
            )
            newRtcManager.onRemoteSessionReceived(session)
            newRtcManager.answerToOffer(state.value.inComingRequestFrom)
        } else {
            rtcManager.onRemoteSessionReceived(session)
            rtcManager.answerToOffer(lTarget = state.value.inComingRequestFrom)
        }
    }

    // Conecta al usuario actual con el usuario objetivo
    suspend fun connectToUser(
        targetName: String,
        socketConnection: SocketConnection,
        state: StateFlow<MainScreenState>
    ) {
        socketConnection.sendMessageToSocket(
            MessageModel(
                type = "start_transfer",
                name = state.value.connectedAs,
                target = targetName,
                data = null
            )
        )
    }

    // Envía un mensaje de chat
    suspend fun sendChatMessage(
        msg: String,
        rtcManager: WebRTCManager?
    ) {
        rtcManager?.sendMessage(msg)
    }

    // Crea una oferta de "amistad" para el usuario actual y el usuario objetivo
    suspend fun createOffer(
        fromUser: String,
        targetUser: String,
        rtcManager: WebRTCManager?
    ) {
        rtcManager?.createOffer(fromUser, targetUser)
    }

    // Responde a una oferta de "amistad" con una respuesta (aceptada o no aceptada)
    suspend fun answerOffer(
        sessionDescription: SessionDescription,
        rtcManager: WebRTCManager?,
        state: StateFlow<MainScreenState>
    ) {
        rtcManager?.onRemoteSessionReceived(sessionDescription)
        rtcManager?.answerToOffer(state.value.inComingRequestFrom)
    }

    // Agrega un candidato ICE al WebRTCManager
    suspend fun addIceCandidate(
        iceCandidate: IceCandidate,
        rtcManager: WebRTCManager?
    ) {
        rtcManager?.addIceCandidate(iceCandidate)
    }
}