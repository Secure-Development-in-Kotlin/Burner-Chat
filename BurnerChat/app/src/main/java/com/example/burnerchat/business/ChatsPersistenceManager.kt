package com.example.burnerchat.business

import com.example.burnerchat.model.chats.Chat
import com.example.burnerchat.model.messages.Message
import com.example.burnerchat.model.users.User

object ChatsPersistenceManager {
    private var chatsDataBase = mutableListOf<Chat>()

    fun getChats():List<Chat>{
        return chatsDataBase
    }

    fun addChat(chat: Chat){
        chatsDataBase.add(chat)
    }

    fun getMessages(target: User): List<Message> {
        for (chat in chatsDataBase){
            if (chat.getTarget() == target){
                return chat.getMessages()
            }
        }
        return listOf()
    }
}