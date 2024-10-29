package com.example.burnerchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.burnerchat.views.chats.ChatsView

class MainActivity : AppCompatActivity() {

    //LogIn button
    private lateinit var btLogIn : Button

    /**
     * Initalizes all components
     */
    private fun initComponents(){
        btLogIn = findViewById(R.id.btLogin)
        val intent = Intent(applicationContext, ChatsView::class.java)
        btLogIn.setOnClickListener(){

            startActivity(intent)
        }
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