package com.example.burnerchat.views.users

import android.content.Intent
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.backend.socket.MessageModel
import com.example.burnerchat.business.MainActions
import com.example.burnerchat.model.chats.Chat
import com.example.burnerchat.model.users.KeyPair
import com.example.burnerchat.model.users.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddChatViewModel : ViewModel() {
    private val socketConnection = BurnerChatApp.appModule.socketConnection


    fun addChat(userName: String) {
        val otherUser = User(KeyPair("aa", "bb"), userName)
        val chat = Chat(otherUser)
        BurnerChatApp.appModule.chatsRepository.addChat(chat)
        viewModelScope.launch(Dispatchers.IO) {
            dispatchAction(
                MainActions.ConnectToUser(chat.getOtherUser().userName)
            )
        }
    }

    private fun dispatchAction(actions: MainActions) {
        when (actions) {
            is MainActions.ConnectToUser -> {
                // TODO
            }

            else -> {
                Log.d("MainViewModel", "dispatchAction: Action not recognized")
            }
        }
    }
}