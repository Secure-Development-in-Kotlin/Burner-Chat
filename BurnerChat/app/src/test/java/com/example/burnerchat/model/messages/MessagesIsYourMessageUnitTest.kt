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
    fun isYourMessageTest(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = getUserIdMock
        var chat = Chat("Chat1",list)
        var message = TextMessage("Texto",userResult,chat)
        assertTrue(message.isYourMessage(getUserIdMock))
    }

    @Test
    fun isNotYourMessageTest(){
        var list = Array<String>(2,{i->i.toString()})
        var userResult = getUserIdMock
        var chat = Chat("Chat1",list)
        var message = TextMessage("Texto",userResult,chat)
        assertFalse(message.isYourMessage(getUserIdMock1))
    }

}