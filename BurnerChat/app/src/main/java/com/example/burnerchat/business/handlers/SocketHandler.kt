package com.example.burnerchat.business.handlers

import android.util.Log
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.backend.socket.MessageModel
import com.example.burnerchat.backend.socket.SocketConnection
import com.example.burnerchat.backend.socket.SocketEvents
import com.example.burnerchat.business.State
import com.example.burnerchat.model.users.KeyPair
import com.example.burnerchat.model.users.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class SocketHandler(
    private val socketConnection: SocketConnection
) {

    private val TAG = "SocketHandler"

    fun startListening(scope: CoroutineScope) {
        scope.launch {
            socketConnection.event.collectLatest { event ->
                when (event) {
                    is SocketEvents.ConnectionChange -> {
                        if (!event.isConnected) {
                            Log.d(TAG, "Socket ConnectionChange: ${event.isConnected}")
                            State.isConnectedToServer = false
                            State.connectedAs = User(KeyPair("", ""), "")
                        }
                    }

                    is SocketEvents.OnSocketMessageReceived -> {
                        Log.d(TAG, "Message received: ${event.message}")
                        BurnerChatApp.appModule.protocolHandler.handleNewMessage(event.message)
                    }

                    is SocketEvents.ConnectionError -> {
                        Log.d(TAG, "Socket ConnectionError: ${event.error}")
                    }
                }
            }
        }
    }

    fun initializeSocket(userName: String) {
        socketConnection.initSocket(userName)
    }

    fun sendMessage(message: MessageModel) {
        socketConnection.sendMessageToSocket(message)
    }

    fun getSocketConnection() = socketConnection
}