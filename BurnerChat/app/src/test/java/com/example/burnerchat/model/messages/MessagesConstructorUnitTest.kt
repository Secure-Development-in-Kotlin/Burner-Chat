package com.example.burnerchat.model.messages

import android.content.Context
import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.messageImpls.ImageMessage
import com.example.burnerchat.webRTC.model.messages.messageImpls.TextMessage
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDateTime

class MessagesConstructorUnitTest {

    @MockK
    val userMock = mockk<FirebaseUser>(relaxed=true)


    @Test
    fun constructorText(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = userMock
        var chat = Chat("Chat1",list)
        var message = TextMessage("Texto",userResult,chat)
        assertNotNull(message)
        assertEquals("Texto",message.getContent())
        assertNotNull(message.getUser())
        assertEquals(userResult,message.getUser())
        assertNotNull(message.getSentDate())
    }

    @Test
    fun constructorImage(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = userMock
        var chat = Chat("Chat1",list)
        var message = ImageMessage("Imagen",chat,userResult)
        assertNotNull(message)
        assertEquals("Imagen",message.getContent())
        assertNotNull(message.getUser())
        assertEquals(userResult,message.getUser())
        assertNotNull(message.getSentDate())
    }

}