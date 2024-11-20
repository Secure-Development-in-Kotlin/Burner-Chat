package com.example.burnerchat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.burnerchat.backend.socket.SocketConnection
import com.example.burnerchat.business.MainActions
import com.example.burnerchat.model.users.KeyPair
import com.example.burnerchat.model.users.User

class MainActivityViewModel : ViewModel() {
    // TODO: verificar si mejor tener valor o no de inicializacion
    private val _userName = MutableLiveData("User")

    private val socketConnection = BurnerChatApp.appModule.socketConnection

    val userName: LiveData<String>
        get() = _userName

    fun setName(name: String) {
        _userName.value = name
    }

    fun dispatchAction(actions: MainActions) {
        when (actions) {
            is MainActions.ConnectAs -> {
                // Iniciar socket
                socketConnection.initSocket(actions.name)
                // Establecer el usuario como loggeado a nivel global en la app
                BurnerChatApp.appModule.userLogged = User(KeyPair("a", "b"), actions.name)
            }

            else -> {
                Log.d("MainViewModel", "dispatchAction: Action not recognized")
            }
        }
    }

}