package com.example.burnerchat.model.messages

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.messageImpls.TextMessage
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class MessagesIsYourMessageUnitTest {
    val userIdMock = "You"
    val userIdMock1 = "NotYou"
    @Test
    fun isYourMessageTest(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = userIdMock
        var chat = Chat("Chat1",list)
        var message = TextMessage("Texto",userResult)
        assertTrue(message.isYourMessage(userIdMock))
    }

    @Test
    fun isNotYourMessageTest(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = userIdMock
        var chat = Chat("Chat1",list)
        var message = TextMessage("Texto",userResult)
        assertFalse(message.isYourMessage(userIdMock1))
    }

}