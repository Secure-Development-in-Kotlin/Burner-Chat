package com.example.burnerchat.webRTC.model.users

import android.graphics.Bitmap
import com.example.burnerchat.webRTC.business.ImageUtils

class User(val keyPair: KeyPair, val username: String) {
    private var loggedIn: Boolean = false
    private var icon: String =""

    fun setIcon(bitmapIcon: Bitmap){
        setIcon(ImageUtils.convertToBase64(bitmapIcon))
    }
    fun setIcon(icono:String){
        this.icon = icono
    }

    fun getIcon():String{
        return icon
    }
}