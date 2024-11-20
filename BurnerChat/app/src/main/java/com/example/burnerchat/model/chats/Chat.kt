package com.example.burnerchat.model.chats

import com.example.burnerchat.model.messages.Message
import com.example.burnerchat.model.users.User
import java.time.LocalDate


class Chat (private val otherUser : User) {
    private var messages : MutableList<Message> = mutableListOf()
    private val creationDate : LocalDate = LocalDate.now()

    /**
     * Returns the other user
     */
    fun getOtherUser() : User{
        return otherUser
    }

    fun getLastMessage() : Message {
        return messages.last()
    }

    fun addMessage(message: Message){
        messages.add(message)
    }

    fun isEmpty():Boolean{
        return messages.isEmpty()
    }
}