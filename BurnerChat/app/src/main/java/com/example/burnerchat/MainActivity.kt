package com.example.burnerchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.burnerchat.firebase.FirebaseAuthView
import com.example.burnerchat.firebase.preferences.AppPreferences
import com.example.burnerchat.views.WebRTCActivity
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {
    //LogIn button
    private lateinit var btWebRTC: Button

    // Firebase button
    private lateinit var btFirebase: Button

    // Preferencias del usuario
    private lateinit var appPreferences: AppPreferences

    /**
     * Initalizes all components
     */
    private fun initComponents() {
        btWebRTC = findViewById(R.id.btWebRTC)

        // Firebase button
        btFirebase = findViewById(R.id.btFirebase)
        btFirebase.setOnClickListener {
            val intent = Intent(applicationContext, FirebaseAuthView::class.java)
            startActivity(intent)
        }

        btWebRTC.setOnClickListener {
            val intent = Intent(applicationContext, WebRTCActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        appPreferences = AppPreferences(this)

        enableEdgeToEdge()

        setContentView(R.layout.activity_main)

        initComponents()

        // Aplica el tema y el idioma guardado
        applyStoredPreferences()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun applyStoredPreferences() {
        // Aplicar el tema y el idioma guardados al inicio
        lifecycleScope.launch {
            appPreferences.preferencesDataClass.collect { preferences ->
                // Aplica el tema
                if (preferences.nightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }

                // Aplica el idioma
                setLocale(preferences.language)
            }
        }
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)
    }
}