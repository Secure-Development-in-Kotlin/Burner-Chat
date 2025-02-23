package com.example.burnerchat.webRTC.backend.webrtc

import android.util.Log
import com.example.burnerchat.BurnerChatApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import java.nio.ByteBuffer

private const val TAG = "WebRtcManager"

sealed class MessageType {
    data class Info(val msg: String) : MessageType()
    data class MessageByMe(val msg: String) : MessageType()
    data class MessageByPeer(val msg: String) : MessageType()
    data object ConnectedToPeer : MessageType()
}

class WebRTCManager(
    private var socketConnection: com.example.burnerchat.webRTC.backend.socket.SocketConnection,
    private var target: String,
    private val userName: String,
) : PeerConnection.Observer {

    //private val socketConnection = BurnerChatApp.appModule.socketConnection
    private val scope = CoroutineScope(Dispatchers.IO)
    private val _messageStream = MutableSharedFlow<MessageType>()
    val messageStream: SharedFlow<MessageType>
        get() = _messageStream

    private val iceServers = listOf(
        PeerConnection.IceServer.builder("turn:83.54.225.146:3478?transport=tcp")
            .setUsername("burnerchat")
            .setPassword("burnerchat")
            .createIceServer()
    )
    private lateinit var peerConnectionFactory: PeerConnectionFactory
    private lateinit var peerConnection: PeerConnection
    private lateinit var dataChannel: DataChannel

    init {
        initializePeerConnectionFactory()
        createPeerConnection()
        createDataChannel("localDataChannel")
    }

    fun updateTarget(name: String) {
        target = name
    }

    private fun initializePeerConnectionFactory() {
        val options = PeerConnectionFactory
            .InitializationOptions
            .builder(BurnerChatApp.getContext())
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)

        val peerConnectionFactoryOptions = PeerConnectionFactory.Options()

        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(peerConnectionFactoryOptions)
            .createPeerConnectionFactory()
    }

    private fun createPeerConnection() {
        peerConnection =
            peerConnectionFactory.createPeerConnection(iceServers, this)!!
    }

    private fun createDataChannel(label: String) {
        val init = DataChannel.Init()
        dataChannel = peerConnection.createDataChannel(label, init)
        dataChannel.registerObserver(object : DataChannel.Observer {
            override fun onBufferedAmountChange(amount: Long) {
                Log.d(TAG, "data channel onBufferedAmountChange: ")
            }

            override fun onStateChange() {
                Log.d(TAG, "data channel onStateChange ")
            }

            override fun onMessage(buffer: DataChannel.Buffer?) {
                Log.d(TAG, "onMessage: at line 86")
                consumeDataChannelData(buffer)
            }
        })
    }

    private fun consumeDataChannelData(buffer: DataChannel.Buffer?) {
        buffer ?: return
        val data = buffer.data
        val bytes = ByteArray(data.capacity())
        data.get(bytes)
        val message = String(bytes, Charsets.UTF_8)
        // Handle the received message
        Log.d(TAG, "Received message: $message")
        scope.launch {
            if (message.isEmpty()) return@launch
            _messageStream.emit(
                MessageType.MessageByPeer(
                    message
                )
            )
        }
    }

    fun createOffer(from: String, target: String) {
        Log.d(TAG, "user is available creating offer")
        val sdpObserver = object : SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription?) {
                peerConnection.setLocalDescription(object : SdpObserver {
                    override fun onCreateSuccess(desc: SessionDescription?) {
                        Log.d(TAG, "onCreateSuccess: .... using socket to notify peer")
                    }

                    override fun onSetSuccess() {
                        Log.d(TAG, "onSetSuccess: ")
                        val offer = hashMapOf(
                            "sdp" to desc?.description,
                            "type" to desc?.type
                        )

                        socketConnection.sendMessageToSocket(
                            com.example.burnerchat.webRTC.backend.socket.MessageModel(
                                "create_offer", from, target, offer
                            )
                        )
                    }

                    override fun onCreateFailure(error: String?) {
                        Log.d(TAG, "error in creating offer $error")
                    }

                    override fun onSetFailure(error: String?) {
                        Log.d(TAG, "onSetFailure: err-> $error")
                    }
                }, desc)
                // Send offer to signaling server
                // Signaling server will forward it to the other peer
                // Upon receiving answer, set it as remote description
            }

            override fun onSetSuccess() {}
            override fun onCreateFailure(error: String?) {}
            override fun onSetFailure(error: String?) {}
        }

        val mediaConstraints = MediaConstraints()
        peerConnection.createOffer(sdpObserver, mediaConstraints)
    }

    fun addIceCandidate(p0: IceCandidate?) {
        peerConnection.addIceCandidate(p0)
    }

    override fun onSignalingChange(p0: PeerConnection.SignalingState?) {
    }

    override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
        when (newState) {
            PeerConnection.IceConnectionState.CONNECTED,
            PeerConnection.IceConnectionState.COMPLETED -> {
                // Peers are connected
                Log.d(TAG, "ICE Connection State: Connected ")
                scope.launch {
                    _messageStream.emit(
                        MessageType.ConnectedToPeer
                    )
                }
            }

            else -> {
                // Peers are not connected
                Log.d(TAG, "ICE Connection State: not Connected")
            }
        }
    }

    override fun onIceConnectionReceivingChange(p0: Boolean) {
    }

    override fun onIceGatheringChange(p0: PeerConnection.IceGatheringState?) {
    }

    override fun onIceCandidate(p0: IceCandidate?) {
        Log.d(TAG, "onIceCandidate called ....")
        addIceCandidate(p0)
        val candidate = hashMapOf(
            "sdpMid" to p0?.sdpMid,
            "sdpMLineIndex" to p0?.sdpMLineIndex,
            "sdpCandidate" to p0?.sdp
        )
        socketConnection.sendMessageToSocket(
            com.example.burnerchat.webRTC.backend.socket.MessageModel(
                "ice_candidate",
                userName,
                target,
                candidate
            )
        )
    }

    override fun onIceCandidatesRemoved(p0: Array<out IceCandidate>?) {
    }

    override fun onAddStream(p0: MediaStream?) {
    }

    override fun onRemoveStream(p0: MediaStream?) {
    }

    override fun onDataChannel(p0: DataChannel?) {
        Log.d(TAG, "onDataChannel: called for peers")
        p0!!.registerObserver(object : DataChannel.Observer {
            override fun onBufferedAmountChange(p0: Long) {
            }

            override fun onStateChange() {
            }

            override fun onMessage(p0: DataChannel.Buffer?) {
                Log.d(TAG, "onMessage: at line 196")
                consumeDataChannelData(p0)
            }
        })
    }

    override fun onRenegotiationNeeded() {
    }

    fun onRemoteSessionReceived(session: SessionDescription) {
        peerConnection.setRemoteDescription(object : SdpObserver {
            override fun onCreateSuccess(p0: SessionDescription?) {

            }

            override fun onSetSuccess() {
            }

            override fun onCreateFailure(p0: String?) {
            }

            override fun onSetFailure(p0: String?) {
            }

        }, session)
    }

    fun answerToOffer(lTarget: String?) {
        val constraints = MediaConstraints()
        peerConnection.createAnswer(object : SdpObserver {
            override fun onCreateSuccess(desc: SessionDescription?) {
                peerConnection.setLocalDescription(object : SdpObserver {
                    override fun onCreateSuccess(p0: SessionDescription?) {
                    }

                    override fun onSetSuccess() {
                        val answer = hashMapOf(
                            "sdp" to desc?.description,
                            "type" to desc?.type
                        )
                        socketConnection.sendMessageToSocket(
                            com.example.burnerchat.webRTC.backend.socket.MessageModel(
                                "create_answer", userName, lTarget, answer
                            )
                        )
                    }

                    override fun onCreateFailure(p0: String?) {
                    }

                    override fun onSetFailure(p0: String?) {
                    }

                }, desc)
            }

            override fun onSetSuccess() {
            }

            override fun onCreateFailure(p0: String?) {
            }

            override fun onSetFailure(p0: String?) {
            }

        }, constraints)
    }

    // Function to send messages between peers
    fun sendMessage(msg: String) {
        val buffer = ByteBuffer.wrap(msg.toByteArray(Charsets.UTF_8))
        val binaryData = DataChannel.Buffer(buffer, false)
        scope.launch {
            if (msg.isEmpty()) return@launch
            _messageStream.emit(
                MessageType.MessageByMe(msg)
            )
        }
        dataChannel.send(binaryData)
    }

}
