package com.example.burnerchat.webRTC.views.chats

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.burnerchat.MainActivityViewModel
import com.example.burnerchat.R

class CreateSingleChatView : AppCompatActivity() {
    private val viewModel: CreateSingleChatViewModel by viewModels()

    private lateinit var btGoBack: ImageButton
    private lateinit var etEmail: EditText
    private lateinit var btConfirm: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_single_chat_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
    }

    private fun initComponents() {
        btGoBack = findViewById(R.id.ibGoBack)
        etEmail = findViewById(R.id.etTargetEmail)
        btConfirm = findViewById(R.id.btConfirm)

        btGoBack.setOnClickListener {
            finish()
        }

        btConfirm.setOnClickListener {
            val email = etEmail.text.toString()
            if (email.isNotEmpty()) {
                viewModel.addChat(email)
            }
        }

        viewModel.createdChat.observe(this) {
            if (it) {
                finish()
            }
        }
    }

}