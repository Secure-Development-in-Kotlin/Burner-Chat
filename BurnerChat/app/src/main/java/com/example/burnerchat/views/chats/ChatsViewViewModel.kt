package com.example.burnerchat.views.chats

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.business.ChatsPersistenceManager
import com.example.burnerchat.business.MainActions
import com.example.burnerchat.model.chats.Chat
import com.example.burnerchat.model.messages.messageImpls.TextMessage
import com.example.burnerchat.model.users.KeyPair
import com.example.burnerchat.model.users.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ChatsViewViewModel : ViewModel() {
    private val dataBase = ChatsPersistenceManager
    private val _chatsList = MutableLiveData(listOf<Chat>())
    private val _loggedUser = MutableLiveData(User(KeyPair("a","b"), "userName"))


    val chatsList: LiveData<List<Chat>>
        get() = _chatsList

    val loggedUser: LiveData<User>
        get() = _loggedUser

    fun getChats():List<Chat>{
        _chatsList.value = dataBase.getChats()
        return dataBase.getChats()
    }

    fun logIn(userName: String){
        val user = User(KeyPair("a","b"),userName)
        logIn(user)
    }
    fun logIn(user : User){
        _loggedUser.value = user
        //TODO Aquí irá el MainActions.ConnectAs(user.userName)
    }

    fun addChat(userName: String){
        var user = User(KeyPair("aa","bb"), userName)
        addChat(user)
    }

    fun addChat(user: User){
        var map = mutableMapOf<String, User>()
        map.put(_loggedUser.value?.keyPair?.publicKey!!, _loggedUser.value!!)
        map.put(user.keyPair.publicKey, user)
        val chat = Chat(map)
        addChat(chat)
    }

    fun addChat(chat : Chat){
        dataBase.addChat(chat)
        _chatsList.value = dataBase.getChats()
        //TODO Aquí irá MainActions.ConnectToUser(chat.getOtherUser())
    }

    private fun generateUsers(number: Int) : List<User>{
        var usersList = mutableListOf<User>()
        for (i in (0..number)){
            usersList.add(User(KeyPair("sample$i", "sampleb$i"), "SampleUser$i"))
        }
        return usersList
    }

    fun init(){
        var users = generateUsers(25)
        initChats(users)
        _chatsList.value = dataBase.getChats()
    }

    private fun initChats(usersList : List<User>){
        for (user in usersList){
            var map : MutableMap<String, User> = mutableMapOf()
            map.put(_loggedUser.value?.keyPair?.publicKey!!, _loggedUser.value!!)
            map.put(user.keyPair.publicKey, user)
            var chat = Chat(map)
            addMessagesToAChat(chat, user, 12)
            dataBase.addChat(chat)
        }
    }
    private fun addMessagesToAChat(chat : Chat, user: User, number: Int){
        for (i in (0..number)){
            if (i%2==0)
                chat.addMessage(TextMessage("Text Message $i", user, chat))
            else
                chat.addMessage(TextMessage("Text Message $i", _loggedUser.value!!, chat))
        }
    }

}