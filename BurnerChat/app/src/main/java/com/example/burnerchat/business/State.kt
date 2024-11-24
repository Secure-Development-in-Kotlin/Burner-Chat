package com.example.burnerchat.business

import com.example.burnerchat.backend.webrtc.MessageType
import com.example.burnerchat.model.users.KeyPair
import com.example.burnerchat.model.users.User

object State {
    var isConnectedToServer: Boolean = false
    var isConnectToPeer: String? = null
    var connectedAs: User = User(KeyPair("", ""), "")
    var messagesFromServer: List<MessageType> = emptyList()
    var inComingRequestFrom: String = ""
    var isRtcEstablished: Boolean = false
    var peerConnectionString: String = ""
}