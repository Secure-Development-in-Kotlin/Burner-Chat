package com.example.burnerchat.business

import com.example.burnerchat.backend.webrtc.MessageType

object State {
    var isConnectedToServer: Boolean = false
    var isConnectToPeer: String? = null
    var connectedAs: String = ""
    var messagesFromServer: List<MessageType> = emptyList()
    var inComingRequestFrom: String = ""
    var isRtcEstablished: Boolean = false
    var peerConnectionString: String = ""
}