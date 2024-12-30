package com.example.burnerchat.webRTC.business

import android.util.Log
import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.Message
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore


object ChatsPersistenceManager {
    val db = Firebase.firestore

    private const val CHATS_COLLECTION_NAME = "chats"

    fun getChats(): List<Chat> {
        val chatsDataBase = mutableListOf<Chat>()
        db.collection(CHATS_COLLECTION_NAME)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
//                    chatsDataBase.add(chat)
                }
            }
        return chatsDataBase
    }

    fun addChat(chat: Chat) {
        val chatId: String = chat.uid
        val name: String = chat.name
        val participants = chat.participants
        val createdAt = FieldValue.serverTimestamp()
        val lastMessage = chat.getLastMessage()
        val messages = chat.getMessages()
        val imageUrl = chat.getImageUrl()


        // Prepare the user document
        val chatData: MutableMap<String, Any?> = HashMap()
        chatData["name"] = name
        chatData["participants"] = participants
        chatData["createdAt"] = createdAt
        chatData["lastMessage"] = lastMessage
        chatData["messages"] = messages
        chatData["imageUrl"] = imageUrl


        // Add chat in Firestore
        db.collection(CHATS_COLLECTION_NAME).document(chatId).set(chatData)
            .addOnSuccessListener { aVoid: Void? ->
                Log.d("Firestore", "Chat data added successfully")
            }
            .addOnFailureListener { e: Exception? ->
                Log.e("Firestore", "Error adding user data", e)
            }

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