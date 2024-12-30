package com.example.burnerchat.webRTC.model.chats

import com.example.burnerchat.webRTC.model.messages.Message
import java.time.LocalDate
import java.util.UUID
import com.google.firebase.auth.FirebaseUser

class Chat(
    var name: String,
    val participants: Array<String>
) {
    val uid: String = UUID.randomUUID().toString() // Automatically generate a unique ID
    private var messages: MutableList<Message> = mutableListOf()
    private val creationDate: LocalDate = LocalDate.now()
    private var imageUrl: String? = null

    fun getLastMessage(): Message {
        return messages.last()
    }

    fun getMessages(): List<Message> {
        return messages
    }

    fun addMessage(message: Message) {
        messages.add(message)
    }

    fun isEmpty(): Boolean {
        return messages.isEmpty()
    }

    fun getImageUrl(): String? {
        return imageUrl
    }

    fun setImageUrl(url: String) {
        imageUrl = url
    }

}
