package com.example.burnerchat.webRTC.views.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.webRTC.backend.webrtc.WebRTCManager
import com.example.burnerchat.webRTC.business.ChatsPersistenceManager
import com.example.burnerchat.webRTC.business.MainOneTimeEvents
import com.example.burnerchat.webRTC.model.chats.Chat
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class ChatsViewViewModel : ViewModel() {
    private val dataBase = ChatsPersistenceManager
    private val _chatsList = MutableLiveData(listOf<Chat>())
    private val _loggedUser = MutableLiveData<FirebaseUser>(null)

    // WebRTC connection variables
    //private val socketConnection = BurnerChatApp.appModule.socketConnection
    private var rtcManager: WebRTCManager = BurnerChatApp.appModule.rtcManager
    private lateinit var newOfferMessage: com.example.burnerchat.webRTC.backend.socket.MessageModel
    private val _oneTimeEvents = MutableLiveData<MainOneTimeEvents>()
    val oneTimeEvents: LiveData<MainOneTimeEvents>
        get() = _oneTimeEvents


    private fun addChat(name: String, participants: Array<String>) {
        val chat = Chat(name, participants)
        BurnerChatApp.appModule.chatsRepository.addChat(chat)

        // Update the chats from the view
        viewModelScope.launch {
            _chatsList.value = dataBase.getChats()
        }
    }

    val chatsList: LiveData<List<Chat>>
        get() = _chatsList

    // TODO: quitar, tenemos el usuario loggeado por firebase siempre accesible
    val loggedUser: LiveData<FirebaseUser>
        get() = _loggedUser

    // TODO: quitar
    fun logIn(user: FirebaseUser) {
        _loggedUser.value = user
        //TODO Aquí irá el MainActions.ConnectAs(user.userName)
    }

    fun init() {
        viewModelScope.launch {
            _chatsList.value = dataBase.getChats()
        }
    }

    suspend fun getChats() {
        _chatsList.value = dataBase.getChats()
    }

}