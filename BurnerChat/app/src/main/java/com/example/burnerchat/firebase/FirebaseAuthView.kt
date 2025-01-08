package com.example.burnerchat.firebase

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.R
import com.example.burnerchat.firebase.views.chats.ChatsView
import com.example.burnerchat.firebase.views.chats.UserDTO
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class FirebaseAuthView : AppCompatActivity() {
    private val viewModel: FirebaseAuthViewModel by viewModels()

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btLogIn: Button
    private lateinit var btSignUp: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_firebase_auth_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
    }

    private fun initComponents() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btLogIn = findViewById(R.id.btLogIn)
        btSignUp = findViewById(R.id.btSignUp)

        // Set the listeners for the buttons
        btSignUp.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            viewModel.signUp(
                email = email,
                password = password,
                onSuccess = { user ->
                    viewModel.syncUserInDB(user) {
                        showChats()
                    }
                },
                onError = { exception ->
                    showAlert(exception)
                }
            )
        }

        btLogIn.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()

            viewModel.logIn(
                email = email,
                password = password,
                onSuccess = { user ->
                    viewModel.syncUserInDB(user) {
                        showChats()
                    }
                },
                onError = { exception ->
                    showAlert(exception)
                }
            )
        }
    }

    private fun showChats() {
        val intent = Intent(applicationContext, ChatsView::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun showAlert(exception: Exception?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario: ${exception?.message}")
        builder.setPositiveButton("Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}