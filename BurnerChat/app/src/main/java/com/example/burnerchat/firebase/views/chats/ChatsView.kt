package com.example.burnerchat.firebase.views.chats

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.R
import com.example.burnerchat.firebase.FirebaseAuthView
import com.example.burnerchat.firebase.model.chats.Chat
import com.example.burnerchat.firebase.repositories.ImageUtils
import com.example.burnerchat.firebase.views.messages.MessagesActivity
import com.example.burnerchat.firebase.views.users.AddChatActivity
import com.example.burnerchat.firebase.views.users.UserProfileActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch


class ChatsView : AppCompatActivity() {
    private val viewModel: ChatsViewViewModel by viewModels()

    private val userRepository = BurnerChatApp.appModule.usersRepository

    //Main recyclerView
    private lateinit var rvChats: RecyclerView

    //ImageView that displays the profile icon
    private lateinit var ivIcon: ImageView

    //FAB which opens the add chat menu
    private lateinit var fabAdd: FloatingActionButton

    // Para la correcta actualización del idioma
    companion object {
        private const val REQUEST_CODE_PROFILE = 1001
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.getChats()
            resetImage()
        }

    }

    private fun resetImage() {
        val context = this
        lifecycleScope.launch {
            val user = viewModel.fetchUser()
            if (user == null) {
                val intent = Intent(applicationContext, FirebaseAuthView::class.java)
                startActivity(intent)
            } else {
                val icon = user.icon

                if (icon == "null" || TextUtils.isEmpty(icon)) {
                    ivIcon.setImageResource(R.drawable.baseline_person_24)
                } else {
                    // Adaptar la imagen al tamaño máximo de 46dp
                    ImageUtils.setImageWithRoundedBorder(context, icon, ivIcon, 128)
                }
            }
        }
    }

    /**
     * Initializes all components
     */
    private fun initComponents() {
        rvChats = findViewById(R.id.rcChats)
        ivIcon = findViewById(R.id.ivIcon)
        fabAdd = findViewById(R.id.fabAdd)

        initChatRecycler()
        initFAB()
        initIcon()
    }

    private fun initIcon() {

        resetImage()

        ivIcon.setOnClickListener {
            val intent = Intent(this, UserProfileActivity::class.java)
            val loggedUser = BurnerChatApp.appModule.usersRepository.getLoggedUser()
            intent.putExtra(
                UserProfileActivity.CLAVE_NOMBRE_USUARIO,
                loggedUser?.email.toString()
            )
            Log.d("chat", loggedUser?.email!!)
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
        val function = { chat: Chat? ->
            val intent = Intent(this, MessagesActivity::class.java)
            intent.putExtra("chatId", chat?.uid)
            if (chat != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "Cannot open chat", Toast.LENGTH_SHORT).show()
            }
        }

        val context = this

        lifecycleScope.launch {
            viewModel.getChats()

            val customAdapter = ChatsAdapter(
                viewModel.chatsList.value!!,
                BurnerChatApp.appModule.usersRepository.getLoggedUser()?.uid!!
            ) { chat ->
                function(chat)
            }

            rvChats.layoutManager = LinearLayoutManager(context)
            rvChats.adapter = customAdapter

            viewModel.chatsList.observe(context) { chatsList ->
                //rvChats.adapter?.notifyDataSetChanged()
                val adapter = rvChats.adapter!! as ChatsAdapter
                adapter.updateChatsList(chatsList)
            }
        }
    }

    private fun initUser() {
        val loggedUser = userRepository.getLoggedUser()
        if (loggedUser == null) {
            val intent = Intent(applicationContext, FirebaseAuthView::class.java)
            startActivity(intent)
        } else {
            Log.d("debug", loggedUser.email.toString())
        }

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