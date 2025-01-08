package com.example.burnerchat.firebase.views.users

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.burnerchat.R
import com.example.burnerchat.firebase.views.chats.CreateGroupChatView
import com.example.burnerchat.firebase.views.chats.CreateSingleChatView

class AddChatActivity : AppCompatActivity() {
    private lateinit var btSingleChat: Button
    private lateinit var btGroupChat: Button
    private lateinit var ivIcon: ImageView
    private lateinit var btQR: Button
    private lateinit var btGoBack: ImageButton

    private fun initComponents() {
        btSingleChat = findViewById(R.id.btSingleChat)
        btGroupChat = findViewById(R.id.btGroupChat)
        ivIcon = findViewById(R.id.ivNewChat)
        btGoBack = findViewById(R.id.ibGoBackFromAddChat)
        btQR = findViewById(R.id.btAddQR)

        btGoBack.setOnClickListener {
            finish()
        }

        btSingleChat.setOnClickListener {
            val intent = Intent(this, CreateSingleChatView::class.java)
            startActivity(intent)
        }

        btGroupChat.setOnClickListener {
            val intent = Intent(this, CreateGroupChatView::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_chat)

        initComponents()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }


}