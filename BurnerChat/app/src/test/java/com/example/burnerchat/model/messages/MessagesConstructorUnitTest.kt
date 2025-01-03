package com.example.burnerchat.model.messages

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.messageImpls.ImageMessage
import com.example.burnerchat.webRTC.model.messages.messageImpls.TextMessage
import com.google.firebase.auth.FirebaseUser
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MessagesConstructorUnitTest {



    @Test
    fun constructorText(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = "User1"
        var chat = Chat("Chat1",list)
        var message = TextMessage("Texto",userResult)
        assertNotNull(message)
        assertEquals("Texto",message.getContent())
        assertNotNull(message.getUserId())
        assertEquals(userResult,message.getUserId())
        assertNotNull(message.getSentDate())
    }

    @Test
    fun constructorImage(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = "User1"
        var chat = Chat("Chat1",list)
        var message = ImageMessage("Imagen",userResult)
        assertNotNull(message)
        assertEquals("Imagen",message.getContent())
        assertNotNull(message.getUserId())
        assertEquals(userResult,message.getUserId())
        assertNotNull(message.getSentDate())
    }

}