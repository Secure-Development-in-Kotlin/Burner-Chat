package com.example.burnerchat.firebase.repositories

import android.util.Log
import com.example.burnerchat.firebase.model.chats.Chat
import com.example.burnerchat.firebase.model.messages.Message
import com.example.burnerchat.firebase.model.messages.messageImpls.ImageMessage
import com.example.burnerchat.firebase.model.messages.messageImpls.TextMessage
import com.google.firebase.Firebase
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


object ChatsRepository {
    val db = Firebase.firestore

    private const val CHATS_COLLECTION_NAME = "chats"

    suspend fun getChats(): List<Chat> {
        val result = db.collection(CHATS_COLLECTION_NAME).get().await()
        val chatsDataBase = mutableListOf<Chat>()
        for (document in result) {
            val data = document.data
            val image = data["imageUrl"]
            val participantsList = document.data["participants"] as? List<String> ?: emptyList()
            val chat = Chat(
                name = document.data["name"] as String,
                participants = participantsList.toTypedArray(), // Convert List<String> to Array<String>
                uid = document.id,
                creationDate = document.data["createdAt"] as Timestamp,
                imageUrl = if (document.data["imageUrl"] == null) null else document.data["imageUrl"] as String
            )

            val messagesData = document.data["messages"] as List<Map<String, Any>>
            messagesData.forEach { msgData ->
                chat.messages.add(parseMessageData(msgData))
            }

            chatsDataBase.add(chat)
        }
        return chatsDataBase
    }

    // Function to get all chats where a user is involved
    suspend fun getChatsByUser(user: FirebaseUser): List<Chat> {
        val result = db.collection(CHATS_COLLECTION_NAME).get().await()
        val chatsDataBase = mutableListOf<Chat>()
        for (document in result) {
            val data = document.data
            val image = data["imageUrl"]
            val participantsList = document.data["participants"] as? List<String> ?: emptyList()
            if (participantsList.contains(user.uid)) {
                val chat = Chat(
                    name = document.data["name"] as String,
                    participants = participantsList.toTypedArray(), // Convert List<String> to Array<String>
                    uid = document.id,
                    creationDate = document.data["createdAt"] as Timestamp,
                    imageUrl = if (document.data["imageUrl"] == null) null else document.data["imageUrl"] as String
                )

                val messagesData = document.data["messages"] as List<Map<String, Any>>
                messagesData.forEach { msgData ->
                    chat.messages.add(parseMessageData(msgData))
                }

                chatsDataBase.add(chat)
            }
        }
        return chatsDataBase
    }


    fun addChat(chat: Chat) {
        //TODO: refactor the viewModel to add the chats here
    }

    fun addMessage(chat: Chat, message: Message) {
        // Create a map for the message attributes
        val updatedMessages = getUpdatedMapMessages(chat, message)

        try {
            db.collection(CHATS_COLLECTION_NAME).document(chat.uid)
                .update("messages", updatedMessages).addOnSuccessListener {
                    Log.d("ChatsPersistenceManager", "Message added to chat")
                }.addOnFailureListener {
                    Log.e("ChatsPersistenceManager", "Error adding message to chat", it)
                }
        } catch (e: Exception) {
            Log.e("ChatsPersistenceManager", "Error adding message to chat", e)
        }
    }

    // Function to delete all messages of a user in a chat (for Panic Mode)
    fun deleteMessagesByUser(chat: Chat, user: FirebaseUser) {
        // If the chat is a private chat, delete the chat
        if (chat.participants.size == 2) {
            db.collection(CHATS_COLLECTION_NAME).document(chat.uid).delete()
            return
        }
        // If the chat is a group chat, delete all messages sent by the user
        else {
            val originalMessages = chat.messages
            for (msg in chat.messages) {
                if (msg.getUserId() == user.uid) {
                    originalMessages.remove(msg)
                }
            }

            // Updates the chat with the new messages list
            updateChat(chat, convertMessagesToMap(originalMessages))
        }
    }

    // Function to update a group chat (for Panic Mode)
    private fun updateChat(chat: Chat, convertMessagesToMap: MutableList<Map<String, Any>>) {
        try {
            db.collection(CHATS_COLLECTION_NAME).document(chat.uid)
                .update("messages", convertMessagesToMap).addOnSuccessListener {
                    Log.d("ChatsPersistenceManager", "Chat updated")
                }.addOnFailureListener {
                    Log.e("ChatsPersistenceManager", "Error updating chat", it)
                }
        } catch (e: Exception) {
            Log.e("ChatsPersistenceManager", "Error updating chat", e)
        }
    }

    //Function to update a chat (In general)
    fun updateChat(chat: Chat){
        val messages = convertMessagesToMap(chat.messages)
        val uid = chat.uid
        val name = chat.name
        val image = chat.imageUrl
        val participants = chat.participants.toList()
        try {
            db.collection(CHATS_COLLECTION_NAME).document(uid)
                .update("messages", messages,
                    "name",name,
                    "imageUrl",image,
                    "participants", participants).addOnSuccessListener {
                    Log.d("ChatsPersistenceManager", "Chat updated")
                }.addOnFailureListener {
                    Log.e("ChatsPersistenceManager", "Error updating chat", it)
                }
        } catch (e: Exception) {
            Log.e("ChatsPersistenceManager", "Error updating chat", e)
        }
    }

    // Function to convert a list of firebase messages to a map
    private fun convertMessagesToMap(messages: MutableList<Message>): MutableList<Map<String, Any>> {
        val updatedMessages = mutableListOf<Map<String, Any>>()
        for (msg in messages) {
            updatedMessages.add(
                mapOf(
                    "content" to msg.getContent(),
                    "sender" to msg.getUserId(),
                    "createdAt" to msg.getSentDate(),
                    "messageType" to msg.getMessageTypeCode(msg.getUserId())
                )
            )
        }
        return updatedMessages
    }

    private fun getUpdatedMapMessages(chat: Chat, message: Message): List<Map<String, Any>> {
        val updatedMessages = convertMessagesToMap(chat.messages)

        when (message.getMessageTypeCode(message.getUserId())) {
            0, 1 -> {
                // Text message
                updatedMessages.add(
                    mapOf(
                        "content" to message.getContent(),
                        "sender" to message.getUserId(),
                        "createdAt" to message.getSentDate(),
                        "messageType" to message.getMessageTypeCode(message.getUserId())
                    )
                )
            }

            2, 3 -> {
                // Image message
                updatedMessages.add(
                    mapOf(
                        "content" to message.getContent(),
                        "sender" to message.getUserId(),
                        "createdAt" to message.getSentDate(),
                        "messageType" to message.getMessageTypeCode(message.getUserId()),
                        "textContent" to (message as ImageMessage).textContent
                    )
                )
            }
        }

        return updatedMessages
    }

    suspend fun getChat(chatId: String): Chat {
        // Search the db for the chat with the given id
        val result = db.collection(CHATS_COLLECTION_NAME).document(chatId).get().await()
        if (result.exists()) {

            val participantsList = result.data?.get("participants") as? List<String> ?: emptyList()

            val chat = Chat(
                name = result.data?.get("name") as String,
                participants = participantsList.toTypedArray(), // Convert List<String> to Array<String>
                uid = result.id,
                creationDate = result.data?.get("createdAt") as Timestamp,
                imageUrl = if (result.data?.get("imageUrl") == null) null else result.data?.get("imageUrl") as String
            )

            val messagesData = result.data?.get("messages") as List<Map<String, Any>>
            messagesData.forEach { msgData ->
                chat.messages.add(parseMessageData(msgData))
            }


            return chat
        }
        throw Exception("Chat not found")
    }

    fun listenToChatsRealtime(onChatsUpdated: (List<Chat>) -> Unit) {
        db.collection(CHATS_COLLECTION_NAME).addSnapshotListener { snapshots, error ->
            if (error != null) {
                return@addSnapshotListener
            }

            val chatsDataBase = mutableListOf<Chat>()
            if (snapshots != null) {
                for (document in snapshots) {
                    val participantsList =
                        document.data["participants"] as? List<String> ?: emptyList()
                    val chat = Chat(
                        name = document.data["name"] as String,
                        participants = participantsList.toTypedArray(), // Convert List<String> to Array<String>
                        uid = document.id,
                        creationDate = document.data["createdAt"] as? Timestamp
                            ?: Timestamp.now(),
                        imageUrl = document.data["imageUrl"] as? String,
                        messages = mutableListOf()
                    )
                    chatsDataBase.add(chat)
                }
            }
            onChatsUpdated(chatsDataBase)
        }
    }

    fun listenForMessagesRealtime(chat: Chat, onMessagesUpdated: (List<Message>) -> Unit) {
        db.collection(CHATS_COLLECTION_NAME).document(chat.uid)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    return@addSnapshotListener
                }

                val messages = mutableListOf<Message>()
                if (snapshot != null) {
                    val messagesList =
                        snapshot.data?.get("messages") as? List<Map<String, Any>> ?: emptyList()

                    messagesList.forEach { msgData ->
                        messages.add(parseMessageData(msgData))
                    }
                }
                onMessagesUpdated(messages)
            }
    }

    private fun parseMessageData(msgData: Map<String, Any>): Message {
        when (msgData["messageType"]) {
            0L, 1L -> {
                // Text message
                val message = TextMessage(
                    msgData["content"] as String,
                    msgData["sender"] as String,
                    msgData["createdAt"] as Timestamp
                )
                return message
            }

            2L, 3L -> {
                // Image message
                val message = ImageMessage(
                    msgData["content"] as String,
                    msgData["sender"] as String,
                    msgData["createdAt"] as Timestamp
                )
                message.textContent = msgData["textContent"] as String
                return message
            }

            else -> {
                val message = TextMessage(
                    msgData["content"] as String,
                    msgData["sender"] as String,
                    msgData["createdAt"] as Timestamp
                )
                return message
            }
        }
    }

}