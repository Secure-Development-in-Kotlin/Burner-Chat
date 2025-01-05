package com.example.burnerchat.firebase.views.messages

import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Window
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.R
import com.example.burnerchat.firebase.repositories.ImageUtils
import com.example.burnerchat.firebase.views.chats.ChatInfoActivity
import kotlinx.coroutines.launch

class MessagesActivity : AppCompatActivity() {
    private val viewModel: MessagesViewModel by viewModels()
    private lateinit var rvMessages: RecyclerView

    // View elements
    private lateinit var tvChatName: TextView
    private lateinit var tvServerState: TextView
    private lateinit var etMessage: EditText
    private lateinit var btSendMessage: ImageButton
    private lateinit var btSendFoto: ImageButton
    private lateinit var currentImage: Bitmap
    private lateinit var ibGoBackFromChat: ImageButton
    private lateinit var ibChatInfo:ImageButton

    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        val galleryURI = it
        try {
            val bitmap = ImageUtils.loadBitmapFromURI(galleryURI!!, contentResolver)
            currentImage = bitmap!!
            initDialog()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.picture_text_dialog_view)
        val etMessage: EditText = dialog.findViewById(R.id.etSendDialog)
        val btSend: ImageButton = dialog.findViewById(R.id.btSendDialog)
        val ivImage: ImageView = dialog.findViewById(R.id.ivImageDialog)
        val btCancel: ImageButton = dialog.findViewById(R.id.btCancelDialog)
        ivImage.setImageBitmap(currentImage)
        btSend.setOnClickListener {
            val text = etMessage.text.toString()
            viewModel.sendImageMessage(currentImage, text)
            dialog.dismiss()
        }
        btCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_messages)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()

        val context = this
        lifecycleScope.launch {
            viewModel.setChat(intent.getStringExtra("chatId")!!)
            initChatRecycler()
            viewModel.chat.observe(context) { chat ->
                //rvMessages.adapter?.notifyDataSetChanged()
                val adapter = rvMessages.adapter!! as MessagesAdapter
                adapter.updateMessages(chat.messages)
                if (chat.messages.isNotEmpty())
                    rvMessages.scrollToPosition(chat.messages.size - 1)
            }
        }



    }

    private fun initComponents() {
        tvChatName = findViewById(R.id.tvChatName)
        tvChatName.text = intent.getStringExtra("target")
        etMessage = findViewById(R.id.etMessage)
        btSendMessage = findViewById(R.id.btSendMessage)
        rvMessages = findViewById(R.id.rvMessages)
        btSendFoto = findViewById(R.id.btFoto)
        ibChatInfo = findViewById(R.id.ibChatInfo)
        btSendMessage.setOnClickListener {
            val text = etMessage.text.toString()
            if (text.isNotBlank() && text.isNotEmpty()) {
                viewModel.sendMessage(etMessage.text.toString())
                etMessage.text.clear()

            }
        }
        ibGoBackFromChat = findViewById(R.id.ibGoBackFromChats)
        ibGoBackFromChat.setOnClickListener {
            finish()
        }

        ibChatInfo.setOnClickListener{
            val chat = viewModel.chat.value!!
            val intent = Intent(this, ChatInfoActivity::class.java)
            intent.putExtra("chatId", chat?.uid)
            if (chat != null) {
                startActivity(intent)
            }

        }
        initBtFoto()
    }

    private fun initBtFoto() {
        btSendFoto.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
    }

    private fun initChatRecycler() {
        rvMessages.layoutManager = LinearLayoutManager(this)
        rvMessages.adapter = MessagesAdapter(viewModel.chat.value!!.messages)
    }
}

