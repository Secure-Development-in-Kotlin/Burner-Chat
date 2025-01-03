package com.example.burnerchat.model.chats

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.messageImpls.ImageMessage
import com.example.burnerchat.webRTC.model.messages.messageImpls.TextMessage
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ChatsLastMessageUnitTest {

    @MockK
    val userIdMock = mockk<FirebaseUser>()
    @MockK
    val userIdMock1 = mockk<FirebaseUser>()

    @Before
    fun setUp(){
        every { getUserIdMock.email } returns "You"
        every { getUserIdMock1.email } returns "NotYou"
    }

    private fun createChat(): Chat {
        var list = Array<String>(2,{i->i.toString()})
        list[0]="You"
        list[1]="NotYou"
        var chat = Chat("Chat1",list)
        return chat
    }



    @Test
    fun isText(){
        val chat = createChat()
        val message = TextMessage("Mensaje1",getUserIdMock1,chat)
        chat.addMessage(message)
        assertEquals(message, chat.getLastMessage())

    }
    @Test
    fun isImage(){
        val chat = createChat()
        val message = ImageMessage("Mensaje1",chat,getUserIdMock1)
        chat.addMessage(message)
        assertEquals(message, chat.getLastMessage())

    }
}