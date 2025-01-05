package com.example.burnerchat.webRTC.views.chats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.webRTC.business.ChatsPersistenceManager
import com.example.burnerchat.webRTC.business.UserPersistenceManager
import com.example.burnerchat.webRTC.model.chats.Chat
import kotlinx.coroutines.launch

class ChatsViewViewModel : ViewModel() {
    private val dataBase = ChatsPersistenceManager
    private val usersRepo = BurnerChatApp.appModule.usersRepository
    private val _chatsList = MutableLiveData(listOf<Chat>())

    fun init() {
        viewModelScope.launch {
            dataBase.listenToChatsRealtime { chats ->
                _chatsList.value = chats
                Log.d("Chats", chats.toString())
            }
        }


    }

    suspend fun fetchUser():UserUIInfo?{
        return usersRepo.getUserData()
    }

    private fun addChat(name: String, participants: Array<String>) {
        val chat = Chat(name, participants)
        BurnerChatApp.appModule.chatsRepository.addChat(chat)

        // Update the chats from the view
        viewModelScope.launch {
            _chatsList.value = dataBase.getChats()
        }
    }

    val chatsList: LiveData<List<Chat>>
        get() = _chatsList

    suspend fun getChats() {
        _chatsList.value = dataBase.getChats()
    }

}