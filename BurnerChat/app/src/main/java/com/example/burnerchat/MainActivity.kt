package com.example.burnerchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.burnerchat.business.MainActions
import com.example.burnerchat.views.chats.ChatsView
import com.example.burnerchat.views.users.AddChatActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    companion object {
        public const val CLAVE_NOMBRE_USUARIO = "userName"
    }

    //LogIn button
    private lateinit var btLogIn: Button
    private lateinit var etUserName: EditText

    /**
     * Initalizes all components
     */
    private fun initComponents() {
        btLogIn = findViewById(R.id.btLogin)

        etUserName = findViewById(R.id.etMainName)
        btLogIn.setOnClickListener {

            val userName = etUserName.text.toString()
            viewModel.setName(userName) // TODO: refactor a un onchange del editText
            if (!(userName.isBlank() || userName.isEmpty())) {

                lifecycleScope.launch(Dispatchers.IO) {

                    val intent = Intent(applicationContext, ChatsView::class.java)
                    intent.putExtra(CLAVE_NOMBRE_USUARIO, userName)
                    login(userName)
                    startActivity(intent)

                }

            } else {
                Toast.makeText(
                    this,
                    "El nombre de usuario no puede estar vacío",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

    // TODO: Verificar que funciona correctamente
    private fun login(name: String) {
        MainActions.ConnectAs(name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        initComponents()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}