package com.example.burnerchat.webRTC.model.messages.messageImpls

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.Message
import com.example.burnerchat.webRTC.model.messages.MessageImpl
import com.example.burnerchat.webRTC.model.users.User


class ImageMessage(private val path: String, chat: Chat, user: User) : MessageImpl(user, chat) {
    var textContent:String =""

    override fun getConcreteContent(): String {
        return path;
    }

    override fun getLastContent(): String {
        return "Image message "+textContent
    }

    override fun getMessageTypeCode(user: User): Int {
        if(user.username!== this.getUser().username){
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