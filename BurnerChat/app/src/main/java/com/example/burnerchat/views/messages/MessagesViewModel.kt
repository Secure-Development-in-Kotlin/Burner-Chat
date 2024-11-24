package com.example.burnerchat.views.messages

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.backend.socket.MessageModel
import com.example.burnerchat.backend.socket.SocketEvents
import com.example.burnerchat.backend.webrtc.IceCandidateModel
import com.example.burnerchat.backend.webrtc.MessageType
import com.example.burnerchat.backend.webrtc.WebRTCManager
import com.example.burnerchat.business.ChatsPersistenceManager
import com.example.burnerchat.business.MainActions
import com.example.burnerchat.business.State
import com.example.burnerchat.model.chats.Chat
import com.example.burnerchat.model.messages.Message
import com.example.burnerchat.model.messages.messageImpls.TextMessage
import com.example.burnerchat.views.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate

class MessagesViewModel : ViewModel() {
    private lateinit var chat : Chat

    private var rtcManager = BurnerChatApp.appModule.rtcManager
    private val socketConnection = BurnerChatApp.appModule.socketConnection

    private val _messages = MutableLiveData(listOf<Message>())
    val messages: LiveData<List<Message>>
        get() = _messages

    init {
        val chat : Chat? = ChatsPersistenceManager.getChat(State.isConnectToPeer)
        if (chat != null) {
            this.chat = chat
        } else {
            Log.d(TAG, "Chat not found")
        }
    }

    fun getMessages(): List<Message> {
        return _messages.value ?: listOf()
    }

    fun sendMessage(message: String) {
        dispatchAction(MainActions.SendChatMessage(message));
    }

    private fun dispatchAction(actions: MainActions) {
        when (actions) {
            // In this part the message is send using the WebRtcManager
            is MainActions.SendChatMessage -> {
                rtcManager.sendMessage(actions.msg)
            }

            else -> {
                Log.d(TAG, "Unknown action: $actions")
            }
        }
    }
}