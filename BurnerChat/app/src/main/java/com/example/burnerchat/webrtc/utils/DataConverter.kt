package com.example.burnerchat.webrtc.utils

import android.graphics.BitmapFactory
import android.util.Log
import org.webrtc.DataChannel
import java.io.File
import java.nio.ByteBuffer

object DataConverter {

    private const val TAG = "DataConverter"

    private var isWaitingForData = false
    private var nextInputType:String?=null

    // This function converts the data (text, image...) to the DataChannel.Buffer to send it over the socket
    fun convertToBuffer(type: FileMetaDataType, body:String) : DataChannel.Buffer {
        Log.d(TAG, "convertToBuffer: type= $type, body = $body")
        return when(type)
        {
            FileMetaDataType.TEXT -> {
                val fileMetadataBuffer = ByteBuffer.wrap(body.toByteArray())
                DataChannel.Buffer(fileMetadataBuffer, false)
            }
            FileMetaDataType.IMAGE -> {
                val imageFile = File(body)
                val fileData = imageFile.readBytes()
                DataChannel.Buffer(ByteBuffer.wrap(fileData), false)
            }
            FileMetaDataType.META_DATA_TEXT -> {
                val fileMetadataBuffer = ByteBuffer.wrap(FileMetaDataType.META_DATA_TEXT.name.toByteArray())
                DataChannel.Buffer(fileMetadataBuffer, false)
            }
            FileMetaDataType.META_DATA_IMAGE -> {
                val fileMetadataBuffer = ByteBuffer.wrap(FileMetaDataType.META_DATA_IMAGE.name.toByteArray())
                DataChannel.Buffer(fileMetadataBuffer, false)
            }
        }
    }

    fun convertToModel(buffer: DataChannel.Buffer): Pair<String, Any>?{
        Log.d(TAG, "convertToModel: is called status: $isWaitingForData")
        val data = ByteArray(buffer.data.remaining())
        buffer.data.get(data)
        return if(!isWaitingForData){ // we are waiting for metadata
            val metaDataString = String(data, Charsets.UTF_8)
            nextInputType = when (metaDataString) {
                "META_DATA_TEXT" -> {
                    "TEXT"
                }
                "META_DATA_IMAGE" -> {
                    "IMAGE"
                }
                else -> {
                   null
                }
            }
            Log.d(TAG, "convertToModel: next incoming is data, and the type is $isWaitingForData")
            isWaitingForData = true // with this, the next incoming message will get converted into text or image
            null
        } else{ // We are: type= $type, body = $body having incoming data
            when(nextInputType){
                "TEXT" -> {
                    nextInputType = null
                    isWaitingForData = false
                    val textDataString = String(data, Charsets.UTF_8)
                    "TEXT" to textDataString
                }
                "IMAGE" -> {
                    nextInputType = null
                    isWaitingForData = false
                    val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
                    "IMAGE" to bitmap
                }
                else ->{
                    null
                }
            }
        }
    }
}