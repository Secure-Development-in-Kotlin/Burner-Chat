package com.example.burnerchat.model.users

class User (val keyPair: KeyPair) {
    private var loggedIn: Boolean = false
    private lateinit var iconPath: String
}