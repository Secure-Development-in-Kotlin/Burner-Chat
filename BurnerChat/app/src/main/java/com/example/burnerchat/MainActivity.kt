package com.example.burnerchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.burnerchat.business.MainActions
import com.example.burnerchat.views.chats.ChatsView

class MainActivity : AppCompatActivity() {

    companion object{
        public const val CLAVE_NOMBRE_USUARIO = "userName"
    }
    //LogIn button
    private lateinit var btLogIn : Button
    private lateinit var etUserName : EditText

    /**
     * Initalizes all components
     */
    private fun initComponents(){
        btLogIn = findViewById(R.id.btLogin)

        etUserName = findViewById(R.id.etMainName)
        btLogIn.setOnClickListener(){
            val userName = etUserName.text.toString()

            if(!(userName.isBlank()||userName.isEmpty())){
                val intent = Intent(applicationContext, ChatsView::class.java)
                intent.putExtra(CLAVE_NOMBRE_USUARIO, userName)
                login(userName)
                startActivity(intent)
            }

        }
    }

    fun login(name:String){
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