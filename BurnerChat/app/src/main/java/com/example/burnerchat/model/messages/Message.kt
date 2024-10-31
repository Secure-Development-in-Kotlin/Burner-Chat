package com.example.burnerchat.model.messages

import com.example.burnerchat.model.chats.Chat
import com.example.burnerchat.model.users.User
import java.time.LocalDate

interface Message {
    public fun getSentDate() : LocalDate
    public fun getContent() : String
    public fun getUser() : User
    public fun getChat() : Chat
    public fun getLastContent() : String
}