package com.example.burnerchat.webRTC.views.messages

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.webRTC.business.ChatsPersistenceManager
import com.example.burnerchat.webRTC.business.MainActions
import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.Message
import com.example.burnerchat.webRTC.views.TAG
import kotlinx.coroutines.launch

class MessagesViewModel : ViewModel() {
    private lateinit var chat: Chat

    private var target = ""

//    private var rtcManager = BurnerChatApp.appModule.rtcManager
    //private val socketConnection = BurnerChatApp.appModule.socketConnection

    private val _messages = MutableLiveData(listOf<Message>())
    val messages: LiveData<List<Message>>
        get() = _messages

    fun getMessages(): List<Message> {
        return _messages.value ?: listOf()
    }

    fun sendMessage(message: String) {
        //dispatchAction(MainActions.SendChatMessage(message));
    }

    // Método para establecer conexión con el otro usuario
    fun establishConnection() {
        viewModelScope.launch {
            BurnerChatApp.appModule.protocolHandler.dispatchAction(
                MainActions.ConnectToUser(
                    target
                )
            )
        }
    }

    fun setTarget(target: String) {
        this.target = target

        val chat: Chat? = ChatsPersistenceManager.getChat(target)
        if (chat != null) {
            this.chat = chat
        } else {
            Log.d(TAG, "Chat not found")
        }
    }

//    private fun dispatchAction(actions: MainActions) {
//        when (actions) {
//            // In this part the message is send using the WebRtcManager
//            is MainActions.SendChatMessage -> {
//                rtcManager.sendMessage(actions.msg)
//            }
//
//            else -> {
//                Log.d(TAG, "Unknown action: $actions")
//            }
//        }
//    }
}