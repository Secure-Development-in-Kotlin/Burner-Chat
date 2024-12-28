package com.example.burnerchat.webRTC.views.users

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.burnerchat.R

class UserProfileActivity : AppCompatActivity() {
    companion object {
        const val CLAVE_NOMBRE_USUARIO: String = "nombreUsuario"
        const val CLAVE_CLAVE_PUBLICA: String = "clavePublica"
    }

    private lateinit var tvName: TextView
    private lateinit var etEditName: EditText
    private lateinit var btEditIcon: Button
    private lateinit var ivIcon: ImageView
    private lateinit var btGoBack: Button
    private lateinit var btConfirm: Button

    private lateinit var btnToggleTheme: ImageButton

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

    fun initComponents() {
        tvName = findViewById(R.id.tvProfileName)
        etEditName = findViewById(R.id.etEditName)
        ivIcon = findViewById(R.id.ivProfileIcon)
        btGoBack = findViewById(R.id.btProfileGoBack)
        btConfirm = findViewById(R.id.btEditConfirm)
        btnToggleTheme = findViewById(R.id.btToggleButton)

        tvName.setText(intent.getStringExtra(CLAVE_NOMBRE_USUARIO))
        etEditName.setText(intent.getStringExtra(CLAVE_NOMBRE_USUARIO))

        initGoBack()
        initThemeToggleButton()
    }

    private fun initThemeToggleButton() {
        // Configura el icono inicial basado en el tema actual
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
//        var isNightMode = currentNightMode == Configuration.UI_MODE_NIGHT_YES
        btnToggleTheme.setImageResource(
            if (currentNightMode == Configuration.UI_MODE_NIGHT_YES)
            // Si está en modo oscuro, muestra el icono de modo claro
                R.drawable.light_mode
            else
            // Si está en modo claro, muestra el icono de modo oscuro
                R.drawable.dark_mode
        )

        btnToggleTheme.setOnClickListener {
            // Cambia entre modo claro y oscuro
            val newThemeMode =
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES) {
                    // Si está en modo oscuro, cambia a modo claro
                    AppCompatDelegate.MODE_NIGHT_NO
                } else {
                    // Si está en modo claro, cambia a modo oscuro
                    AppCompatDelegate.MODE_NIGHT_YES
                }

            Log.d("Tema", "El nuevo modo es oscuro? $newThemeMode")

            // Aplica el nuevo modo de tema
            AppCompatDelegate.setDefaultNightMode(newThemeMode)

            // Actualiza el icono después del cambio
            btnToggleTheme.setImageResource(
                if (currentNightMode == Configuration.UI_MODE_NIGHT_YES)
                // Si está en modo oscuro, muestra el icono de modo claro
                    R.drawable.light_mode
                else
                // Si está en modo claro, muestra el icono de modo oscuro
                    R.drawable.dark_mode
            )

            // Reinicia la actividad para aplicar el cambio de tema correctamente
            recreate()
        }
    }

    private fun initGoBack() {
        btGoBack.setOnClickListener {
            finish()
        }
    }
}