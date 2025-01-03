package com.example.burnerchat.model.messages

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.Message
import com.example.burnerchat.webRTC.model.messages.messageImpls.ImageMessage
import com.example.burnerchat.webRTC.model.messages.messageImpls.TextMessage
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MessagesGetMessageTypeCodeUnitTest {
    @MockK
    val userIdMock = mockk<FirebaseUser>()
    @MockK
    val userIdMock1 = mockk<FirebaseUser>()

    @Before
    fun setUp(){
        every { getUserIdMock.email } returns "You"
        every { getUserIdMock1.email } returns "NotYou"
    }

    @Test
    fun yourTextMessage(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = getUserIdMock
        var chat = Chat("Chat1",list)
        var message = TextMessage("Texto",userResult,chat)
        assertTrue(message.isYourMessage(getUserIdMock))
        assertEquals(Message.LayoutType.TextoPropio.ordinal, message.getMessageTypeCode(getUserIdMock))
    }

    @Test
    fun otherTextMessage(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = getUserIdMock1
        var chat = Chat("Chat1",list)
        var message = TextMessage("Texto",userResult,chat)
        assertFalse(message.isYourMessage(getUserIdMock))
        assertEquals(Message.LayoutType.TextoAjeno.ordinal, message.getMessageTypeCode(getUserIdMock))
    }

    @Test
    fun yourImageMessage(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = getUserIdMock
        var chat = Chat("Chat1",list)
        var message = ImageMessage("Texto",chat,userResult)
        assertTrue(message.isYourMessage(getUserIdMock))
        assertEquals(Message.LayoutType.ImagenPropia.ordinal, message.getMessageTypeCode(getUserIdMock))
    }

    @Test
    fun otherImageMessage(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = getUserIdMock1
        var chat = Chat("Chat1",list)
        var message = ImageMessage("Texto",chat,userResult)
        assertFalse(message.isYourMessage(getUserIdMock))
        assertEquals(Message.LayoutType.ImagenAjena.ordinal, message.getMessageTypeCode(getUserIdMock))
    }

}