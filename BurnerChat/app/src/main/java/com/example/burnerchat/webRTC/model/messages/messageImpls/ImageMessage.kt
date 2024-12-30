package com.example.burnerchat.webRTC.model.messages.messageImpls

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.Message
import com.example.burnerchat.webRTC.model.messages.MessageImpl
import com.google.firebase.auth.FirebaseUser


class ImageMessage(private val path: String, chat: Chat, user: FirebaseUser) : MessageImpl(user, chat) {
    var textContent:String =""

    override fun getConcreteContent(): String {
        return path;
    }

    override fun getLastContent(): String {
        return "Image message "+textContent
    }

    override fun getMessageTypeCode(user: FirebaseUser): Int {
        if(user.email !== this.getUser().email){
            return Message.LayoutType.ImagenAjena.ordinal
        } else
            return Message.LayoutType.ImagenPropia.ordinal
    }

    override fun getSelfType(): Int {
        return Message.LayoutType.ImagenPropia.ordinal
    }

    override fun getOtherType(): Int {
        return Message.LayoutType.ImagenAjena.ordinal
    }
}