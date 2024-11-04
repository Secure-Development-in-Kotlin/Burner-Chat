package com.example.burnerchat.views.users

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.burnerchat.R
import com.example.burnerchat.backend.MainViewModel
import com.example.burnerchat.business.MainActions
import com.example.burnerchat.views.MessagesActivity
import kotlinx.coroutines.launch

class AddChatActivity : AppCompatActivity() {

    private lateinit var etPublicKey: EditText
    private lateinit var ivIcon : ImageView
    private lateinit var btQR : Button
    private lateinit var btGoBack : Button
    private lateinit var btAddChat : Button

    companion object {
        const val OTHER_USER_KEY = "OTHER_USER_KEY"
        const val OTHER_USER_NAME = "OTHER_USER_NAME"
    }

    fun initComponents(){
        etPublicKey = findViewById(R.id.etAddKey)
        ivIcon = findViewById(R.id.ivNewChat)
        btGoBack = findViewById(R.id.btGoBackChat)
        btAddChat = findViewById(R.id.btAddChat)
        btQR = findViewById(R.id.btAddQR)
        loadListeners()
    }

    private fun loadListeners() {

        btGoBack.setOnClickListener{
            finish()
        }

        btAddChat.setOnClickListener {
            val key = etPublicKey.text
            if (key.isNullOrEmpty()) {

                Toast.makeText(this, "Key cannot be empty", Toast.LENGTH_SHORT).show()

            } else {
                // TODO: Se puede comprobar el resutlado de la conexion?
//                lifecycleScope.launch {
//                    viewModel.dispatchAction(
//                        MainActions.ConnectAs(key.toString())
////                        MainActions.ConnectToUser(key.toString())
//                    )
//                }

                val intent = Intent(applicationContext, MessagesActivity::class.java)
                intent.putExtra(OTHER_USER_KEY, key)
                startActivity(intent)

            }

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