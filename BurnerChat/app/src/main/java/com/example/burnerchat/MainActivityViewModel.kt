package com.example.burnerchat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.webRTC.business.MainActions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivityViewModel : ViewModel() {
    // TODO: verificar si mejor tener valor o no de inicializacion
    private val _userName = MutableLiveData("User")

    val userName: LiveData<String>
        get() = _userName

    fun setName(name: String) {
        _userName.value = name
    }

    // Función para iniciar el login en el servidor
    fun login(name: String) {
        viewModelScope.launch(Dispatchers.IO) {
            BurnerChatApp.appModule.protocolHandler.dispatchAction(
                MainActions.ConnectAs(name)
            )
        }
    }
}