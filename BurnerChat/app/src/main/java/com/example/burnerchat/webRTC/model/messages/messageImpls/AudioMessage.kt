package com.example.burnerchat.webRTC.model.messages.messageImpls

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.Message
import com.example.burnerchat.webRTC.model.messages.MessageImpl
import com.google.firebase.auth.FirebaseUser

class AudioMessage(private val path: String, user: FirebaseUser, chat: Chat) : MessageImpl(user, chat) {
    override fun getConcreteContent(): String {
        return path;
    }

    override fun getLastContent(): String {
        return "Audio message"
    }

    override fun getSelfType(): Int {
        return Message.LayoutType.AudioPropio.ordinal
    }

    override fun getOtherType(): Int {
        return Message.LayoutType.AudioAjeno.ordinal
    }

}
