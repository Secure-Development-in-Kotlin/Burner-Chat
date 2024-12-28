package com.example.burnerchat.webRTC.views.messages

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.R
import com.example.burnerchat.webRTC.business.ImageUtils

class MessagesActivity2 : AppCompatActivity() {
    private val viewModel: MessagesViewModel by viewModels()
    private lateinit var rvMessages: RecyclerView

    // View elements
    private lateinit var tvChatName: TextView
    private lateinit var tvServerState: TextView
    private lateinit var etMessage: EditText
    private lateinit var btSendMessage: Button
    private lateinit var btSendFoto: Button

    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        val galleryURI = it
        try{
            val bitmap = ImageUtils.loadBitmapFromURI(galleryURI!!, contentResolver)
            viewModel.sendImageMessage(bitmap!!)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messages2)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel.setTarget(intent.getStringExtra("target")!!)

        initComponents()
        initChatRecycler()

        viewModel.messages.observe(this){
            newList->
                rvMessages.adapter?.notifyDataSetChanged()
                rvMessages.scrollToPosition(newList.size - 1)
        }

        establishConnection()
    }

    // Método para establecer conexión con el otro usuario
    private fun establishConnection() {
        viewModel.establishConnection()
    }

    private fun initComponents() {
        tvChatName = findViewById(R.id.tvChatName)
        tvChatName.text = intent.getStringExtra("target")
        etMessage = findViewById(R.id.etMessage)
        btSendMessage = findViewById(R.id.btSendMessage)
        rvMessages = findViewById(R.id.rvMessages)
        btSendFoto = findViewById(R.id.btFoto)
        btSendMessage.setOnClickListener {
            val text = etMessage.text.toString()
            if(text.isNotBlank() && text.isNotEmpty()){
                viewModel.sendMessage(etMessage.text.toString())
                etMessage.text.clear()

            }
        }
        initBtFoto()
    }

    private fun initBtFoto(){
        btSendFoto.setOnClickListener{
            galleryLauncher.launch("image/*")
        }
    }

    private fun initChatRecycler() {
        val messages = viewModel.getMessages()
        val customAdapter = MessagesAdapter(messages)

        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = customAdapter
    }
}

