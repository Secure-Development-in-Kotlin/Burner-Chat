package com.example.burnerchat.firebase.views.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.firebase.model.chats.Chat
import kotlinx.coroutines.launch

class ChatInfoActivityViewModel : ViewModel() {
    private val database = BurnerChatApp.appModule.usersRepository
    private val chatsDB = BurnerChatApp.appModule.chatsRepository

    private val _chat = MutableLiveData<Chat>()
    val chat : LiveData<Chat>
        get() = _chat

    private val _usersDBList = MutableLiveData<List<UserUIInfo>>(mutableListOf())
    val usersDBList :LiveData<List<UserUIInfo>>
        get() = _usersDBList

    suspend fun getChatFromDB(uid:String){
        val chat = chatsDB.getChat(uid)
        setChat(chat)

    }

    fun setChat(chat: Chat){
        _chat.value = chat
    }

    suspend fun getUsers(){
        val emails = chat.value?.participants!!
        var users = database.getUsersByEmail(emails)
        _usersDBList.value = users
    }
}