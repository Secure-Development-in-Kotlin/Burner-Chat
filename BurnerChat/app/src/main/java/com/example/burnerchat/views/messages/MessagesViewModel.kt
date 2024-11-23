package com.example.burnerchat.views.messages

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.business.MainScreenState
import com.example.burnerchat.business.State
import com.example.burnerchat.model.chats.Chat
import com.example.burnerchat.model.messages.Message
import com.example.burnerchat.model.messages.messageImpls.TextMessage
import com.example.burnerchat.model.users.KeyPair
import com.example.burnerchat.model.users.User

class MessagesViewModel : ViewModel() {
    private val _messages = MutableLiveData(listOf<Message>())
    val messages: LiveData<List<Message>>
        get() = _messages

    private val _state = MutableLiveData<State>(State)
    val state: LiveData<State>
        get() = _state

    fun getMessages(): List<Message> {
//        return _messages.value ?: listOf()
        // Create a list of messages to test the view
        val user = User(KeyPair("", ""), "User")
        val chat = Chat(user)
        val messages = mutableListOf<Message>()
        messages.add(TextMessage("Hello", user, chat))
        messages.add(TextMessage("How are you?", user, chat))
        messages.add(TextMessage("I'm fine, thank you", user, chat))
        messages.add(TextMessage("And you?", user, chat))
        return messages
    }
}