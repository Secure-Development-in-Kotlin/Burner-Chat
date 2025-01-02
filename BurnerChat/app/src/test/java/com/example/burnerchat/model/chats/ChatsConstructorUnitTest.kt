package com.example.burnerchat.model.chats

import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.messages.Message
import com.example.burnerchat.webRTC.model.messages.messageImpls.TextMessage
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import net.bytebuddy.asm.Advice.Local
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.UUID

class ChatsConstructorUnitTest {
    @MockK
    val userMock = mockk<FirebaseUser>()
    @MockK
    val userMock1 = mockk<FirebaseUser>()

    @Before
    fun setUp(){
        every { userMock.email } returns "You"
        every { userMock1.email } returns "NotYou"
    }
    @Test
    fun baseConstructorTest(){
        var list = Array<String>(2,{i->i.toString()})
        list[0]="You"
        list[1]="NotYou"
        var chat = Chat("Chat1",list)
        assertNotNull(chat)
        assertArrayEquals(chat.participants, list)
        assertNotNull(chat.uid)
        assertNotNull(chat.messages)
        assertEquals(0, chat.messages.count())
        assertNotNull(chat.name)
        assertEquals(chat.name, "Chat1")
        assertNull(chat.imageUrl)
        assertNotNull(chat.creationDate)
    }
    @Test
    fun UUIDConstructorTest(){
        var id = UUID.randomUUID().toString()
        var list = Array<String>(2,{i->i.toString()})
        list[0]="You"
        list[1]="NotYou"
        var chat = Chat("Chat1",list,uid=id)
        assertNotNull(chat)
        assertArrayEquals(chat.participants, list)
        assertNotNull(chat.uid)
        assertEquals(id, chat.uid)
        assertNotNull(chat.messages)
        assertEquals(0, chat.messages.count())
        assertNotNull(chat.name)
        assertEquals(chat.name, "Chat1")
        assertNull(chat.imageUrl)
        assertNotNull(chat.creationDate)

    }
    @Test
    fun dateConstructorTest(){
        val date = Timestamp.now()
        var list = Array<String>(2,{i->i.toString()})
        list[0]="You"
        list[1]="NotYou"
        var chat = Chat("Chat1",list, creationDate = date)
        assertNotNull(chat)
        assertArrayEquals(chat.participants, list)
        assertNotNull(chat.uid)
        assertNotNull(chat.messages)
        assertEquals(0, chat.messages.count())
        assertNotNull(chat.name)
        assertEquals(chat.name, "Chat1")
        assertNull(chat.imageUrl)
        assertNotNull(chat.creationDate)
        assertEquals(chat.creationDate, date)

    }

    @Test
    fun listConstructorTest(){
        var list = Array<String>(2,{i->i.toString()})
        list[0]="You"
        list[1]="NotYou"
        val messages = generateMessages(12)
        var chat = Chat("Chat1",list, messages = messages)
        assertNotNull(chat)
        assertArrayEquals(chat.participants, list)
        assertNotNull(chat.uid)
        assertNotNull(chat.messages)
        assertNotNull(chat.name)
        assertEquals(chat.name, "Chat1")
        assertNull(chat.imageUrl)
        assertNotNull(chat.messages)
        assertEquals(12, chat.messages.count())
        assertEquals(chat.messages, messages)

    }

    @Test
    fun imgNotNullConstructorTest(){
        val img = "img"
        var list = Array<String>(2,{i->i.toString()})
        list[0]="You"
        list[1]="NotYou"
        val messages = generateMessages(12)
        var chat = Chat("Chat1",list, imageUrl = img)
        assertNotNull(chat)
        assertArrayEquals(chat.participants, list)
        assertNotNull(chat.uid)
        assertNotNull(chat.messages)
        assertEquals(0, chat.messages.count())
        assertNotNull(chat.name)
        assertEquals(chat.name, "Chat1")
        assertNotNull(chat.imageUrl)
        assertEquals(chat.imageUrl, img)
        assertNotNull(chat.messages)



    }
    @Test
    fun imgNullConstructorTest(){

        var list = Array<String>(2,{i->i.toString()})
        list[0]="You"
        list[1]="NotYou"
        val messages = generateMessages(12)
        var chat = Chat("Chat1",list, imageUrl = null)
        assertNotNull(chat)
        assertArrayEquals(chat.participants, list)
        assertNotNull(chat.uid)
        assertNotNull(chat.messages)
        assertEquals(0, chat.messages.count())
        assertNotNull(chat.name)
        assertEquals(chat.name, "Chat1")
        assertNull(chat.imageUrl)
        assertNotNull(chat.messages)
    }

    @Test
    fun fullNotNullConstructorTest(){
        var id = UUID.randomUUID().toString()
        val date = Timestamp.now()
        var list = Array<String>(2,{i->i.toString()})
        list[0]="You"
        list[1]="NotYou"
        val messages = generateMessages(12)
        val img = "img"
        var chat = Chat("Chat1",list,
            imageUrl = img,
            uid = id,
            creationDate = date,
            messages = messages,
            )
        assertNotNull(chat)
        assertArrayEquals(chat.participants, list)
        assertNotNull(chat.uid)
        assertEquals(id, chat.uid)
        assertNotNull(chat.messages)
        assertEquals(12, chat.messages.count())
        assertEquals(chat.messages, messages)
        assertNotNull(chat.name)
        assertEquals(chat.name, "Chat1")
        assertNotNull(chat.imageUrl)
        assertEquals(chat.imageUrl, img)
        assertNotNull(chat.messages)
        assertNotNull(chat.creationDate)
        assertEquals(chat.creationDate, date)
    }

    @Test
    fun fullImgNullConstructorTest(){
        var id = UUID.randomUUID().toString()
        val date = Timestamp.now()
        var list = Array<String>(2,{i->i.toString()})
        list[0]="You"
        list[1]="NotYou"
        val messages = generateMessages(12)
        var chat = Chat("Chat1",list,
            imageUrl = null,
            uid = id,
            creationDate = date,
            messages = messages,
        )
        assertNotNull(chat)
        assertArrayEquals(chat.participants, list)
        assertNotNull(chat.uid)
        assertEquals(id, chat.uid)
        assertNotNull(chat.messages)
        assertEquals(12, chat.messages.count())
        assertEquals(chat.messages, messages)
        assertNotNull(chat.name)
        assertEquals(chat.name, "Chat1")
        assertNull(chat.imageUrl)
        assertNotNull(chat.messages)
        assertNotNull(chat.creationDate)
        assertEquals(chat.creationDate, date)
    }

    private fun generateMessages(amount:Int):MutableList<Message>{
        val chat = Chat("a",Array<String>(2,{i->i.toString()}))
        val list = mutableListOf<Message>()
        for (i in 0..<amount){
            val message = TextMessage("Texto"+i,userMock1,chat)
            list.add(message)
        }
        return list
    }


}