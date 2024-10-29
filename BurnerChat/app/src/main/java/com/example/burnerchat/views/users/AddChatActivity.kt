package com.example.burnerchat.views.users

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.burnerchat.R
import com.example.burnerchat.views.users.UserProfileActivity.Companion.CLAVE_CLAVE_PUBLICA
import com.example.burnerchat.views.users.UserProfileActivity.Companion.CLAVE_NOMBRE_USUARIO

class AddChatActivity : AppCompatActivity() {

    private lateinit var etPublicKey: EditText
    private lateinit var ivIcon : ImageView
    private lateinit var btQR : Button
    private lateinit var btGoBack : Button
    private lateinit var btConfirm : Button

    fun initComponents(){
        etPublicKey = findViewById(R.id.etAddKey)
        ivIcon = findViewById(R.id.ivNewChat)
        btGoBack = findViewById(R.id.btGoBackChat)
        btConfirm = findViewById(R.id.btAddChat)
        btQR = findViewById(R.id.btAddQR)
        initGoBack()
    }

    private fun initGoBack() {
        btGoBack.setOnClickListener{
            finish()
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