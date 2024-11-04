package com.example.burnerchat.model.chats

import com.example.burnerchat.model.messages.Message
import com.example.burnerchat.model.users.User
import java.time.LocalDate


class Chat (val users : Map<String, User>) {
    private var messages : MutableList<Message> = mutableListOf()
    private val creationDate : LocalDate = LocalDate.now()

    /**
     * Returns the other user
     */
    public fun getOtherUser(id : String) : User?{
        var keys = users.keys
        // TODO: refactor to do it better when only chat with one user
        if (keys.size == 1){
            return users[keys.first()]
        }

        var result : User? = null
        for (key in keys){
            if(key != id){
                return users[key]
            }

        }
        return result
    }

    fun getLastMessage() : Message {
        return messages.last()
    }

    fun addMessage(message: Message){
        messages.add(message)
    }
}