package com.example.burnerchat.webRTC.views

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.webRTC.backend.webrtc.MessageType
import com.example.burnerchat.webRTC.backend.webrtc.WebRTCManager
import com.example.burnerchat.webRTC.business.MainActions
import com.example.burnerchat.webRTC.business.MainOneTimeEvents
import com.example.burnerchat.webRTC.business.MainScreenState
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch


class MessagesActivityViewModel : ViewModel() {
    private val _state = MutableStateFlow(
        MainScreenState.getInstance(BurnerChatApp.getContext())
    )
    val state: StateFlow<MainScreenState>
        get() = _state

    private lateinit var newOfferMessage: com.example.burnerchat.webRTC.backend.socket.MessageModel

    private val _oneTimeEvents = MutableSharedFlow<MainOneTimeEvents>()
    val oneTimeEvents: Flow<MainOneTimeEvents>
        get() = _oneTimeEvents.asSharedFlow()

    //private val socketConnection = BurnerChatApp.appModule.socketConnection
    private val gson = Gson()
    private var rtcManager: WebRTCManager = BurnerChatApp.appModule.rtcManager

    init {
        //listenToSocketEvents()
        Log.d(TAG, "State: ${state.value}")
    }

//    private fun listenToSocketEvents() {
//        viewModelScope.launch {
//            socketConnection.event.collectLatest {
//                when (it) {
//                    is SocketEvents.ConnectionChange -> {
//                        if (!it.isConnected) {
//                            MainScreenState.updateInstance {
//                                isConnectedToServer = false
//                                connectedAs = ""
//                            }
//                        }
//                    }
//
//                    is SocketEvents.OnSocketMessageReceived -> {
//                        handleNewMessage(it.message)
//                    }
//
//                    is SocketEvents.ConnectionError -> {
//                        Log.d(TAG, "socket ConnectionError ${it.error}")
//                    }
//                }
//            }
//        }
//    }

//    private fun handleNewMessage(message: MessageModel) {
//        Log.d(TAG, "handleNewMessage in VM")
//        when (message.type) {
//            "user_already_exists" -> {
//                sendMessageToUi(MessageType.Info("User already exists"))
//            }
//
//            "user_stored" -> {
//                Log.d(TAG, "User stored in socket")
//                sendMessageToUi(MessageType.Info("User stored in socket"))
//                MainScreenState.updateInstance {
//                    isConnectedToServer = true
//                    connectedAs = message.data.toString()
//                }
//            }
//
//            "transfer_response" -> {
//                Log.d(TAG, "transfer_response: ")
//                // user is online / offline
//                if (message.data == null) {
//                    sendMessageToUi(MessageType.Info("User is not available"))
//                    return
//                }
//                // important to update target
//                rtcManager = WebRTCManager(
//                    userName = state.value.connectedAs,
//                    target = message.data.toString(),
//                )
//                MainScreenState.updateInstance {
//                    isRtcEstablished = true
//                }
//
//                consumeEventsFromRTC()
//                rtcManager.updateTarget(message.data.toString())
//                sendMessageToUi(MessageType.Info("User is Connected to ${message.data}"))
//                MainScreenState.updateInstance {
//                    isConnectToPeer = message.data.toString()
//                }
//                rtcManager.createOffer(
//                    from = state.value.connectedAs,
//                    target = message.data.toString(),
//                )
//            }
//
//            "offer_received" -> {
//                newOfferMessage = message
//                Log.d(TAG, "offer_received ")
//                MainScreenState.updateInstance {
//                    inComingRequestFrom = message.name.orEmpty()
//                }
//                viewModelScope.launch {
//                    _oneTimeEvents.emit(
//                        MainOneTimeEvents.GotInvite
//                    )
//                }
//            }
//
//            "answer_received" -> {
//                val session = SessionDescription(
//                    SessionDescription.Type.ANSWER,
//                    message.data.toString()
//                )
//                Log.d(TAG, "onNewMessage: answer received $session")
//                rtcManager.onRemoteSessionReceived(session)
//            }
//
//            "ice_candidate" -> {
//                try {
//                    val receivingCandidate = gson.fromJson(
//                        gson.toJson(message.data),
//                        IceCandidateModel::class.java
//                    )
//                    Log.d(TAG, "onNewMessage: ice candidate $receivingCandidate")
//                    rtcManager.addIceCandidate(
//                        IceCandidate(
//                            receivingCandidate.sdpMid,
//                            Math.toIntExact(receivingCandidate.sdpMLineIndex.toLong()),
//                            receivingCandidate.sdpCandidate
//                        )
//                    )
//                } catch (e: Exception) {
//                    e.printStackTrace()
//                }
//            }
//        }
//    }


//    private fun consumeEventsFromRTC() {
//        viewModelScope.launch {
//            rtcManager.messageStream.collectLatest {
//                if (it is MessageType.ConnectedToPeer) {
//                    MainScreenState.updateInstance {
//                        isRtcEstablished = true
//                        peerConnectionString = "Is connected to peer ${state.value.isConnectToPeer}"
//                    }
//                }
//                if (it is MessageType.MessageByMe) {
//                    Log.d(TAG, "consumeEventsFromRTC: ${it.msg}")
//                }
//                sendMessageToUi(msg = it)
//            }
//        }
//    }

    // This function sends the message of the user to the User Interface
    private fun sendMessageToUi(msg: MessageType) {
        viewModelScope.launch(Dispatchers.IO) {
            MainScreenState.updateInstance {
                messagesFromServer = state.value.messagesFromServer + msg
            }
        }
    }

    // Método para aceptar la conexión entrante
    fun acceptConnection() {
        viewModelScope.launch(Dispatchers.IO) {
            BurnerChatApp.appModule.protocolHandler.dispatchAction(
                MainActions.AcceptIncomingConnection
            )
        }
    }

//    fun dispatchAction(actions: MainActions) {
//        when (actions) {
//            is MainActions.ConnectAs -> {
//                socketConnection.initSocket(actions.name)
//            }
//
//            is MainActions.AcceptIncomingConnection -> {
//                val session = SessionDescription(
//                    SessionDescription.Type.OFFER,
//                    newOfferMessage.data.toString()
//                )
//                // move to new place
//                consumeEventsFromRTC()
//                rtcManager.onRemoteSessionReceived(session)
//                rtcManager.answerToOffer(newOfferMessage.name)
//            }
//
//            is MainActions.ConnectToUser -> {
//                socketConnection.sendMessageToSocket(
//                    MessageModel(
//                        type = "start_transfer",
//                        name = state.value.connectedAs,
//                        target = actions.name,
//                        data = null,
//                    )
//                )
//            }
//            // In this part the message is send using the WebRtcManager
//            is MainActions.SendChatMessage -> {
//                rtcManager.sendMessage(actions.msg)
//            }
//        }
//    }

}