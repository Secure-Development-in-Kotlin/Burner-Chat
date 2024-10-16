package com.example.burnerchat.backend.socket

data class MessageModel(
    // TODO name it enum, and refactor it
    val type: String,
    val name: String? = null,
    val target: String? = null,
    val data: Any? = null
)