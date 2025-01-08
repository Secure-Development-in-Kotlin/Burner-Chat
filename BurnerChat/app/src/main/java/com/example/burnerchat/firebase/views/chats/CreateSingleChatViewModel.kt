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

    private val usersRepository = BurnerChatApp.appModule.usersRepository
    private val db = Firebase.firestore

    private var _createdChat = MutableLiveData(false)
    val createdChat: LiveData<Boolean>
        get() = _createdChat

    private var _usersDBList = MutableLiveData<List<UserDTO>>(listOf())
    val usersDBList: LiveData<List<UserDTO>>
        get() = _usersDBList

    private var _selectedUser = MutableLiveData<String>()
    val selectedUser: LiveData<String?>
        get() = _selectedUser

    fun selectUser(user: String) {
        _selectedUser.value = user
    }

    fun deselectUser() {
        selectUser("")
    }

    suspend fun getUsers() {
        val users = usersRepository.getUsers()
        _usersDBList.value = users
    }

    suspend fun findUsers(string: String) {
        val users = usersRepository.getUsersByString(string)
        _usersDBList.value = users
    }

    fun isSelected(email: String): Boolean {
        if (_selectedUser.value == null)
            return false
        return _selectedUser.value!! == email
    }


    fun setIcon(image: Bitmap) {
        _icon.value = ImageUtils.convertToBase64(image)
    }

    private var _icon = MutableLiveData<String>("")
    val icon: LiveData<String>
        get() = _icon

    fun addChat(name: String) {
        var email = ""
        if (_selectedUser.value != null)
            email = _selectedUser.value!!

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
                            Log.d(
                                "Firestore",
                                "Chat document added with ID: ${documentReference.id}"
                            )
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

    fun canAdd(): Boolean {
        return !_selectedUser.value.isNullOrBlank()
    }
}