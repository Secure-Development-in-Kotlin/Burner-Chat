package com.example.burnerchat.webrtc.utils

data class DataModel(
    val type: DataModelType?=null,
    val username:String,
    val target:String?=null,
    val data:Any?=null
)

enum class DataModelType {
    SignIn, StartConnection, Offer, Answer, IceCandidates
}
