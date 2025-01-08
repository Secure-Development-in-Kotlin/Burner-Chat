package com.example.burnerchat.firebase.model.messages

import com.google.firebase.Timestamp

interface Message {
    fun getSentDate(): Timestamp
    fun getContent(): String
    fun getUserEmail(): String
    fun getLastContent(): String
    fun getMessageTypeCode(userEmail: String): Int
    enum class LayoutType(value: Int) {
        TextoPropio(0),
        TextoAjeno(1),
        ImagenPropia(2),
        ImagenAjena(3),
    }
}