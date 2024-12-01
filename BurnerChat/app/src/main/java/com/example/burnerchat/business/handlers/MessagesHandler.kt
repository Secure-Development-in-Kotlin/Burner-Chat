package com.example.burnerchat.business.handlers

import android.util.Log
import com.example.burnerchat.backend.socket.MessageModel
import com.example.burnerchat.backend.webrtc.MessageType
import com.example.burnerchat.business.MainOneTimeEvents
import com.example.burnerchat.business.MainScreenState
import com.example.burnerchat.business.State
import com.example.burnerchat.model.users.User
import kotlinx.coroutines.flow.Flow
import org.webrtc.SessionDescription

class MessageHandler(
    private val rtcManagerHandler: RTCManagerHandler
) {

    private val TAG = "MessageHandler"

    private lateinit var newOfferMessage: MessageModel

    fun handleMessage(
        message: MessageModel,
        currentState: State
    ) {
        when (message.type) {
            "user_already_exists" -> {
                Log.d(TAG, "User already exists")
                currentState.messagesFromServer += MessageType.Info("User already exists")
            }

            "user_stored" -> {
                Log.d(TAG, "User stored in socket as ${message.data.toString()}")
                currentState.isConnectedToServer = true
                currentState.connectedAs = User(
                    keyPair = currentState.connectedAs.keyPair,
                    username = message.data.toString()
                )
                currentState.messagesFromServer += MessageType.Info("User stored in socket as ${message.data.toString()}")

            }

            "transfer_response" -> {
                Log.d(TAG, "Transfer response received")
                if (message.data == null) {
                    Log.d(TAG, "User is not available")
                    currentState.messagesFromServer += MessageType.Info("User is not available")
                } else {
                    rtcManagerHandler.initializeRTCManager(
                        userName = currentState.connectedAs.username,
                        target = message.data.toString()
                    )
                    currentState.isConnectToPeer = message.data.toString()
                    currentState.messagesFromServer += MessageType.Info("Connected to ${message.data}")
                    Log.d(TAG, "Connected to ${message.data}")
                }
            }

            "offer_received" -> {
                Log.d(TAG, "Offer received from ${message.name.orEmpty()}")
                currentState.inComingRequestFrom = message.name.orEmpty()
                currentState.messagesFromServer += MessageType.Info("Offer received from ${message.name.orEmpty()}")
                // Aquí se lanza el dialogo de aceptar o rechazar la solicitud de amistad
            }

            "answer_received" -> {
                Log.d(TAG, "Answer received")
                rtcManagerHandler.answerToOffer(
                    userName = currentState.connectedAs.username,
                    session = SessionDescription(
                        SessionDescription.Type.ANSWER,
                        message.data.toString()
                    )
                )
                currentState.messagesFromServer += MessageType.Info("Answer received")
            }

            "ice_candidate" -> {
                Log.d(TAG, "ICE candidate received")
                rtcManagerHandler.handleIceCandidate(message.data!!)
                currentState.messagesFromServer += MessageType.Info("ICE candidate received")
            }
        }
    }
}