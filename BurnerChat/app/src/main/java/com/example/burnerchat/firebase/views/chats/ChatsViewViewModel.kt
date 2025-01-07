package com.example.burnerchat.firebase.views.chats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.firebase.repositories.ChatsRepository
import com.example.burnerchat.firebase.model.chats.Chat
import kotlinx.coroutines.launch

class ChatsViewViewModel : ViewModel() {
    private val chatsRepository = ChatsRepository
    private val usersRepo = BurnerChatApp.appModule.usersRepository
    private val _chatsList = MutableLiveData(listOf<Chat>())

    fun init() {
        viewModelScope.launch {
            chatsRepository.listenToChatsRealtime { chats ->
                _chatsList.value = chats
                Log.d("Chats", chats.toString())
            }
        }


    }

    suspend fun fetchUser(): UserDTO? {
        return usersRepo.getUserData()
    }

    val chatsList: LiveData<List<Chat>>
        get() = _chatsList

    suspend fun getChats() {
        _chatsList.value = chatsRepository.getChats()
    }

}