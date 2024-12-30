package com.example.burnerchat.webRTC.views.chats

import android.os.Message
import android.util.Log
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore


class CreateSingleChatViewModel : ViewModel() {
    private val db = Firebase.firestore

    private var _createdChat = MutableLiveData<Boolean>(false)
    val createdChat: LiveData<Boolean>
        get() = _createdChat

    fun addChat(email: String) {
        // Check if the user exists in the db
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val chat = hashMapOf(
                        "email" to email,
                        "participants" to listOf(email),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "lastMessage" to null,
                        "messages" to mutableListOf<Any>()
                    )


                    db.collection("chats")
                        .add(chat)
                        .addOnSuccessListener { documentReference ->
                            Log.d("Firestore", "Chat document added with ID: ${documentReference.id}")
                            _createdChat.value = true
                        }
                        .addOnFailureListener { e ->
                            Log.e("Firestore", "Error adding chat document: ", e)
                        }
                } else {
                    Log.e("Firestore", "Error getting documents: ", task.exception)
                }
            }


    }
}