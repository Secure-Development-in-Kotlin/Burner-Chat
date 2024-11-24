package com.example.burnerchat.model.users

class User (val keyPair: KeyPair, val username : String) {
    private var loggedIn: Boolean = false
    private lateinit var iconPath: String
}