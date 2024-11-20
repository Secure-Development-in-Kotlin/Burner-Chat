package com.example.burnerchat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.burnerchat.backend.socket.SocketConnection
import com.example.burnerchat.business.MainActions
import com.example.burnerchat.business.MainScreenState
import com.example.burnerchat.model.users.KeyPair
import com.example.burnerchat.model.users.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class MainActivityViewModel : ViewModel() {
    private val _state = MutableStateFlow(
        MainScreenState.getInstance(BurnerChatApp.getContext())
    )
    val state: StateFlow<MainScreenState>
        get() = _state

    // TODO: verificar si mejor tener valor o no de inicializacion
    private val _userName = MutableLiveData("User")

    val userName: LiveData<String>
        get() = _userName

    fun setName(name: String) {
        _userName.value = name
    }

    fun dispatchAction(actions: MainActions) {
        when (actions) {
            is MainActions.ConnectAs -> {
                // Iniciar socket
                BurnerChatApp.appModule.socketConnection.initSocket(actions.name)
                // Establecer el usuario como loggeado a nivel global en la app
                BurnerChatApp.appModule.userLogged = User(KeyPair("a", "b"), actions.name)
                MainScreenState.updateInstance {
                    isConnectedToServer = true
                    connectedAs = actions.name
                }
            }

            else -> {
                Log.d("MainViewModel", "dispatchAction: Action not recognized")
            }
        }
    }

}