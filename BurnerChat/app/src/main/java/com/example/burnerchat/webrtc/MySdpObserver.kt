package com.example.burnerchat.webrtc

import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

// It is open to exent some situations anonymously
open class MySdpObserver : SdpObserver {
    override fun onCreateSuccess(desc: SessionDescription?) {
    }

    override fun onSetSuccess() {
    }

    override fun onCreateFailure(p0: String?) {
    }

    override fun onSetFailure(p0: String?) {
    }
}