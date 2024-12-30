package com.example.burnerchat.webRTC.views.users

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.webRTC.business.ImageUtils
import androidx.lifecycle.lifecycleScope
import com.example.burnerchat.R
import com.example.burnerchat.preferences.AppPreferences
import com.example.burnerchat.preferences.PreferenciasDataClass
import kotlinx.coroutines.launch
import java.util.Locale

class UserProfileActivity : AppCompatActivity() {
    companion object {
        const val CLAVE_NOMBRE_USUARIO: String = "nombreUsuario"
        const val CLAVE_CLAVE_PUBLICA: String = "clavePublica"
    }

    private val viewModel: UserProfileViewModel by viewModels()

    private val usersRepository = BurnerChatApp.appModule.usersRepository
    private lateinit var tvName: TextView

    private lateinit var ivIcon: ImageView
    private lateinit var btGoBack: ImageButton

    private lateinit var btnToggleTheme: ImageButton

    // Tema guardado
    private lateinit var appPreferences: AppPreferences
    private lateinit var preferencias: PreferenciasDataClass

    // Idioma
    private lateinit var spinnerLanguage: Spinner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_user_profile)

        // Inicializa ThemePreferences antes de usarlo
        appPreferences = AppPreferences(this)

        initComponents()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        val galleryURI = it
        try {
            val bitmap = ImageUtils.loadBitmapFromURI(galleryURI!!, contentResolver)
            viewModel.setIcon(bitmap!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun initComponents() {
        tvName = findViewById(R.id.tvProfileName)
        ivIcon = findViewById(R.id.ivProfileIcon)
        btGoBack = findViewById(R.id.ibGoBackUserProfile)
        btnToggleTheme = findViewById(R.id.btToggleButton)
        spinnerLanguage = findViewById(R.id.spinnerLanguage)

        tvName.setText(intent.getStringExtra(CLAVE_NOMBRE_USUARIO))

        val user0 = usersRepository.getLoggedUser()
        val user = viewModel.user.value!!
        val icon = user.photoUrl

        if (icon == null) {
            ivIcon.setImageResource(R.drawable.default_icon_128)
        } else {
            // Adaptar la imagen al tamaño máximo de 128dp
            ImageUtils.setImageWithRoundedBorder(this, icon.toString(), ivIcon, 46)
        }

        // Initialize buttons and spinner
        initGoBack()
        initEditIcon()

        viewModel.user.observe(this) { newUser ->
            val icono = newUser.photoUrl
            if (icono != null) {
                val bitmap = ImageUtils.decodeFromBase64(icono.toString())
                ivIcon.setImageBitmap(bitmap)
            }
        }

        initAvailableLanguages()
        initThemeToggleButton()
    }


    private fun initAvailableLanguages() {
        // Idiomas disponibles
        val languages = arrayOf("Español", "English", "Français", "Русский")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.adapter = adapter

        // Obtener el idioma guardado y seleccionar la opción correspondiente en el Spinner
        lifecycleScope.launch {
            appPreferences.preferencesDataClass.collect { preferences ->
                val position = when (preferences.language) {
                    "es" -> 0
                    "en" -> 1
                    "fr" -> 2
                    "ru" -> 3
                    else -> 1  // Por defecto, Inglés
                }
                spinnerLanguage.setSelection(position)
            }
        }

        // Configurar el evento de selección
        spinnerLanguage.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Cambiar el idioma inmediatamente
                val selectedLanguage = getSelectedLanguage()
                setLocale(selectedLanguage)
                // Guardar el idioma seleccionado en las preferencias
                lifecycleScope.launch {
                    appPreferences.savePreferences(
                        isNightMode(),
                        selectedLanguage
                    )
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }
    }

    // Función para saber si el modo oscuro está activado
    private fun isNightMode(): Boolean {
        return resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    private fun setLocale(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)
        val config = Configuration()
        // Esto está deprecado
//        config.locale = locale
//        resources.updateConfiguration(config, resources.displayMetrics)
        config.setLocale(locale)
        baseContext.createConfigurationContext(config)
    }

    private fun initThemeToggleButton() {
        // Configurar el botón de alternar tema
        lifecycleScope.launch {
            appPreferences.preferencesDataClass.collect { preferences ->
                // Configura el icono del botón según el tema
                btnToggleTheme.setImageResource(
                    if (preferences.nightMode) R.drawable.light_mode else R.drawable.dark_mode
                )
            }
        }
        // Cambiar el tema cuando el usuario haga clic en el botón
        btnToggleTheme.setOnClickListener {
            // Determina el nuevo modo de tema
            val newThemeMode =
                if (isNightMode()) {
                    AppCompatDelegate.MODE_NIGHT_NO
                } else {
                    AppCompatDelegate.MODE_NIGHT_YES
                }

            // Aplica el nuevo tema inmediatamente
            AppCompatDelegate.setDefaultNightMode(newThemeMode)

            // Guarda las preferencias del tema en DataStore
            lifecycleScope.launch {
                appPreferences.savePreferences(
                    newThemeMode == AppCompatDelegate.MODE_NIGHT_YES,
                    getSelectedLanguage()
                )
            }

            // Actualiza el icono del botón después de cambiar el tema
            btnToggleTheme.setImageResource(
                if (newThemeMode == AppCompatDelegate.MODE_NIGHT_YES)
                    R.drawable.light_mode
                else
                    R.drawable.dark_mode
            )

            // Evitar llamar a recreate() inmediatamente
            // Si realmente necesitas reiniciar la actividad, hazlo con una transición controlada
            // recreate()
        }
    }

    private fun initEditIcon() {
        ivIcon.setOnClickListener {
            galleryLauncher.launch("image/*")
        }
    }

    private fun initGoBack() {
        btGoBack.setOnClickListener {
            finish()
        }
    }

    private fun getSelectedLanguage(): String {
        return when (spinnerLanguage.selectedItemPosition) {
            0 -> "es" // Español
            1 -> "en" // Inglés
            2 -> "fr" // Francés
            3 -> "ru" // Ruso
            else -> "en" // Ingés por defecto
        }
    }
}