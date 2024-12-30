package com.example.burnerchat.webRTC.model.messages.messageImpls

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.Message
import com.example.burnerchat.webRTC.model.messages.MessageImpl
import com.google.firebase.auth.FirebaseUser

class TextMessage(private val text: String, user: FirebaseUser, chat: Chat) : MessageImpl(user, chat) {
    override fun getConcreteContent(): String {
        return text;
    }

    override fun getLastContent(): String {
        return text;
    }


    override fun getSelfType(): Int {
        return Message.LayoutType.TextoPropio.ordinal
    }

    override fun getOtherType(): Int {
        return Message.LayoutType.TextoAjeno.ordinal
    }
}