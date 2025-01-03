package com.example.burnerchat.webRTC.model.chats

import android.util.Log
import com.example.burnerchat.webRTC.model.messages.Message
import com.google.firebase.Timestamp
import java.util.UUID

class Chat(
    var name: String,
    val participants: Array<String>,
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

}

