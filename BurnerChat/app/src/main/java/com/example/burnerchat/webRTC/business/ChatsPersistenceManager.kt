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

    fun getMessages(target: String): List<Message> {
        for (chat in chatsDataBase) {
            if (chat.getTarget().username == target) {
                return chat.getMessages()
            }
        }
        return listOf()
    }

    fun addMessage(target:String, message: Message){
        for (chat in chatsDataBase) {
            if (chat.getTarget().username == target) {
                chat.addMessage(message)
            }
        }
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