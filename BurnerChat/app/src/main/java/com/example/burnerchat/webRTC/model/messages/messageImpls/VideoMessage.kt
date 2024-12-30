package com.example.burnerchat.webRTC.model.messages.messageImpls

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.Message
import com.example.burnerchat.webRTC.model.messages.MessageImpl
import com.google.firebase.auth.FirebaseUser

class VideoMessage(val path: String, user: FirebaseUser, chat: Chat) : MessageImpl(user, chat) {
    override fun getConcreteContent(): String {
        return path;
    }

    override fun getLastContent(): String {
        return "Video message";
    }

    override fun getMessageTypeCode(user: FirebaseUser): Int {
        if (user.email !== this.getUser().email) {
            return Message.LayoutType.VideoAjeno.ordinal
        } else
            return Message.LayoutType.VideoPropio.ordinal
    }

    override fun getSelfType(): Int {
        return Message.LayoutType.VideoPropio.ordinal
    }

    override fun getOtherType(): Int {
        return Message.LayoutType.VideoAjeno.ordinal
    }
}