package com.example.burnerchat

import android.app.Application
import android.content.Context
import com.example.burnerchat.backend.socket.SocketConnection
import com.example.burnerchat.backend.webrtc.WebRTCManager
import com.example.burnerchat.business.ChatsPersistenceManager
import com.example.burnerchat.model.users.User
import com.google.gson.Gson

class BurnerChatApp : Application() {
    companion object {
        lateinit var appModule: AppModule
        private lateinit var application: Application

        fun getContext(): Context {
            return application
        }
    }

    override fun onCreate() {
        super.onCreate()
        appModule = AppModuleImpl(this)
        application = this
    }
}

//¿Por qué utilizamos una interfaz?
interface AppModule {
    val chatsRepository: ChatsPersistenceManager
    val socketConnection: SocketConnection
    val rtcManager: WebRTCManager
    val gson: Gson
    val contexto: Context
}


class AppModuleImpl(
    override val contexto: Context // No lo quito porque lo necesitaremos en el futuro para la db
) : AppModule {

    // Gson
    override val gson: Gson by lazy {
        Gson()
    }

    // Socket
    override val socketConnection: SocketConnection by lazy {
        SocketConnection()
    }

    // WebRTC
    override val rtcManager: WebRTCManager by lazy {
        WebRTCManager(
            userName = "",
            target = "",
        )
    }

    // Repository
    override val chatsRepository: ChatsPersistenceManager by lazy {
        ChatsPersistenceManager
    }

}
