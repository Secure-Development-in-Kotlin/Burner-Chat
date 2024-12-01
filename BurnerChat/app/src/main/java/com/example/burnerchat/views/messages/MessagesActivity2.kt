package com.example.burnerchat.views.messages

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.R

class MessagesActivity2 : AppCompatActivity() {
    private val viewModel: MessagesViewModel by viewModels()
    private lateinit var rvMessages: RecyclerView

    // View elements
    private lateinit var tvChatName: TextView
    private lateinit var tvServerState: TextView
    private lateinit var etMessage: EditText
    private lateinit var btSendMessage: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messages2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
        initChatRecycler()

        viewModel.setTarget(intent.getStringExtra("target")!!)

        establishConnection()
    }

    // Método para establecer conexión con el otro usuario
    private fun establishConnection() {
        viewModel.establishConnection()
    }

    private fun initComponents() {
        tvChatName = findViewById(R.id.tvChatName)
        tvChatName.text = intent.getStringExtra("target")
        tvServerState = findViewById(R.id.tvServerState)
        etMessage = findViewById(R.id.etMessage)
        btSendMessage = findViewById(R.id.btSendMessage)
        rvMessages = findViewById(R.id.rvMessages)

        btSendMessage.setOnClickListener {
            viewModel.sendMessage(etMessage.text.toString())
            etMessage.text.clear()
        }
    }


    private fun initChatRecycler() {
        val customAdapter = MessagesAdapter(viewModel.getMessages())

        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = customAdapter
    }
}

