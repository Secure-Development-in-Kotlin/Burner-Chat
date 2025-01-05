package com.example.burnerchat.firebase.views.messages

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.firebase.repositories.ImageUtils
import com.example.burnerchat.firebase.model.chats.Chat
import com.example.burnerchat.firebase.model.messages.messageImpls.ImageMessage
import com.example.burnerchat.firebase.model.messages.messageImpls.TextMessage
import kotlinx.coroutines.launch

class MessagesViewModel : ViewModel() {

    private val userRepository = BurnerChatApp.appModule.usersRepository
    private val chatsRepository = BurnerChatApp.appModule.chatsRepository

    private val _chat = MutableLiveData<Chat>()
    val chat: LiveData<Chat>
        get() = _chat

    fun sendMessage(message: String) {
        val messageObject = TextMessage(
            message,
            userRepository.getLoggedUser()?.uid!!,
        )

        viewModelScope.launch {
            chatsRepository.addMessage(
                _chat.value!!,
                messageObject
            )
        }

    }

    fun sendImageMessage(bitmap: Bitmap) {
        val messageObject = ImageMessage(
            ImageUtils.convertToBase64(bitmap),
            userRepository.getLoggedUser()?.uid!!
        )

        viewModelScope.launch {
            chatsRepository.addMessage(
                _chat.value!!,
                messageObject
            )
        }
    }

    fun sendImageMessage(bitmap: Bitmap, text: String) {
        val messageObject = ImageMessage(
            ImageUtils.convertToBase64(bitmap),
            userRepository.getLoggedUser()?.uid!!
        )
        messageObject.textContent = text

        viewModelScope.launch {
            chatsRepository.addMessage(
                _chat.value!!,
                messageObject
            )
        }
    }

    suspend fun setChat(chatId: String) {
        _chat.value = chatsRepository.getChat(chatId)
        chatsRepository.listenForMessagesRealtime(_chat.value!!) {
            viewModelScope.launch {
                _chat.value = chatsRepository.getChat(chatId)
            }
        }
    }
}