package com.example.burnerchat.views.chats

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.backend.socket.MessageModel
import com.example.burnerchat.backend.socket.SocketEvents
import com.example.burnerchat.backend.webrtc.IceCandidateModel
import com.example.burnerchat.backend.webrtc.MessageType
import com.example.burnerchat.backend.webrtc.WebRTCManager
import com.example.burnerchat.business.ChatsPersistenceManager
import com.example.burnerchat.business.MainActions
import com.example.burnerchat.business.MainOneTimeEvents
import com.example.burnerchat.business.State
import com.example.burnerchat.model.chats.Chat
import com.example.burnerchat.model.messages.messageImpls.TextMessage
import com.example.burnerchat.model.users.KeyPair
import com.example.burnerchat.model.users.User
import com.example.burnerchat.views.TAG
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription

class ChatsViewViewModel : ViewModel() {
    private val dataBase = ChatsPersistenceManager
    private val _chatsList = MutableLiveData(listOf<Chat>())
    private val _loggedUser = MutableLiveData(User(KeyPair("a", "b"), "userName"))

    // WebRTC connection variables
    private val socketConnection = BurnerChatApp.appModule.socketConnection
    private var rtcManager: WebRTCManager = BurnerChatApp.appModule.rtcManager
    private lateinit var newOfferMessage: MessageModel
    private val _oneTimeEvents = MutableLiveData<MainOneTimeEvents>()
    val oneTimeEvents: LiveData<MainOneTimeEvents>
        get() = _oneTimeEvents


    init {
        listenToSocketEvents()
    }

    private fun listenToSocketEvents() {
        viewModelScope.launch {
            socketConnection.event.collectLatest {
                when (it) {
                    is SocketEvents.ConnectionChange -> {
                        if (!it.isConnected) {
                            State.isConnectedToServer = false
                            State.connectedAs = User(KeyPair("", ""), "")
                        }
                    }

                    is SocketEvents.OnSocketMessageReceived -> {
                        handleNewMessage(it.message)
                    }

                    is SocketEvents.ConnectionError -> {
                        Log.d(TAG, "socket ConnectionError ${it.error}")
                    }
                }
            }
        }
    }

    private fun handleNewMessage(message: MessageModel) {
        Log.d(TAG, "handleNewMessage in VM")
        when (message.type) {
            "transfer_response" -> {
                Log.d(TAG, "transfer_response: ")
                // user is online / offline
                if (message.data == null) {
                    // TODO: change to toast??
                    Log.d(TAG, "User is not available")
                    return
                }
                // important to update target
                rtcManager = WebRTCManager(
                    userName = State.connectedAs.username,
                    target = message.data.toString(),
                )
                State.isRtcEstablished = true

                consumeEventsFromRTC()
                rtcManager.updateTarget(message.data.toString())
                // TODO: change to toast?
                Log.d(TAG, "User is Connected to ${message.data}")
                State.isConnectToPeer = message.data.toString()
                rtcManager.createOffer(
                    from = State.connectedAs.username,
                    target = message.data.toString(),
                )
            }

            "offer_received" -> {
                newOfferMessage = message
                Log.d(TAG, "offer_received ")
                State.inComingRequestFrom = message.name.orEmpty()
                viewModelScope.launch {
                    _oneTimeEvents.postValue(MainOneTimeEvents.GotInvite)
                }
            }

            "answer_received" -> {
                val session = SessionDescription(
                    SessionDescription.Type.ANSWER,
                    message.data.toString()
                )
                Log.d(TAG, "onNewMessage: answer received $session")
                rtcManager.onRemoteSessionReceived(session)
            }

            "ice_candidate" -> {
                try {
                    val receivingCandidate = BurnerChatApp.appModule.gson.fromJson(
                        BurnerChatApp.appModule.gson.toJson(message.data),
                        IceCandidateModel::class.java
                    )
                    Log.d(TAG, "onNewMessage: ice candidate $receivingCandidate")
                    rtcManager.addIceCandidate(
                        IceCandidate(
                            receivingCandidate.sdpMid,
                            Math.toIntExact(receivingCandidate.sdpMLineIndex.toLong()),
                            receivingCandidate.sdpCandidate
                        )
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

        }
    }

    private fun addChat(userName: String) {
        val otherUser = User(KeyPair("aa", "bb"), userName)
        val chat = Chat(otherUser)
        BurnerChatApp.appModule.chatsRepository.addChat(chat)

        // Update the chats from the view
        _chatsList.value = dataBase.getChats()
    }

    private fun consumeEventsFromRTC() {
        viewModelScope.launch {
            rtcManager.messageStream.collectLatest {
                if (it is MessageType.ConnectedToPeer) {
                    State.isRtcEstablished = true
                    State.peerConnectionString = "Is connected to peer ${State.isConnectToPeer}"
                }
                if (it is MessageType.MessageByMe) {
                    Log.d(TAG, "consumeEventsFromRTC: ${it.msg}")
                }
                // TODO: change to Toast?
                // or notification system
                Log.d(TAG, "$it")
            }
        }
    }

    fun dispatchAction(actions: MainActions) {
        when (actions) {
            is MainActions.AcceptIncomingConnection -> {
                val session = SessionDescription(
                    SessionDescription.Type.OFFER,
                    newOfferMessage.data.toString()
                )
                // move to new place
                consumeEventsFromRTC()
                rtcManager.onRemoteSessionReceived(session)
                rtcManager.answerToOffer(newOfferMessage.name)

                // Add chat to offer reciever db
                addChat(newOfferMessage.name.toString())
            }

            else -> {
                Log.d(TAG, "dispatchAction not recognized: $actions")
            }
        }
    }

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

}