package com.example.burnerchat.webRTC.business

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import org.webrtc.MediaSource
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI

object ImageUtils {
    fun convertToBase64(bitmapIcon: Bitmap) : String{
        val output = ByteArrayOutputStream()
        bitmapIcon.compress(Bitmap.CompressFormat.JPEG,100, output)
        val byteArray = output.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun decodeFromBase64(base64String: String):Bitmap{
        val decoded = Base64.decode(base64String,Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decoded,0,decoded.size)
    }

    fun loadBitmapFromURL(path:String):Bitmap{
        val file = BitmapFactory.decodeFile(path)
        return file
    }
    fun loadBase64FromURL(path: String):String{
        return convertToBase64(loadBitmapFromURL(path))
    }

    fun loadBitmapFromURI(uri: Uri, contentResolver: ContentResolver): Bitmap? {
        try{
            val parcelFileDescriptor = contentResolver.openFileDescriptor(uri,"r")
            val fileDescriptor = parcelFileDescriptor!!.fileDescriptor
            val bitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor)
            parcelFileDescriptor.close()
            return bitmap
        }catch (e: IOException){
            e.printStackTrace()
        }
        return null
    }

}