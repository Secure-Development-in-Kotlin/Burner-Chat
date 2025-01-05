package com.example.burnerchat.firebase.model.messages.messageImpls

import com.example.burnerchat.firebase.model.messages.Message
import com.example.burnerchat.firebase.model.messages.MessageImpl
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

class VideoMessage(
    val path: String,
    user: String,
    sentDate: Timestamp = Timestamp.now()
) : MessageImpl(user, sentDate) {
    override fun getConcreteContent(): String {
        return path;
    }

    override fun getLastContent(): String {
        return "Video message";
    }

    override fun getMessageTypeCode(userId: String): Int {
        return if (userId !== this.getUserId()) {
            Message.LayoutType.VideoAjeno.ordinal
        } else
            Message.LayoutType.VideoPropio.ordinal
    }

    override fun getSelfType(): Int {
        return Message.LayoutType.VideoPropio.ordinal
    }

    override fun getOtherType(): Int {
        return Message.LayoutType.VideoAjeno.ordinal
    }
}