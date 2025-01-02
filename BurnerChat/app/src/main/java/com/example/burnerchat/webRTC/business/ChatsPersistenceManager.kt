package com.example.burnerchat.webRTC.business

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.Message
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


object ChatsPersistenceManager {
    val db = Firebase.firestore

    private const val CHATS_COLLECTION_NAME = "chats"

    suspend fun getChats(): List<Chat> {
        val result = db.collection(CHATS_COLLECTION_NAME).get().await()
        val chatsDataBase = mutableListOf<Chat>()
        for (document in result) {
            val participantsList = document.data["participants"] as? List<String> ?: emptyList()
            val chat = Chat(
                name = document.data["name"] as String,
                participants = participantsList.toTypedArray(), // Convert List<String> to Array<String>
                uid = document.id,
                creationDate = document.data["createdAt"] as Timestamp,
                messages = document.data["messages"] as MutableList<Message>,
                imageUrl = if (document.data["imageUrl"] == null) null else document.data["imageUrl"] as String
            )
            chatsDataBase.add(chat)
        }
        return chatsDataBase
    }


    fun addChat(chat: Chat) {
        //TODO: refactor the viewModel to add the chats here
    }

    fun getMessages(chat: Chat): List<Message> {
        return listOf()
    }

    fun addMessage(chat: Chat, message: Message) {
        // TODO: not implemented
    }

    fun getChat(targetUser: String?): Chat? {
        return null
    }
}