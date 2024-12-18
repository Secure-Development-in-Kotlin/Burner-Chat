package com.example.burnerchat.model.messages.messageImpls

import com.example.burnerchat.model.chats.Chat
import com.example.burnerchat.model.messages.MessageImpl
import com.example.burnerchat.model.users.User

class AudioMessage(private val path: String, user: User, chat: Chat) : MessageImpl(user, chat) {
    override fun getConcreteContent(): String {
        return path;
    }

    override fun getLastContent(): String {
        return "Audio message"
    }

}
