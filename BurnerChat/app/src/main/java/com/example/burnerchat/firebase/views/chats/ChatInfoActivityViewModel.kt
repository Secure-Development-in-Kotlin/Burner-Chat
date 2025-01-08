package com.example.burnerchat.firebase.views.chats

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.firebase.model.chats.Chat
import com.example.burnerchat.firebase.repositories.ImageUtils

class ChatInfoActivityViewModel : ViewModel() {
    private val usersRepository = BurnerChatApp.appModule.usersRepository
    private val chatsDB = BurnerChatApp.appModule.chatsRepository


    private val _chat = MutableLiveData<Chat>()
    val chat: LiveData<Chat>
        get() = _chat

    private val _usersDBList = MutableLiveData<List<UserDTO>>(mutableListOf())
    val usersDBList: LiveData<List<UserDTO>>
        get() = _usersDBList

    private var _usersDBListAll = MutableLiveData<List<UserDTO>>(listOf())
    val usersDBListAll: LiveData<List<UserDTO>>
        get() = _usersDBListAll

    private var _selectedToAddUsersList = MutableLiveData<MutableList<String>>(mutableListOf())
    val selectedToAddUsersList: LiveData<MutableList<String>>
        get() = _selectedToAddUsersList

    private var _selectedToRemoveUsersList = MutableLiveData<MutableList<String>>(mutableListOf())
    val selectedToRemoveUsersList: LiveData<MutableList<String>>
        get() = _selectedToRemoveUsersList

    fun selectUserToAdd(user: UserDTO) {
        _selectedToAddUsersList.value?.add(user.email)
        _selectedToAddUsersList.value = _selectedToAddUsersList.value
    }

    fun deselectUserToAdd(user: UserDTO) {
        _selectedToAddUsersList.value?.remove(user.email)
        _selectedToAddUsersList.value = _selectedToAddUsersList.value
    }

    fun isInToAdd(email: String): Boolean {
        return _selectedToAddUsersList.value?.contains(email)!!
    }

    fun selectUserToRemove(user: UserDTO) {
        _selectedToRemoveUsersList.value?.add(user.email)
        _selectedToRemoveUsersList.value = _selectedToRemoveUsersList.value
    }

    fun deselectUserToRemove(user: UserDTO) {
        _selectedToRemoveUsersList.value?.remove(user.email)
        _selectedToRemoveUsersList.value = _selectedToRemoveUsersList.value
    }

    fun isInToRemove(email: String): Boolean {
        return _selectedToRemoveUsersList.value?.contains(email)!!
    }

    suspend fun getAddableUsers() {
        val users = usersRepository.getUsersExcept(chat.value?.participants?.toList()!!)
        _usersDBListAll.value = users
    }

    suspend fun findCurrentAddableUsers(string: String) {
        val users = usersRepository.getUsersByStringExcept(string, chat.value?.participants!!)
        _usersDBListAll.value = users
    }


    suspend fun getChatFromDB(uid: String) {
        val chat = chatsDB.getChat(uid)
        setChat(chat)

    }

    private fun setChat(chat: Chat) {
        _chat.value = chat
    }

    suspend fun getUsersInChat() {
        val emails = chat.value?.participants!!
        val users = usersRepository.getUsersByEmail(emails)
        _usersDBList.value = users
    }

    fun setIcon(bitmap: Bitmap) {
        chat.value?.imageUrl = ImageUtils.convertToBase64(bitmap)
        setChat(chat.value!!)
    }

    fun setNombre(name: String) {
        chat.value?.name = name
        setChat(chat.value!!)
    }

    // Si tienes un chat grupa (> 2 usuarios), devuelve si después de borrarlos sigues teniendo un chat grupal
    // Si tienes un chat individual devuelve false (no puedes borrar)
    fun canDelete(): Boolean {
        val initialLength = chat.value?.participants?.size!!
        val usersToAdd = _selectedToAddUsersList.value?.size!!
        val usersToRemove = _selectedToRemoveUsersList.value?.size!!
        if (initialLength > 2)
            return initialLength + usersToAdd - usersToRemove > 2
        return false
    }

    fun isGroup(): Boolean {
        return chat.value?.isGroup()!!
    }

    fun updateChat() {
        val list = chat.value?.participants?.toMutableList()!!
        list.removeAll(selectedToRemoveUsersList.value!!)
        list.addAll(selectedToAddUsersList.value!!)
        chat.value?.participants = (list.toList().toTypedArray())
        chatsDB.updateChat(chat.value!!)
    }


}