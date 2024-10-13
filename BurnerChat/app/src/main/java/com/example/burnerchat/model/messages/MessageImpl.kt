package com.example.burnerchat.model.messages

import com.example.burnerchat.model.chats.Chat
import com.example.burnerchat.model.users.User
import java.time.LocalDate

abstract class MessageImpl (val user: User, val chat: Chat) : Message {
    private val sentDate : LocalDate = LocalDate.now()

    override fun getSentDate(): LocalDate {
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