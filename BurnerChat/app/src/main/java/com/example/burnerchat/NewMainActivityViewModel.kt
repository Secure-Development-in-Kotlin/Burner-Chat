package com.example.burnerchat

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class NewMainActivityViewModel : ViewModel() {
    private val _userName = MutableLiveData("User")

    val userName : LiveData<String>
        get()=_userName

    fun setName(name:String){
        _userName.value = name
    }

}