package com.example.burnerchat.business

import android.content.Context
import com.example.burnerchat.backend.webrtc.MessageType


class MainScreenState(
    var isConnectedToServer: Boolean = false,
    var isConnectToPeer: String? = null,
    var connectedAs: String = "",
    var messagesFromServer: List<MessageType> = emptyList(),
    var inComingRequestFrom: String = "",
    var isRtcEstablished: Boolean = false,
    var peerConnectionString: String = ""
) {
    companion object {
        @Volatile
        private var SINGLETON: MainScreenState? = null

        fun getInstance(context: Context?): MainScreenState {
            return SINGLETON ?: synchronized(this) {
                SINGLETON ?: MainScreenState().also { SINGLETON = it }
            }
        }

        fun updateInstance(updater: MainScreenState.() -> Unit) {
            SINGLETON?.apply(updater)
        }

        fun forPreview(): MainScreenState {
            return MainScreenState(
                isConnectedToServer = true,
                isConnectToPeer = "Moto",
                connectedAs = "P6",
                messagesFromServer = listOf(
                    MessageType.Info("Connected to Server as P6"),
                    MessageType.Info("Connected to Peer Moto"),
                    MessageType.MessageByMe("Hello Moto Edge"),
                    MessageType.MessageByPeer("Hi P6, we have"),
                    MessageType.MessageByMe("Which Android OS version are you running???"),
                ),
            )
        }
    }
}

sealed class MainActions {
    data class ConnectAs(val name: String) : MainActions()
    data object AcceptIncomingConnection : MainActions()
    data class ConnectToUser(val name: String) : MainActions()

    data class SendChatMessage(val msg: String) : MainActions()
}

sealed class MainOneTimeEvents {
    object GotInvite : MainOneTimeEvents()
}
