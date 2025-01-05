package com.example.burnerchat.firebase.views.users

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.firebase.repositories.ImageUtils
import com.example.burnerchat.firebase.views.chats.UserDTO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserProfileViewModel : ViewModel() {

    private val usersRepository = BurnerChatApp.appModule.usersRepository
    private val _user = MutableLiveData<UserDTO>()

    val user: LiveData<UserDTO>
        get() = _user

    fun setUser(user: UserDTO) {
        _user.value = (user)
    }

    suspend fun fetchUser(){
        _user.value = usersRepository.getUserData()
    }

    fun setIcon(bitmap: Bitmap) {
        _user.value = UserDTO(user.value?.email!!, ImageUtils.convertToBase64(bitmap))
        usersRepository.updateUser(usersRepository.getLoggedUser()!!,_user.value!!)
    }

    fun sendPanic() {
        viewModelScope.launch(Dispatchers.IO) {
            BurnerChatApp.appModule.usersRepository.sendPanic(usersRepository.getLoggedUser()!!)
        }
    }

    fun logOut(): Boolean {
        return usersRepository.logout()
    }
}