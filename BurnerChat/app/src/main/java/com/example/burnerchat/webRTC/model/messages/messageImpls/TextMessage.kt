package com.example.burnerchat.webRTC.model.messages.messageImpls

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.MessageImpl
import com.example.burnerchat.webRTC.model.users.User

class TextMessage(private val text: String, user: User, chat: Chat) : MessageImpl(user, chat) {
    override fun getConcreteContent(): String {
        return text;
    }

    override fun getLastContent(): String {
        return text;
    }
}