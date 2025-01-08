package com.example.burnerchat.firebase.model.messages

import com.google.firebase.Timestamp

abstract class MessageImpl(
    private val userEmail: String,
    private val sentDate: Timestamp = Timestamp.now()
) : Message {

    override fun getSentDate(): Timestamp {
        return sentDate;
    }

    override fun getContent(): String {
        return getConcreteContent();
    }

    override fun getUserEmail(): String {
        return userEmail;
    }

    abstract fun getConcreteContent(): String;

    fun isYourMessage(userToCheckId: String): Boolean {
        return  userEmail == userToCheckId
    }

    override fun getMessageTypeCode(userId: String): Int {
        return if (!isYourMessage(userId)) {
            getOtherType()
        } else
            getSelfType()
    }

    protected abstract fun getSelfType(): Int
    protected abstract fun getOtherType(): Int

}