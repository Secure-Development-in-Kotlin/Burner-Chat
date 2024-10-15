package com.example.burnerchat.webrtc

import android.content.Context
import com.example.burnerchat.webrtc.utils.DataModel
import com.example.burnerchat.webrtc.utils.DataModelType
import com.google.gson.Gson
import org.webrtc.*
import org.webrtc.PeerConnection.Observer
import javax.inject.Inject

class WebRtcClient @Inject constructor(
    private val context : Context,
    private val gson : Gson
) {
    private lateinit var username : String
    private lateinit var observer : Observer

    var listener : Listener?=null

    var receiverListener : ReceiverListener?=null

    private var peerConnection: PeerConnection?=null

    private val eglBaseContext = EglBase.create().eglBaseContext
    private val peerConnectionFactory by lazy { createPeerConnectionFactory()}

    private val dataChannelObserver = object : DataChannel.Observer{
        override fun onBufferedAmountChange(p0: Long) {
        }

        override fun onStateChange() {
        }

        // To notify that there is a new message incoming
        override fun onMessage(p0: DataChannel.Buffer?) {
            p0?.let { receiverListener?.onDataReceived(it) }
        }

    }

    // It is important to use TCP because UDP packets are usually blocked in internal networks
    private val iceServer = listOf(
        PeerConnection.IceServer.builder("turn:192.168.1.61:3478?transport=tcp")
            .setUsername("burnerchat")
            .setPassword("burnerchat")
            .createIceServer()
    )


    // For the SdpObserver
    private val mediaConstraint = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
        mandatory.add(MediaConstraints.KeyValuePair("RtpDataChannels", "true"))
    }

    init {
        initPeerConnectionFacory(context)
    }

    fun initializeWebrtcClient(
        username: String, observer: Observer
    ) {
        this.username = username
        this.observer = observer
        peerConnection = createPeerConnection(observer)
        createDataChannel()
    }

    private fun createDataChannel() {
        val initDataChannel = DataChannel.Init()
        val dataChannel = peerConnection?.createDataChannel("dataChannelLabel", initDataChannel)
        dataChannel?.registerObserver(dataChannelObserver)
    }

    // Initialize the peer connection Factory
    private fun initPeerConnectionFacory(application:Context)
    {
        val options = PeerConnectionFactory.InitializationOptions.builder(application).setEnableInternalTracer(true).setFieldTrials("WebRTC-H264HighProfile/Enabled/").createInitializationOptions()
        PeerConnectionFactory.initialize(options)
    }

    // Create the peer connection factory
    private fun createPeerConnectionFactory() : PeerConnectionFactory{
        return PeerConnectionFactory.builder().setVideoDecoderFactory(
            DefaultVideoDecoderFactory(eglBaseContext)
        ).setVideoEncoderFactory(
            DefaultVideoEncoderFactory(
                eglBaseContext, true, true
            )
        ).setOptions(PeerConnectionFactory.Options().apply {
            disableEncryption = false
            disableNetworkMonitor = false
        }).createPeerConnectionFactory()
    }

    // Create the peer connection
    private fun createPeerConnection(observer: Observer) : PeerConnection?{
        return peerConnectionFactory.createPeerConnection(iceServer, observer)
    }

    // Create a bay between two peers and send an offer (message)
    fun call(target: String){
        peerConnection?.createOffer(object: MySdpObserver(){// First create the offer
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection?.setLocalDescription(object: MySdpObserver(){// Then send this to the peer
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        listener?.onTransferEventToSocket(// Transfer some data to the socket
                            DataModel(
                                type = DataModelType.Offer,
                                username, // the username sender
                                target, //the target peer
                                desc?.description //the data to send
                            )
                        )
                    }
                }, desc)
            }
        }, mediaConstraint)
    }

    fun answer(target:String){
        peerConnection?.createAnswer(object: MySdpObserver(){
            override fun onCreateSuccess(desc: SessionDescription?) {
                super.onCreateSuccess(desc)
                peerConnection?.setLocalDescription(object : MySdpObserver(){
                    override fun onSetSuccess() {
                        super.onSetSuccess()
                        listener?.onTransferEventToSocket(
                            DataModel(
                                type = DataModelType.Answer,
                                username = username,
                                target = target,
                                data = desc?.description
                            )
                        )
                    }
                }, desc)
            }
        }, mediaConstraint)
    }

    fun onRemoteSessionReceived(sessionDescription: SessionDescription){
        peerConnection?.setRemoteDescription(MySdpObserver(), sessionDescription)
    }

    fun addIceCandidate(iceCandidate: IceCandidate)
    {
        peerConnection?.addIceCandidate(iceCandidate)
    }

    // Makes the signal and sends it to the other peer
    fun sendIceCandidate(candidate: IceCandidate, target : String)
    {
        addIceCandidate(candidate)
        listener?.onTransferEventToSocket(
            DataModel(
                type = DataModelType.IceCandidates,
                username = username,
                target = target,
                data = gson.toJson(candidate)
            )
        )
    }

    interface Listener {
        fun onTransferEventToSocket(data: DataModel)
    }

    interface ReceiverListener {
        fun onDataReceived(it: DataChannel.Buffer)
    }
}