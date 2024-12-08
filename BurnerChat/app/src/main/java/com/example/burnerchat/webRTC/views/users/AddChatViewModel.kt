package com.example.burnerchat.webRTC.views.users

import androidx.lifecycle.ViewModel
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.users.KeyPair
import com.example.burnerchat.webRTC.model.users.User

class AddChatViewModel : ViewModel() {
    //private val socketConnection = BurnerChatApp.appModule.socketConnection

    fun connectToChat(userName: String) {
        val otherUser = User(KeyPair("aa", "bb"), userName)
        val chat = Chat(otherUser)
        BurnerChatApp.appModule.chatsRepository.addChat(chat)
//        viewModelScope.launch(Dispatchers.IO) {
//            dispatchAction(
//                MainActions.ConnectToUser(userName)
//            )
        // Creo que este comportamiento debe realizarse en la propia pantalla del chat creado (la de los mensajes del chat)
//            BurnerChatApp.appModule.protocolHandler.dispatchAction(
//                MainActions.ConnectToUser(userName)
//            )
//        }
    }

//    private fun dispatchAction(actions: MainActions) {
//        when (actions) {
//            is MainActions.ConnectToUser -> {
//                socketConnection.sendMessageToSocket(
//                    MessageModel(
//                        type = "start_transfer",
//                        name = State.connectedAs.username,
//                        target = actions.name,
//                        data = null,
//                    )
//                )
//            }
//
//            else -> {
//                Log.d("MainViewModel", "dispatchAction: Action not recognized")
//            }
//        }
//    }
}