package com.example.burnerchat

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.backend.socket.SocketConnection
import com.example.burnerchat.business.MainActions
import com.example.burnerchat.business.MainScreenState
import com.example.burnerchat.business.State
import com.example.burnerchat.model.users.KeyPair
import com.example.burnerchat.model.users.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    // TODO: verificar si mejor tener valor o no de inicializacion
    private val _userName = MutableLiveData("User")

    val userName: LiveData<String>
        get() = _userName

    fun setName(name: String) {
        _userName.value = name
    }

    fun dispatchAction(actions: MainActions) {
        viewModelScope.launch(Dispatchers.IO) {
            when (actions) {
                is MainActions.ConnectAs -> {
                    // Iniciar socket
                    BurnerChatApp.appModule.socketConnection.initSocket(actions.name)
                    // Establecer el usuario como loggeado a nivel global en la app
                    BurnerChatApp.appModule.userLogged = User(KeyPair("a", "b"), actions.name)

                    State.connectedAs = actions.name
                    State.isConnectedToServer = true
                }

                else -> {
                    Log.d("MainViewModel", "dispatchAction: Action not recognized")
                }
            }
        }
    }

}