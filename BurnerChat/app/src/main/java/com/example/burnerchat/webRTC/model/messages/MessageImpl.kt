package com.example.burnerchat.webRTC.model.messages

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.users.User
import java.time.LocalDate
import java.time.LocalDateTime

abstract class MessageImpl(private val user: User, private val chat: Chat) : Message {
    private val sentDate: LocalDateTime = LocalDateTime.now()

    override fun getSentDate(): LocalDateTime {
        return sentDate;
    }

    override fun getContent(): String {
        return getConcreteContent();
    }

    override fun getUser(): User {
        return user;
    }

    override fun getChat(): Chat {
        return chat;
    }

    abstract fun getConcreteContent(): String;
}