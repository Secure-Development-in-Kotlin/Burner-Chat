package com.example.burnerchat.webRTC.views.users

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.burnerchat.webRTC.model.users.User

class UserProfileViewModel : ViewModel() {

    private val _user = MutableLiveData<User>()
    val user : LiveData<User>
        get() = _user

    fun setUser(user: User){
        _user.postValue(user)
    }
    fun setIcon(bitmap: Bitmap){
        _user.value?.setIcon(bitmap)
        _user.postValue(_user.value)
    }
}