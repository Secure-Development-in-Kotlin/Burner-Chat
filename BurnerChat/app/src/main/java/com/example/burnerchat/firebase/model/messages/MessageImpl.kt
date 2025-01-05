package com.example.burnerchat.firebase.model.messages

import com.example.burnerchat.BurnerChatApp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser

abstract class MessageImpl(
    private val userId: String,
    private val sentDate: Timestamp = Timestamp.now()
) : Message {

    override fun getSentDate(): Timestamp {
        return sentDate;
    }

    override fun getContent(): String {
        return getConcreteContent();
    }

    override fun getUserId(): String {
        return userId;
    }

    abstract fun getConcreteContent(): String;

    fun isYourMessage(userToCheckId: String): Boolean {
        return  userId == userToCheckId
    }

    override fun getMessageTypeCode(userId: String): Int {
        if (!isYourMessage(userId)) {
            return getOtherType()
        } else
            return getSelfType()
    }

    protected abstract fun getSelfType(): Int
    protected abstract fun getOtherType(): Int

}