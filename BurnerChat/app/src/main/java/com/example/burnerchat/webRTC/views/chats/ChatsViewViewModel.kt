package com.example.burnerchat.webRTC.views.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.webRTC.backend.webrtc.WebRTCManager
import com.example.burnerchat.webRTC.business.ChatsPersistenceManager
import com.example.burnerchat.webRTC.business.MainActions
import com.example.burnerchat.webRTC.business.MainOneTimeEvents
import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.messageImpls.TextMessage
import com.example.burnerchat.webRTC.model.users.KeyPair
import com.example.burnerchat.webRTC.model.users.User
import kotlinx.coroutines.launch

class ChatsViewViewModel : ViewModel() {
    private val dataBase = ChatsPersistenceManager
    private val _chatsList = MutableLiveData(listOf<Chat>())
    private val _loggedUser = MutableLiveData(User(KeyPair("a", "b"), "userName"))

    // WebRTC connection variables
    //private val socketConnection = BurnerChatApp.appModule.socketConnection
    private var rtcManager: WebRTCManager = BurnerChatApp.appModule.rtcManager
    private lateinit var newOfferMessage: com.example.burnerchat.webRTC.backend.socket.MessageModel
    private val _oneTimeEvents = MutableLiveData<MainOneTimeEvents>()
    val oneTimeEvents: LiveData<MainOneTimeEvents>
        get() = _oneTimeEvents


    private fun addChat(userName: String) {
        val otherUser = User(KeyPair("aa", "bb"), userName)
        val chat = Chat(otherUser)
        BurnerChatApp.appModule.chatsRepository.addChat(chat)

        // Update the chats from the view
        _chatsList.value = dataBase.getChats()
    }

//    private fun consumeEventsFromRTC() {
//        viewModelScope.launch {
//            rtcManager.messageStream.collectLatest {
//                if (it is MessageType.ConnectedToPeer) {
//                    State.isRtcEstablished = true
//                    State.peerConnectionString = "Is connected to peer ${State.isConnectToPeer}"
//                }
//                if (it is MessageType.MessageByMe) {
//                    Log.d(TAG, "consumeEventsFromRTC: ${it.msg}")
//                }
//                // TODO: change to Toast?
//                // or notification system
//                Log.d(TAG, "$it")
//            }
//        }
//    }
//
//    fun dispatchAction(actions: MainActions) {
//        when (actions) {
//            is MainActions.AcceptIncomingConnection -> {
//                val session = SessionDescription(
//                    SessionDescription.Type.OFFER,
//                    newOfferMessage.data.toString()
//                )
//                // move to new place
//                consumeEventsFromRTC()
//                rtcManager.onRemoteSessionReceived(session)
//                rtcManager.answerToOffer(newOfferMessage.name)
//
//                // Add chat to offer reciever db
//                addChat(newOfferMessage.name.toString())
//            }
//
//            else -> {
//                Log.d(TAG, "dispatchAction not recognized: $actions")
//            }
//        }
//    }

    val chatsList: LiveData<List<Chat>>
        get() = _chatsList

    val loggedUser: LiveData<User>
        get() = _loggedUser

    fun getChats(): List<Chat> {
        _chatsList.value = dataBase.getChats()
        return dataBase.getChats()
    }

    fun logIn(userName: String) {
        val user = User(KeyPair("a", "b"), userName)
        logIn(user)
    }

    fun logIn(user: User) {
        _loggedUser.value = user
        //TODO Aquí irá el MainActions.ConnectAs(user.userName)
    }


    private fun generateUsers(number: Int): List<User> {
        var usersList = mutableListOf<User>()
        for (i in (0..number)) {
            usersList.add(User(KeyPair("sample$i", "sampleb$i"), "SampleUser$i"))
        }
        return usersList
    }

    fun init() {
        // Por ahora los quiero vacíos, no hardcodeados
        // N O
        var users = generateUsers(25)
        initChats(users)
        _chatsList.value = dataBase.getChats()
    }

    private fun initChats(usersList: List<User>) {
        for (otherUser in usersList) {
            val chat = Chat(otherUser)
            addMessagesToAChat(chat, otherUser, 12)
            dataBase.addChat(chat)
        }
    }

    private fun addMessagesToAChat(chat: Chat, user: User, number: Int) {
        for (i in (0..number)) {
            if (i % 2 == 0)
                chat.addMessage(TextMessage("Text Message $i", user, chat))
            else
                chat.addMessage(TextMessage("Text Message $i", _loggedUser.value!!, chat))
        }
    }

    // Método para aceptar una conexión entrante
    fun acceptIncomingConnection() {
        viewModelScope.launch {
            BurnerChatApp.appModule.protocolHandler.dispatchAction(
                MainActions.AcceptIncomingConnection
            )
        }
    }

}