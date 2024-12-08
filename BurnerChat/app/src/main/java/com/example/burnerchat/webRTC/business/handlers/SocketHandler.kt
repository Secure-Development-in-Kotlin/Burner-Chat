package com.example.burnerchat.webRTC.business.handlers

import android.util.Log
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.webRTC.business.State
import com.example.burnerchat.webRTC.model.users.KeyPair
import com.example.burnerchat.webRTC.model.users.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SocketHandler(
    private val socketConnection: com.example.burnerchat.webRTC.backend.socket.SocketConnection
) {

    private val TAG = "SocketHandler"

    fun startListening(scope: CoroutineScope) {
        scope.launch {
            socketConnection.event.collectLatest { event ->
                when (event) {
                    is com.example.burnerchat.webRTC.backend.socket.SocketEvents.ConnectionChange -> {
                        if (!event.isConnected) {
                            Log.d(TAG, "Socket ConnectionChange: ${event.isConnected}")
                            State.isConnectedToServer = false
                            State.connectedAs = User(KeyPair("", ""), "")
                        }
                    }

                    is com.example.burnerchat.webRTC.backend.socket.SocketEvents.OnSocketMessageReceived -> {
                        Log.d(TAG, "Message received: ${event.message}")
                        BurnerChatApp.appModule.protocolHandler.handleNewMessage(event.message)
                    }

                    is com.example.burnerchat.webRTC.backend.socket.SocketEvents.ConnectionError -> {
                        Log.d(TAG, "Socket ConnectionError: ${event.error}")
                    }
                }
            }
        }
    }

    fun initializeSocket(userName: String) {
        socketConnection.initSocket(userName)
    }

    fun sendMessage(message: com.example.burnerchat.webRTC.backend.socket.MessageModel) {
        socketConnection.sendMessageToSocket(message)
    }

    fun getSocketConnection() = socketConnection
}