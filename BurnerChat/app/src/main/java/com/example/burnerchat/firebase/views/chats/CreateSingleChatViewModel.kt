package com.example.burnerchat.firebase.views.chats

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.firebase.repositories.ImageUtils
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.firestore


class CreateSingleChatViewModel : ViewModel() {
    private val db = Firebase.firestore

    private var _createdChat = MutableLiveData<Boolean>(false)
    val createdChat: LiveData<Boolean>
        get() = _createdChat

    fun setIcon(image: Bitmap){
        _icon.value = ImageUtils.convertToBase64(image)
    }

    private var _icon = MutableLiveData<String>("")
    val icon:LiveData<String>
        get()=_icon

    fun addChat(email: String, name: String) {
        // TODO: Check if the user exists in the db
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val participants = mutableListOf<String>()
                    participants.add(email)
                    participants.add(BurnerChatApp.appModule.usersRepository.getLoggedUser()!!.email!!)

                    val chat = hashMapOf(
                        "name" to name,
                        "participants" to participants,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "lastMessage" to null,
                        "messages" to mutableListOf<Any>(),
                        "imageUrl" to icon.value
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