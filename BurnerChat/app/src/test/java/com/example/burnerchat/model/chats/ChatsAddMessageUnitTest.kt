package com.example.burnerchat.model.chats

import com.example.burnerchat.firebase.model.chats.Chat
import com.example.burnerchat.firebase.model.messages.messageImpls.ImageMessage
import com.example.burnerchat.firebase.model.messages.messageImpls.TextMessage
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class ChatsAddMessageUnitTest {

    val userIdMock = "You"
    val userIdMock1 = "NotYou"



    private fun createChat(): Chat {
        var list = Array<String>(2,{i->i.toString()})
        list[0]="You"
        list[1]="NotYou"
        var chat = Chat("Chat1",list)
        return chat
    }



    @Test
    fun addOneText(){
        val chat = createChat()
        val message = TextMessage("Mensaje1",userIdMock)
        chat.addMessage(message)
        assertEquals(1, chat.messages.count())
        assertEquals(message, chat.messages[0])

    }
    @Test
    fun addOneImage(){
        val chat = createChat()
        val message = ImageMessage("Mensaje1",userIdMock1)
        chat.addMessage(message)
        assertEquals(1, chat.messages.count())
        assertEquals(message, chat.messages[0])

    }

    @Test
    fun addTwo(){
        val chat = createChat()
        val message = TextMessage("Mensaje1",userIdMock)
        val message2 = TextMessage("Mensaje2",userIdMock1)

        chat.addMessage(message)
        assertEquals(1, chat.messages.count())
        assertEquals(message, chat.messages[0])
        chat.addMessage(message2)
        assertEquals(2, chat.messages.count())
        assertEquals(message2, chat.messages[1])

    }

    @Test
    fun addThree(){
        val chat = createChat()
        val message = TextMessage("Mensaje1",userIdMock1)
        val message2 = TextMessage("Mensaje2",userIdMock1)
        val message3 = TextMessage("Mensaje3",userIdMock1)


        chat.addMessage(message)
        assertEquals(1, chat.messages.count())
        assertEquals(message, chat.messages[0])
        chat.addMessage(message2)
        assertEquals(2, chat.messages.count())
        assertEquals(message2, chat.messages[1])
        chat.addMessage(message3)
        assertEquals(3, chat.messages.count())
        assertEquals(message3, chat.messages[2])

    }

    @Test
    fun addMixed(){
        val chat = createChat()
        val message = TextMessage("Mensaje1",userIdMock1)
        val message2 = TextMessage("Mensaje2",userIdMock1)
        val message3 = TextMessage("Mensaje3",userIdMock1)
        val iMessage = ImageMessage("Mensaje1",userIdMock1)

        chat.addMessage(message)
        assertEquals(1, chat.messages.count())
        assertEquals(message, chat.messages[0])
        chat.addMessage(message2)
        assertEquals(2, chat.messages.count())
        assertEquals(message2, chat.messages[1])
        chat.addMessage(message3)
        assertEquals(3, chat.messages.count())
        assertEquals(message3, chat.messages[2])
        chat.addMessage(iMessage)
        assertEquals(4, chat.messages.count())
        assertEquals(iMessage, chat.messages[3])

    }
}