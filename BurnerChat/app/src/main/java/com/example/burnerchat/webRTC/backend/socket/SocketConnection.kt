package com.example.burnerchat.webRTC.backend.socket


import android.util.Log
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.net.URI

private const val TAG = "SocketConnection"

sealed class SocketEvents {
    data class OnSocketMessageReceived(val message: com.example.burnerchat.webRTC.backend.socket.MessageModel) :
        com.example.burnerchat.webRTC.backend.socket.SocketEvents()

    data class ConnectionChange(val isConnected: Boolean) :
        com.example.burnerchat.webRTC.backend.socket.SocketEvents()

    data class ConnectionError(val error: String) :
        com.example.burnerchat.webRTC.backend.socket.SocketEvents()
}

class SocketConnection {

    private val scope = CoroutineScope(Dispatchers.IO)

    private var webSocket: WebSocketClient? = null
    private val gson = Gson()

    private val _events =
        MutableSharedFlow<com.example.burnerchat.webRTC.backend.socket.SocketEvents>()
    val event: SharedFlow<com.example.burnerchat.webRTC.backend.socket.SocketEvents>
        get() = _events

    fun initSocket(
        username: String,
    ) {

        webSocket = object : WebSocketClient(URI("ws://83.54.225.146:3000")) {
            override fun onOpen(handshakedata: ServerHandshake?) {
                Log.d(
                    com.example.burnerchat.webRTC.backend.socket.TAG,
                    "onOpen: ${Thread.currentThread()}"
                )
                sendMessageToSocket(
                    com.example.burnerchat.webRTC.backend.socket.MessageModel(
                        "store_user", username, null, null
                    )
                )
            }

            override fun onMessage(message: String?) {
                try {
                    Log.d(com.example.burnerchat.webRTC.backend.socket.TAG, "onMessage: $message")
                    emitEvent(
                        com.example.burnerchat.webRTC.backend.socket.SocketEvents.OnSocketMessageReceived(
                            gson.fromJson(
                                message,
                                com.example.burnerchat.webRTC.backend.socket.MessageModel::class.java
                            )
                        )
                    )
                } catch (e: Exception) {
                    Log.d(
                        com.example.burnerchat.webRTC.backend.socket.TAG,
                        "onMessage: error -> $e"
                    )
                    emitEvent(
                        com.example.burnerchat.webRTC.backend.socket.SocketEvents.ConnectionError(
                            e.message ?: "error in receiving messages from socket"
                        )
                    )
                    e.printStackTrace()
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                Log.d(com.example.burnerchat.webRTC.backend.socket.TAG, "onClose: $reason")
                emitEvent(
                    com.example.burnerchat.webRTC.backend.socket.SocketEvents.ConnectionChange(
                        isConnected = false,
                    )
                )
            }

            override fun onError(ex: Exception?) {
                Log.d(com.example.burnerchat.webRTC.backend.socket.TAG, "onError: $ex")
                emitEvent(
                    com.example.burnerchat.webRTC.backend.socket.SocketEvents.ConnectionError(
                        ex?.message ?: "Socket exception"
                    )
                )
            }
        }
        webSocket?.connect()
    }

    private fun emitEvent(event: com.example.burnerchat.webRTC.backend.socket.SocketEvents) {
        scope.launch {
            _events.emit(
                event
            )
        }
    }

    fun sendMessageToSocket(message: com.example.burnerchat.webRTC.backend.socket.MessageModel) {
        try {
            Log.d(com.example.burnerchat.webRTC.backend.socket.TAG, "sendMessageToSocket: $message")
            webSocket?.send(Gson().toJson(message))
        } catch (e: Exception) {
            Log.d(com.example.burnerchat.webRTC.backend.socket.TAG, "sendMessageToSocket: $e")
        }
    }

}