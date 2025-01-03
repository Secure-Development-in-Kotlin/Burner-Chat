package com.example.burnerchat.webRTC.model.messages.messageImpls

import com.example.burnerchat.webRTC.model.messages.Message
import com.example.burnerchat.webRTC.model.messages.MessageImpl
import com.google.firebase.Timestamp


class ImageMessage(
    private val base64Path: String,
    userId: String,
    sentDate: Timestamp = Timestamp.now()
    ) : MessageImpl(userId, sentDate) {
    var textContent: String = ""

    override fun getConcreteContent(): String {
        return base64Path;
    }

    override fun getLastContent(): String {
        return "Image message " + textContent
    }

    override fun getMessageTypeCode(userId: String): Int {
        return if (userId != this.getUserId()) {
            Message.LayoutType.ImagenAjena.ordinal
        } else
            Message.LayoutType.ImagenPropia.ordinal
    }

    override fun getSelfType(): Int {
        return Message.LayoutType.ImagenPropia.ordinal
    }

    override fun getOtherType(): Int {
        return Message.LayoutType.ImagenAjena.ordinal
    }
}