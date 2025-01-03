package com.example.burnerchat.webRTC.views.users

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.burnerchat.BurnerChatApp
import com.google.firebase.auth.FirebaseUser

class UserProfileViewModel : ViewModel() {

    private val _user = MutableLiveData<FirebaseUser>()

    val user: LiveData<FirebaseUser>
        get() = _user

    fun setUser(user: FirebaseUser) {
        _user.value = (user)
    }

    fun setIcon(bitmap: Bitmap) {
//        _user.value?.setIcon(bitmap)
        _user.value = (_user.value)
    }

    fun sendPanic() {
        BurnerChatApp.appModule.usersRepository.sendPanic()
    }
}