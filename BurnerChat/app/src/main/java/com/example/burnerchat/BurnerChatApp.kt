package com.example.burnerchat

import android.app.Application
import android.content.Context
import com.example.burnerchat.webRTC.backend.webrtc.WebRTCManager
import com.example.burnerchat.webRTC.business.ChatsPersistenceManager
import com.example.burnerchat.webRTC.business.UserPersistenceManager
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
    val usersRepository: UserPersistenceManager
    val contexto: Context
}


class AppModuleImpl(
    override val contexto: Context // No lo quito porque lo necesitaremos en el futuro para la db
) : AppModule {

    // Repository
    override val chatsRepository: ChatsPersistenceManager by lazy {
        ChatsPersistenceManager
    }
    override val usersRepository: UserPersistenceManager by lazy {
        UserPersistenceManager
    }

}
