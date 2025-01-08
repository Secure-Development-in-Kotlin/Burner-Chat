package com.example.burnerchat.firebase.model.messages.messageImpls

import com.example.burnerchat.firebase.model.messages.Message
import com.example.burnerchat.firebase.model.messages.MessageImpl
import com.google.firebase.Timestamp

class TextMessage(
    private val text: String,
    userEmail: String,
    sentDate: Timestamp = Timestamp.now()
) : MessageImpl(userEmail, sentDate) {
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