package com.example.burnerchat.webRTC.business

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.Message
import com.example.burnerchat.webRTC.model.users.User


object ChatsPersistenceManager {
    private var chatsDataBase = mutableListOf<Chat>()

    fun getChats(): List<Chat> {
        return chatsDataBase
    }

    fun addChat(chat: Chat) {
        chatsDataBase.add(chat)
    }

    fun getMessages(target: User): List<Message> {
        for (chat in chatsDataBase) {
            if (chat.getTarget() == target) {
                return chat.getMessages()
            }
        }
        return listOf()
    }

    fun getChat(targetUser: String?): Chat? {
        for (chat in chatsDataBase) {
            if (chat.getTarget().username == targetUser) {
                return chat
            }
        }
        return null
    }
}