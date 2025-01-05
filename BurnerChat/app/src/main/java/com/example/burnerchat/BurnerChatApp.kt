package com.example.burnerchat

import android.app.Application
import android.content.Context
import com.example.burnerchat.firebase.repositories.ChatsRepository
import com.example.burnerchat.firebase.repositories.UsersRepository

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
    val chatsRepository: ChatsRepository
    val usersRepository: UsersRepository
    val contexto: Context
}


class AppModuleImpl(
    override val contexto: Context // No lo quito porque lo necesitaremos en el futuro para la db
) : AppModule {

    // Repository
    override val chatsRepository: ChatsRepository by lazy {
        ChatsRepository
    }
    override val usersRepository: UsersRepository by lazy {
        UsersRepository
    }

}
