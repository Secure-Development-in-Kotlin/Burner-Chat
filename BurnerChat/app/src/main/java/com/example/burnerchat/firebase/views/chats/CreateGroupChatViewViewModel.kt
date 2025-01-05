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

class CreateGroupChatViewViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val database = BurnerChatApp.appModule.usersRepository
    private var _usersList = MutableLiveData<MutableList<String>>(mutableListOf())

    val usersList :LiveData<MutableList<String>>
        get() = _usersList

    private var _createdChat = MutableLiveData<Boolean>(false)
    val createdChat: LiveData<Boolean>
        get() = _createdChat

    private var _dbUsersList = MutableLiveData<List<UserDTO>>(mutableListOf())
    val dbUsersList :LiveData<List<UserDTO>>
        get() = _dbUsersList

    private var _icon = MutableLiveData<String>("")
    val icon:LiveData<String>
        get()=_icon

    fun addUser(userDTO: UserDTO){
        _usersList.value!!.add(userDTO.email)
        _usersList.value = _usersList.value
    }

    fun removeUser(userDTO: UserDTO){
        _usersList.value!!.remove(userDTO.email)
        _usersList.value = _usersList.value
    }

    suspend fun getUsers(){
        var users = database.getUsers()
        _dbUsersList.value = database.getUsers()
    }

    suspend fun findUsers(string: String){
        val users = database.getUsersByString(string)
        _dbUsersList.value=users
    }

    fun setIcon(image:Bitmap){
        _icon.value = ImageUtils.convertToBase64(image)
    }

    fun addChat(name: String ) {
        // TODO: Check if the user exists in the db
        db.collection("users")
            .whereEqualTo("email", name)
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val participants = usersList.value!!
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

    fun isSelected(email:String):Boolean{
        return _usersList.value!!.contains(email)
    }

    fun isGroup():Boolean{
        return _usersList.value?.size!!>=2
    }



}