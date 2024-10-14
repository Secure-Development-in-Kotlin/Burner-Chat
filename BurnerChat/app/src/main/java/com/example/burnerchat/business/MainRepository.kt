package com.example.burnerchat.business

import com.example.burnerchat.webrtc.socket.SocketClient
import com.example.burnerchat.webrtc.MyPeerObserver
import com.example.burnerchat.webrtc.WebRtcClient
import com.example.burnerchat.webrtc.utils.DataModel
import com.example.burnerchat.webrtc.utils.DataModelType
import com.google.gson.Gson
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import javax.inject.Inject
import org.webrtc.SessionDescription

class MainRepository @Inject constructor(
    private val socketClient: SocketClient,
    private val webRtcClient: WebRtcClient,
    private val gson: Gson
) : SocketClient.Listener, WebRtcClient.Listener, WebRtcClient.ReceiverListener {
    private lateinit var username: String
    private lateinit var target: String

    private var dataChannel : DataChannel?=null

    var listener : Listener?=null

    fun init(username:String)
    {
        this.username = username
        initSocket()
        initWebRtcClient()
    }

    // Initialize the socket
    private fun initSocket(){
        socketClient.listener = this
        socketClient.init(username)
    }

    // Initialize the WebRtcClient
    private fun initWebRtcClient(){
        webRtcClient.listener = this
        webRtcClient.receiverListener = this
        webRtcClient.initializeWebrtcClient(username, object : MyPeerObserver() {
            override fun onIceCandidate(p0: IceCandidate?) {
                super.onIceCandidate(p0)
                p0?.let {
                    webRtcClient.sendIceCandidate(it, target)
                }
            }

            override fun onDataChannel(p0: DataChannel?) {
                super.onDataChannel(p0)
                dataChannel = p0
                listener?.onDataChannelReceived()
            }
        })
    }

    // ---------- NEGOTIATION FUNCTIONS ----------

    // Here we start the connection between two peers
    // The user will send a connection request and if the other accepts, the conversation will keep
    // going with the startCall func
    fun sendStartConnection(target: String)
    {
        this.target = target
        socketClient.sendMessageToSocket(
            DataModel(
                type = DataModelType.StartConnection,
                username = username,
                target = target,
                data = null
            )
        )
    }

    // The comunication begins between two peers
    fun startCall(target: String){
        webRtcClient.call(target)
    }

    /*
    fun sendTextToDataChannel(text:String){
        sendBufferToDataChannel(DataConverter.convertToBuffer(FileMetaDataType.META_DATA_TEXT,text))
        sendBufferToDataChannel(DataConverter.convertToBuffer(FileMetaDataType.TEXT,text))
    }

    fun sendImageToChannel(path:String){
        sendBufferToDataChannel(DataConverter.convertToBuffer(FileMetaDataType.META_DATA_IMAGE,path))
        sendBufferToDataChannel(DataConverter.convertToBuffer(FileMetaDataType.IMAGE,path))
    }
     */

    private fun sendBufferToDataChannel(buffer: DataChannel.Buffer){
        dataChannel?.send(buffer)

    }

    override fun onNewMessageReceived(model: DataModel) {
        when(model.type)
        {
            DataModelType.StartConnection -> { //notify the UI that there is a new connection request
                this.target = model.username // We store the target's username
                listener?.onConnectionRequestReceived(model.username)
            }
            DataModelType.Offer -> { // we receive an offer (any kind of message -> text, video, audio...)
                webRtcClient.onRemoteSessionReceived(
                    SessionDescription(
                        SessionDescription.Type.OFFER, model.data.toString()
                    )
                )
                this.target = model.username // restore the target
                webRtcClient.answer(target) // answer the petition
            }
            DataModelType.Answer -> { // Respond a petition
                webRtcClient.onRemoteSessionReceived(
                    SessionDescription(
                        SessionDescription.Type.ANSWER, model.data.toString()
                    )
                )
            }
            DataModelType.IceCandidates -> {
                val candidates = try {
                    gson.fromJson(model.data.toString(), IceCandidate::class.java)
                }catch(e : Exception){
                    e.printStackTrace()
                    null
                }
                // If the candidate s not known, we add it to our known connections
                candidates?.let {
                    webRtcClient.addIceCandidate(it)
                }
            }
            else -> Unit // does nothing
        }
    }

    override fun onTransferEventToSocket(data: DataModel) {
        socketClient.sendMessageToSocket(data)
    }

    override fun onDataReceived(it: DataChannel.Buffer) {
        listener?.onDatareceivedFromChannel(it)
    }

    // Listener to notify to the UI that something is going on behind
    interface Listener {
        fun onConnectionRequestReceived(target: String)
        fun onDataChannelReceived()// Status of our peer connection
        fun onDatareceivedFromChannel(it: DataChannel.Buffer)
    }
}