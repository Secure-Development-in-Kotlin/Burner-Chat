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
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch

class ChatsViewViewModel : ViewModel() {
    private val dataBase = ChatsPersistenceManager
    private val _chatsList = MutableLiveData(listOf<Chat>())
    private val _loggedUser = MutableLiveData<FirebaseUser>(null)

    // WebRTC connection variables
    //private val socketConnection = BurnerChatApp.appModule.socketConnection
    private var rtcManager: WebRTCManager = BurnerChatApp.appModule.rtcManager
    private lateinit var newOfferMessage: com.example.burnerchat.webRTC.backend.socket.MessageModel
    private val _oneTimeEvents = MutableLiveData<MainOneTimeEvents>()
    val oneTimeEvents: LiveData<MainOneTimeEvents>
        get() = _oneTimeEvents


    private fun addChat(name: String, participants: Array<String>) {
        val chat = Chat(name, participants)
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

    // TODO: quitar, tenemos el usuario loggeado por firebase siempre accesible
    val loggedUser: LiveData<FirebaseUser>
        get() = _loggedUser

    fun getChats(): List<Chat> {
        _chatsList.value = dataBase.getChats()
        return dataBase.getChats()
    }

    // TODO: quitar
    fun logIn(user: FirebaseUser) {
        _loggedUser.value = user
        //TODO Aquí irá el MainActions.ConnectAs(user.userName)
    }

    fun init() {
        initChats()
        _chatsList.value = dataBase.getChats()
    }

    private fun initChats() {
        val chats = dataBase.getChats()
        if (chats.isEmpty()) {
            _chatsList.value = chats
        }
    }

    private fun addMessagesToAChat(chat: Chat, user: FirebaseUser, number: Int) {
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