package com.example.burnerchat.webRTC.views.chats

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.MainActivity
import com.example.burnerchat.R
import com.example.burnerchat.webRTC.business.ImageUtils
import com.example.burnerchat.webRTC.business.MainOneTimeEvents
import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.users.KeyPair
import com.example.burnerchat.webRTC.model.users.User
import com.example.burnerchat.webRTC.views.messages.MessagesActivity2
import com.example.burnerchat.webRTC.views.users.AddChatActivity
import com.example.burnerchat.webRTC.views.users.UserProfileActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton


class ChatsView : AppCompatActivity() {
    private val viewModel: ChatsViewViewModel by viewModels()

    private val userRepository = BurnerChatApp.appModule.usersRepository

    //Main recyclerView
    private lateinit var rvChats: RecyclerView;

    //ImageView that displays the profile icon
    private lateinit var ivIcon: ImageView;

    //FAB which opens the add chat menu
    private lateinit var fabAdd: FloatingActionButton

    //Chats list
    private var chatsList: List<Chat> = mutableListOf()

    override fun onResume() {
        super.onResume()
        chatsList = viewModel.getChats()
        resetImage()
    }

    fun resetImage(){
        val user = userRepository.getUser()
        val icon = user.getIcon()

        if(icon.isBlank()){
            ivIcon.setImageResource(R.drawable.baseline_person_24)
        }else
            ivIcon.setImageBitmap(ImageUtils.decodeFromBase64(icon))
    }

    /**
     * Initializes all components
     */
    private fun initComponents() {
        rvChats = findViewById(R.id.rcChats);
        ivIcon = findViewById(R.id.ivIcon);
        fabAdd = findViewById(R.id.fabAdd);

        initChatRecycler()
        initFAB()
        initIcon()

        viewModel.chatsList.observe(this) { newChatsList ->
            chatsList = newChatsList
            rvChats.adapter?.notifyDataSetChanged()
        }

        /*
        viewModel.oneTimeEvents.observe(this) {
            Log.d("INVITATION", "Got invite")
            when (it) {
                is MainOneTimeEvents.GotInvite -> {
                    val dialog = Dialog(this)
                    dialog.setContentView(R.layout.incomming_dialog)
                    dialog.window?.setLayout(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                    dialog.setCancelable(false)

                    // TODO: comportamiento de los botones
                    dialog.findViewById<Button>(R.id.btAccept).setOnClickListener {
//                        viewModel.dispatchAction (
//                            MainActions.AcceptIncomingConnection
//                        )
                        viewModel.acceptIncomingConnection()
                        dialog.dismiss()
                    }

                    dialog.findViewById<Button>(R.id.btCancel).setOnClickListener {
                        dialog.dismiss()
                    }

                    dialog.show()
                }
            }
        }*/
    }

    private fun initIcon() {

        resetImage()

        ivIcon.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            var loggedUser = viewModel.loggedUser.value
            intent.putExtra(
                UserProfileActivity.CLAVE_NOMBRE_USUARIO,
                loggedUser?.username.toString()
            )
            Log.d("chat", loggedUser?.username!!)
            intent.putExtra(
                UserProfileActivity.CLAVE_CLAVE_PUBLICA,
                loggedUser.keyPair.publicKey.toString()
            )
            startActivity(intent)
        }
    }

    private fun initFAB() {
        fabAdd.setOnClickListener {
            val intent = Intent(this, AddChatActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initChatRecycler() {
        val customAdapter = ChatsAdapter(
            viewModel.getChats(),
            viewModel.loggedUser.value?.keyPair?.publicKey!!
        ) { chat ->
            val intent = Intent(this, MessagesActivity2::class.java)
            intent.putExtra("target", chat?.getTarget()?.username)
            if (chat != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Cannot open chat", Toast.LENGTH_SHORT).show()
            }
        }

        rvChats.layoutManager = LinearLayoutManager(this)
        rvChats.adapter = customAdapter
    }

    private fun initUser() {
        var loggedUser = User(
            KeyPair("a", "b"),
            intent.getStringExtra(MainActivity.CLAVE_NOMBRE_USUARIO).toString()
        )
        viewModel.logIn(loggedUser)
        Log.d("debug", loggedUser.username)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chats_view)

        initUser()
        viewModel.init()

        initComponents()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}