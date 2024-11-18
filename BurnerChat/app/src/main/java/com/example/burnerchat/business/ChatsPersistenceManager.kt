package com.example.burnerchat.business

import com.example.burnerchat.model.chats.Chat

object ChatsPersistenceManager {
    private var chatsDataBase = mutableListOf<Chat>()

    fun getChats():List<Chat>{
        return chatsDataBase
    }

    fun addChat(chat: Chat){
        chatsDataBase.add(chat)
    }
}