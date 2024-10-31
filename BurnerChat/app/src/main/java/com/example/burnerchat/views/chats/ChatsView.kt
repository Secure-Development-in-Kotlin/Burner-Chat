package com.example.burnerchat.views.chats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.MainActivity
import com.example.burnerchat.R
import com.example.burnerchat.model.chats.Chat
import com.example.burnerchat.model.messages.Message
import com.example.burnerchat.model.messages.messageImpls.TextMessage
import com.example.burnerchat.model.users.KeyPair
import com.example.burnerchat.model.users.User
import com.example.burnerchat.views.users.AddChatActivity
import com.example.burnerchat.views.users.UserProfileActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ChatsView : AppCompatActivity() {


    //Main recyclerView
    private lateinit var rvChats : RecyclerView;

    //ImageView that displays the profile icon
    private lateinit var ivIcon : ImageView;

    //FAB which opens the add chat menu
    private lateinit var fabAdd : FloatingActionButton

    //Chats list
    private var chatsList: MutableList<Chat> = mutableListOf()

    //Logged user
    private lateinit var loggedUser : User

    //Users List
    private var usersList: MutableList<User> = mutableListOf()
    /**
     * Initializes all components
     */
    private fun initComponents(){
        rvChats = findViewById(R.id.rcChats);
        ivIcon = findViewById(R.id.ivIcon);
        fabAdd = findViewById(R.id.fabAdd);

        initChatRecycler()
        initFAB()
        initIcon()
    }

    private fun initIcon() {
        ivIcon.setOnClickListener{
            val intent = Intent(this, UserProfileActivity::class.java)
            intent.putExtra(UserProfileActivity.CLAVE_NOMBRE_USUARIO, loggedUser.userName.toString())
            Log.d("chat", loggedUser.userName)
            intent.putExtra(UserProfileActivity.CLAVE_CLAVE_PUBLICA, loggedUser.keyPair.publicKey.toString())
            startActivity(intent)
        }
    }

    private fun initFAB() {
        fabAdd.setOnClickListener{
            val intent = Intent(this, AddChatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initChatRecycler() {
        val customAdapter = ChatsAdapter(chatsList,loggedUser.keyPair.publicKey){
            chat ->
                val intent = Intent(this, MessagesActivity::class.java)
                Log.d("debug",chat?.getLastMessage()?.getContent().toString())
                intent.putExtra("chat", chat?.getLastMessage()?.getLastContent())
                startActivity(intent)
        }

        rvChats.layoutManager = LinearLayoutManager(this)
        rvChats.adapter = customAdapter
    }

    private fun initUser(){
        loggedUser = User(KeyPair("a","b"),intent.getStringExtra(MainActivity.CLAVE_NOMBRE_USUARIO).toString())
        Log.d("debug",loggedUser.userName)
    }

    private fun initChats(){
        for (user in usersList){
            var map : MutableMap<String, User> = mutableMapOf()
            map.put(loggedUser.keyPair.publicKey, loggedUser)
            map.put(user.keyPair.publicKey, user)
            var chat = Chat(map)
            addMessagesToAChat(chat, user, 12)
            chatsList.add(chat)
        }
    }

    private fun generateUsers(number: Int){
        for (i in (0..number)){
            usersList.add(User(KeyPair("sample$i", "sampleb$i"), "SampleUser$i"))
        }
    }

    private fun addMessagesToAChat(chat : Chat, user: User, number: Int){
        for (i in (0..number)){
            if (i%2==0)
                chat.addMessage(TextMessage("Text Message $i", user, chat))
            else
                chat.addMessage(TextMessage("Text Message $i", loggedUser, chat))
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chats_view)

        initUser()
        generateUsers(25)
        initChats()


        initComponents()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}