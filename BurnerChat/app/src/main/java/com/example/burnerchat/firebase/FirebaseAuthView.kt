package com.example.burnerchat.firebase

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.burnerchat.R
import com.google.firebase.auth.FirebaseAuth

class FirebaseAuthView : AppCompatActivity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btLogIn: Button
    private lateinit var btSignUp: Button

    companion object {
        const val EMAIL_KEY : String = "email"
        const val PROVIDER_KEY : String = "provider"
    }

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
            if (email.isNotBlank() && password.isNotBlank()) {
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showChats(email, ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }

        btLogIn.setOnClickListener {
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email.isNotBlank() && password.isNotBlank()) {
                FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {
                        showChats(email, ProviderType.BASIC)
                    } else {
                        showAlert()
                    }
                }
            }
        }
    }

    private fun showChats(email: String, provider: ProviderType) {
        val intent = Intent(applicationContext, ChatsFirebaseView::class.java)
        intent.putExtra(EMAIL_KEY, email)
        intent.putExtra(PROVIDER_KEY, provider.toString())
        startActivity(intent)
        setResult(RESULT_OK, intent)
        finish()
    }

    private fun showAlert() {
        val builder= AlertDialog.Builder(this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error autenticando al usuario")
        builder.setPositiveButton("Aceptar",null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }

    private fun verifyCurrentUser() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null)
            showChats(currentUser.email.toString(), ProviderType.BASIC)
    }
}