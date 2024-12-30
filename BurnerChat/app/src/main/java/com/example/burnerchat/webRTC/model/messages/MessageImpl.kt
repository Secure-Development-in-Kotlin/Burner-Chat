package com.example.burnerchat.webRTC.model.messages

import com.example.burnerchat.webRTC.model.chats.Chat
import com.google.firebase.auth.FirebaseUser
import java.time.LocalDateTime

abstract class MessageImpl(private val user: FirebaseUser, private val chat: Chat) : Message {
    private val sentDate: LocalDateTime = LocalDateTime.now()

    override fun getSentDate(): LocalDateTime {
        return sentDate;
    }

    override fun getContent(): String {
        return getConcreteContent();
    }

    override fun getUser(): FirebaseUser {
        return user;
    }

    override fun getChat(): Chat {
        return chat;
    }

    abstract fun getConcreteContent(): String;

    fun isYourMessage(user: FirebaseUser): Boolean {
        val username = user.email.toString()
        val messageUsername = this.getUser().email.toString()
        return username == messageUsername
    }

    override fun getMessageTypeCode(user: FirebaseUser): Int {
        if (!isYourMessage(user)) {
            return getOtherType()
        } else
            return getSelfType()
    }

    protected abstract fun getSelfType(): Int
    protected abstract fun getOtherType(): Int
}