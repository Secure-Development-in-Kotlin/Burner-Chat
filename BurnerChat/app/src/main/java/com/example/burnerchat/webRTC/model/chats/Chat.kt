package com.example.burnerchat.webRTC.model.chats

import com.example.burnerchat.webRTC.model.messages.Message
import com.example.burnerchat.webRTC.model.users.User
import java.time.LocalDate


class Chat(private val target: User) {
    private var messages: MutableList<Message> = mutableListOf()
    private val creationDate: LocalDate = LocalDate.now()

    /**
     * Returns the other user
     */
    fun getTarget(): User {
        return target
    }

    fun getLastMessage(): Message {
        return messages.last()
    }

    fun getMessages(): List<Message> {
        return messages
    }

    fun addMessage(message: Message) {
        messages.add(message)
    }

    fun isEmpty(): Boolean {
        return messages.isEmpty()
    }
}