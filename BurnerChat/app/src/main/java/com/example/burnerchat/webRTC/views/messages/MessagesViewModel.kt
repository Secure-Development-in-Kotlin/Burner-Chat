package com.example.burnerchat.webRTC.views.messages

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.webRTC.business.ChatsPersistenceManager
import com.example.burnerchat.webRTC.business.ImageUtils
import com.example.burnerchat.webRTC.business.MainActions
import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.Message
import com.example.burnerchat.webRTC.model.messages.messageImpls.ImageMessage
import com.example.burnerchat.webRTC.model.messages.messageImpls.TextMessage
import com.example.burnerchat.webRTC.views.TAG
import kotlinx.coroutines.launch

class MessagesViewModel : ViewModel() {

    private val userRepository = BurnerChatApp.appModule.usersRepository
    private val chatsRepository = BurnerChatApp.appModule.chatsRepository
    private lateinit var chat: Chat


    private val _messages = MutableLiveData(listOf<Message>())
    val messages: LiveData<List<Message>>
        get() = _messages

    fun getMessages(): List<Message> {
        _messages.value = chatsRepository.getMessages(chat)
        val messages = _messages.value
        return messages!!
    }

    fun sendMessage(message: String) {
        val messageObject = TextMessage(
            message,
            userRepository.getLoggedUser()!!, chat
        )
        chatsRepository.addMessage(
            chat,
            messageObject
        )
        _messages.value = BurnerChatApp.appModule.chatsRepository.getMessages(chat)
        /*_chat.value?.addMessage(TextMessage(message,
            BurnerChatApp.appModule.usersRepository.getUser(), _chat.value!!))

        _chat.value = _chat.value

         */
        //dispatchAction(MainActions.SendChatMessage(message));
    }

    fun sendImageMessage(bitmap: Bitmap) {
        val messageObject = ImageMessage(
            ImageUtils.convertToBase64(bitmap),
            chat,
            userRepository.getLoggedUser()!!
        )
        chatsRepository.addMessage(
            chat,
            messageObject
        )
        _messages.value = BurnerChatApp.appModule.chatsRepository.getMessages(chat)
    }

    fun sendImageMessage(bitmap: Bitmap, text: String) {
        val messageObject = ImageMessage(
            ImageUtils.convertToBase64(bitmap),
            chat,
            userRepository.getLoggedUser()!!
        )
        messageObject.textContent = text
        chatsRepository.addMessage(
            chat,
            messageObject
        )
        _messages.value = BurnerChatApp.appModule.chatsRepository.getMessages(chat)
    }

    fun setChat(chatId: String) {
        this.chat = ChatsPersistenceManager.getChat(chatId)!!
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