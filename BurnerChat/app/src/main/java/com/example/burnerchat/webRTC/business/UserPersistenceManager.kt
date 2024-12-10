package com.example.burnerchat.webRTC.business

import com.example.burnerchat.webRTC.model.users.User

object UserPersistenceManager {
    private lateinit var usuario : User

    fun setUser(user: User){
        this.usuario= user
    }

    fun getUser():User{
        return usuario
    }
}