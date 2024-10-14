package com.example.burnerchat.utils

data class DataModel(
    val type: DataModelType?=null,
    val username:String,
    val target:String?=null,
    val data:Any?=null
)

enum class DataModelType {
    SignIn, StartStreaming, EndCall, Offer, Answer, IceCandidates
}
