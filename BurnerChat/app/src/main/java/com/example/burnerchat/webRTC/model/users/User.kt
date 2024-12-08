package com.example.burnerchat.webRTC.model.users

class User(val keyPair: KeyPair, val username: String) {
    private var loggedIn: Boolean = false
    private lateinit var iconPath: String
}