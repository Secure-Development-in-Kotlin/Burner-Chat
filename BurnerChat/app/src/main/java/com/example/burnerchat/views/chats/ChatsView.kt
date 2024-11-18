package com.example.burnerchat.views.chats

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
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
    private var chatsList: List<Chat> = mutableListOf()

    //ViewModel
    private val viewModel: ChatsViewViewModel by viewModels()

    override fun onResume(){
        super.onResume()
        chatsList = viewModel.getChats()
    }
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

        viewModel.chatsList.observe(this){
            newChatsList->
            chatsList=newChatsList
            initChatRecycler()
        }
    }

    private fun initIcon() {
        ivIcon.setOnClickListener{
            val intent = Intent(this, UserProfileActivity::class.java)
            var loggedUser = viewModel.loggedUser.value
            intent.putExtra(UserProfileActivity.CLAVE_NOMBRE_USUARIO, loggedUser?.userName.toString())
            Log.d("chat", loggedUser?.userName!!)
            intent.putExtra(UserProfileActivity.CLAVE_CLAVE_PUBLICA, loggedUser?.keyPair?.publicKey.toString())
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
        val customAdapter = ChatsAdapter(viewModel.chatsList.value!!,viewModel.loggedUser.value?.keyPair?.publicKey!!){
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
        var loggedUser = User(KeyPair("a","b"),intent.getStringExtra(MainActivity.CLAVE_NOMBRE_USUARIO).toString())
        viewModel.logIn(loggedUser)
        Log.d("debug",loggedUser.userName)
    }

    private fun initVM(){
        viewModel.init()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chats_view)

        initUser()
        initVM()


        initComponents()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}