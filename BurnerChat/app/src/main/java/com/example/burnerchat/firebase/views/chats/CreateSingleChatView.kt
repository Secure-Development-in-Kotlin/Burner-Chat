package com.example.burnerchat.firebase.views.chats

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.burnerchat.R
import com.example.burnerchat.firebase.repositories.ImageUtils

class CreateSingleChatView : AppCompatActivity() {
    private val viewModel: CreateSingleChatViewModel by viewModels()

    private lateinit var btGoBack: ImageButton
    private lateinit var etEmail: EditText
    private lateinit var btConfirm: Button
    private lateinit var etName: EditText
    private lateinit var ivIcon:ImageView

    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        val galleryURI = it
        try {
            val bitmap = ImageUtils.loadBitmapFromURI(galleryURI!!, contentResolver)
            viewModel.setIcon(bitmap!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }



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
        etName = findViewById(R.id.etName)

        ivIcon = findViewById(R.id.ivSingleChatCreation)
        btGoBack.setOnClickListener {
            finish()
        }

        btConfirm.setOnClickListener {
            val email = etEmail.text.toString()
            val name = etName.text.toString()
            if (email.isNotEmpty() && name.isNotEmpty()) {
                viewModel.addChat(email, name)
            }
        }

        viewModel.createdChat.observe(this) {
            if (it) {
                finish()
            }
        }

        initImageButton()
    }

    private fun initImageButton() {
        ivIcon.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        viewModel.icon.observe(this) { icon ->
            val icono = icon
            if (icono != null && icono.isNotBlank()) {
                val bitmap = ImageUtils.decodeFromBase64(icono.toString())
                ivIcon.setImageBitmap(bitmap)
            }else{
                ivIcon.setImageResource(R.drawable.default_icon_128)
            }
        }
    }

}