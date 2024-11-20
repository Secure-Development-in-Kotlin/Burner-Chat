package com.example.burnerchat.backend

import android.app.Application
import android.content.Context

class BurnerChat: Application() {

    companion object{
        private lateinit var application: Application

        fun getContext(): Context{
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()
        application = this
    }

}