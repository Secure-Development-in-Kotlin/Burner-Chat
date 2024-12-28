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
import androidx.lifecycle.lifecycleScope
import com.example.burnerchat.R
import com.example.burnerchat.preferences.ThemePreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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

    // Tema guardado
    private lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        // Inicializa ThemePreferences antes de usarlo
        themePreferences = ThemePreferences(this)

        initComponents()

        // Aplica el tema al inicio de la actividad
        lifecycleScope.launch {
            themePreferences.isNightMode.collect { isNightMode ->
                if (isNightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }

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
        // Recolectamos el valor de night_mode y aplicamos el tema
        lifecycleScope.launch{
            themePreferences.isNightMode.collect { isNightMode ->
                // Aplica el modo de tema al inicio
                if (isNightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                // Configura el icono del botón según el tema
                btnToggleTheme.setImageResource(
                    if (isNightMode) R.drawable.light_mode else R.drawable.dark_mode
                )
            }
        }

        // Cambia el tema cuando el usuario haga clic en el botón
        btnToggleTheme.setOnClickListener {
            val newThemeMode = if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                AppCompatDelegate.MODE_NIGHT_NO
            } else {
                AppCompatDelegate.MODE_NIGHT_YES
            }

            // Aplica el nuevo tema
            AppCompatDelegate.setDefaultNightMode(newThemeMode)

            // Guarda la preferencia del tema en DataStore
            lifecycleScope.launch {
                themePreferences.saveNightMode(newThemeMode == AppCompatDelegate.MODE_NIGHT_YES)
            }

            // Actualiza el icono después del cambio
            btnToggleTheme.setImageResource(
                if (newThemeMode == AppCompatDelegate.MODE_NIGHT_YES) R.drawable.light_mode else R.drawable.dark_mode
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