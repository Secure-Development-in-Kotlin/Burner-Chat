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
    val userIdMock = "You"
    val userIdMock1 = "NotYou"

    @Test
    fun yourTextMessage(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = userIdMock
        var chat = Chat("Chat1",list)
        var message = TextMessage("Texto",userResult)
        assertTrue(message.isYourMessage(userIdMock))
        assertEquals(Message.LayoutType.TextoPropio.ordinal, message.getMessageTypeCode(userIdMock))
    }

    @Test
    fun otherTextMessage(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = userIdMock1
        var chat = Chat("Chat1",list)
        var message = TextMessage("Texto",userResult)
        assertFalse(message.isYourMessage(userIdMock))
        assertEquals(Message.LayoutType.TextoAjeno.ordinal, message.getMessageTypeCode(userIdMock))
    }

    @Test
    fun yourImageMessage(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = userIdMock
        var chat = Chat("Chat1",list)
        var message = ImageMessage("Texto",userResult)
        assertTrue(message.isYourMessage(userIdMock))
        assertEquals(Message.LayoutType.ImagenPropia.ordinal, message.getMessageTypeCode(userIdMock))
    }

    @Test
    fun otherImageMessage(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = userIdMock1
        var chat = Chat("Chat1",list)
        var message = ImageMessage("Texto",userResult)
        assertFalse(message.isYourMessage(userIdMock))
        assertEquals(Message.LayoutType.ImagenAjena.ordinal, message.getMessageTypeCode(userIdMock))
    }

}