package com.example.burnerchat

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.burnerchat.firebase.FirebaseAuthView
import com.example.burnerchat.preferences.AppPreferences
import com.example.burnerchat.webRTC.views.chats.ChatsView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    companion object {
        const val CLAVE_NOMBRE_USUARIO = "userName"
    }

    //LogIn button
    private lateinit var btWebRTC: Button
    private lateinit var etUserName: EditText

    // Firebase button
    private lateinit var btFirebase: Button

    // Preferencias del usuario
    private lateinit var appPreferences: AppPreferences

//    init {
//        BurnerChatApp.appModule.protocolHandler.setScope(lifecycleScope)
//    }

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

            // TODO: this should not be necessary
//            val userName = etUserName.text.toString()
//            viewModel.setName(userName) // TODO: refactor a un onchange del editText
//            if (!(userName.isBlank() || userName.isEmpty())) {

                lifecycleScope.launch(Dispatchers.IO) {

                    val intent = Intent(applicationContext, ChatsView::class.java)
//                    intent.putExtra(CLAVE_NOMBRE_USUARIO, userName)
//                    login(userName)
                    startActivity(intent)

                }

//            } else {
//                Toast.makeText(
//                    this,
//                    "El nombre de usuario no puede estar vacío",
//                    Toast.LENGTH_SHORT
//                ).show()
//            }

        }
    }

    private fun login(name: String) {
        viewModel.login(name)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Forzar el modo oscuro al inicio de la aplicación -> no funciona, hace crashear la app
        //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        // Función para aplicar el tema guardado en DataStore
        // Inicializar AppPreferences
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