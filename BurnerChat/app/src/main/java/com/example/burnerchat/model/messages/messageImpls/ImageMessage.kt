package com.example.burnerchat.model.messages.messageImpls

import com.example.burnerchat.model.chats.Chat
import com.example.burnerchat.model.messages.MessageImpl
import com.example.burnerchat.model.users.User

class ImageMessage(private val path: String, chat: Chat, user: User) : MessageImpl(user, chat) {
    override fun getConcreteContent(): String {
        return path;
    }
}