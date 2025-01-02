package com.example.burnerchat.model.messages

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

class MessagesGetLastContentUnitTest {
    @MockK
    val userMock = mockk<FirebaseUser>()
    @MockK
    val userMock1 = mockk<FirebaseUser>()

    @Before
    fun setUp(){
        every { userMock.email } returns "You"
        every { userMock1.email } returns "NotYou"
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
        val message = TextMessage("Mensaje1",userMock1,chat)
        chat.addMessage(message)
        assertEquals("Mensaje1", message.getLastContent())

    }
    @Test
    fun isImage(){
        val chat = createChat()
        val message = ImageMessage("Imagen1",chat,userMock1)
        chat.addMessage(message)
        assertEquals("Image message ", message.getLastContent())

    }
    @Test
    fun isImageAndText(){
        val chat = createChat()
        val message = ImageMessage("Imagen1",chat,userMock1)
        message.textContent = "Imagen1"
        chat.addMessage(message)
        assertEquals("Image message Imagen1", message.getLastContent())

    }
}