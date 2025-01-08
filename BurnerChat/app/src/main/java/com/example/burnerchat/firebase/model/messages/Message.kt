package com.example.burnerchat.firebase.model.messages

import com.google.firebase.Timestamp

interface Message {
    fun getSentDate(): Timestamp
    fun getContent(): String
    fun getUserEmail(): String
    fun getLastContent(): String
    fun getMessageTypeCode(userId: String): Int
    enum class LayoutType(value: Int){
        TextoPropio(0),
        TextoAjeno(1),
        ImagenPropia(2),
        ImagenAjena(3),
        VideoPropio(4),
        VideoAjeno(5),
        AudioPropio(6),
        AudioAjeno(7)
    }
}