package com.example.burnerchat.firebase.model.chats

import com.example.burnerchat.firebase.model.messages.Message
import com.google.firebase.Timestamp
import java.util.UUID

class Chat(
    var name: String,
    var participants: Array<String>,
    val uid: String = UUID.randomUUID().toString(), // Generate a unique ID if not provided
    val creationDate: Timestamp = Timestamp.now(),
    var messages: MutableList<Message> = mutableListOf(),
    var imageUrl: String? = null
) {


    fun getLastMessage(): Message {
        return messages.last()
    }

    fun addMessage(message: Message) {
        messages.add(message)
    }

    fun isEmpty(): Boolean {
        return messages.isEmpty()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Chat

        if (name != other.name) return false
        if (!participants.contentEquals(other.participants)) return false
        if (uid != other.uid) return false
        if (creationDate != other.creationDate) return false
        if (messages != other.messages) return false
        if (imageUrl != other.imageUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + participants.contentHashCode()
        result = 31 * result + uid.hashCode()
        result = 31 * result + creationDate.hashCode()
        result = 31 * result + messages.hashCode()
        result = 31 * result + (imageUrl?.hashCode() ?: 0)
        return result
    }

    fun isGroup(): Boolean {
        return participants.size > 2
    }


}

