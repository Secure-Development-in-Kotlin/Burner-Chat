package com.example.burnerchat.views.users

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.burnerchat.R
import java.security.PublicKey

class UserProfileActivity : AppCompatActivity() {
    companion object{
        const val CLAVE_NOMBRE_USUARIO :String = "nombreUsuario"
        const val CLAVE_CLAVE_PUBLICA : String = "clavePublica"
    }

    private lateinit var tvName : TextView
    private lateinit var etPublicKey: EditText
    private lateinit var etEditName: EditText
    private lateinit var btEditIcon: Button
    private lateinit var ivIcon : ImageView
    private lateinit var btGoBack : Button
    private lateinit var btConfirm : Button


    fun initComponents(){
        tvName = findViewById(R.id.tvProfileName)
        etPublicKey = findViewById(R.id.etPublicKey)
        etEditName = findViewById(R.id.etEditName)
        btEditIcon = findViewById(R.id.btEditIcon)
        ivIcon = findViewById(R.id.ivProfileIcon)
        btGoBack = findViewById(R.id.btProfileGoBack)
        btConfirm = findViewById(R.id.btEditConfirm)

        tvName.setText(intent.getStringExtra(CLAVE_NOMBRE_USUARIO))
        etEditName.setText(intent.getStringExtra(CLAVE_NOMBRE_USUARIO))
        etPublicKey.setText(intent.getStringExtra(CLAVE_CLAVE_PUBLICA))

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
        setContentView(R.layout.activity_user_profile)

        initComponents()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}