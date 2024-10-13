package com.example.burnerchat.model.chats

import com.example.burnerchat.model.messages.Message
import com.example.burnerchat.model.users.User
import java.time.LocalDate

class Chat (val users : Map<String, User>) {
    private var messages : MutableList<Message> = mutableListOf()
    private val creationDate : LocalDate = LocalDate.now()
}