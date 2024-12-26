package com.example.burnerchat.webRTC.model.messages

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.users.User
import java.time.LocalDateTime

interface Message {
    public fun getSentDate(): LocalDateTime
    public fun getContent(): String
    public fun getUser(): User
    public fun getChat(): Chat
    public fun getLastContent(): String
    public fun getMessageTypeCode(user: User): Int
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