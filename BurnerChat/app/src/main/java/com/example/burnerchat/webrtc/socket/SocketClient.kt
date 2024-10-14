package com.example.burnerchat.webrtc.socket

import com.example.burnerchat.webrtc.utils.DataModel
import com.example.burnerchat.webrtc.utils.DataModelType
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import java.lang.Exception
import java.net.URI
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SocketClient @Inject constructor(
    private val gson:Gson
) {
    private var username:String?=null

    companion object {
        private var webSocket : WebSocketClient?=null
    }

    var listener : Listener?=null

    fun init(username:String){
        this.username = username
        webSocket = object : WebSocketClient(URI("ws://192.168.1.60:3000")) { // TODO: que no esté hardcodeada la URL
            override fun onOpen(handshakedata: ServerHandshake?) {
                // Login or Signin events
                sendMessageToSocket(
                    DataModel(
                        type = DataModelType.SignIn,
                        username = username,
                        null,
                        null
                    )
                )
            }

            override fun onMessage(message: String?) {
                // Deserialize the message to the data model
                val model = try {
                    gson.fromJson(message.toString(), DataModel::class.java)
                }catch(_:Exception){
                    null
                }
                // If not known
                model?.let {
                    listener?.onNewMessageReceived(it)
                }
            }

            override fun onClose(code: Int, reason: String?, remote: Boolean) {
                // For persisting the websocket in case anything happens
                CoroutineScope(Dispatchers.IO).launch {
                    delay(5000) // 5 secs and reinitialize
                    init(username)
                }
            }

            override fun onError(ex: Exception?) {
            }
        }
        webSocket?.connect()
    }

    fun sendMessageToSocket(message: Any?)
    {
        try {
            webSocket?.send(gson.toJson(message))
        }catch(e: Exception)
        {
            e.printStackTrace()
        }
    }

    interface Listener{
        fun onNewMessageReceived(model: DataModel)
    }
}