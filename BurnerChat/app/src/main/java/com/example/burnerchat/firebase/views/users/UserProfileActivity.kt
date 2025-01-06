package com.example.burnerchat.firebase.views.users

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.MainActivity
import com.example.burnerchat.R
import com.example.burnerchat.firebase.preferences.AppPreferences
import com.example.burnerchat.firebase.repositories.ImageUtils
import kotlinx.coroutines.flow.first
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

    // Tema guardado
    private lateinit var appPreferences: AppPreferences
    private lateinit var btnToggleTheme: ImageButton

    // Idioma
    private lateinit var spinnerLanguage: Spinner
    private lateinit var ivLanguage: ImageView

    // Panic button
    private lateinit var panicButton: ImageButton

    // Log out
    private lateinit var btLogOut: Button

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
        ivLanguage = findViewById(R.id.ivLanguage)
        panicButton = findViewById(R.id.ibPanicButton)
        btLogOut = findViewById(R.id.btLogOut)

        tvName.text = intent.getStringExtra(CLAVE_NOMBRE_USUARIO)

        val user = usersRepository.getLoggedUser()!!
        val icon = user.photoUrl

        val context = this
        lifecycleScope.launch {
            viewModel.fetchUser()
            if (icon == null) {
                ivIcon.setImageResource(R.drawable.default_icon_128)
            } else {
                // Adaptar la imagen al tamaño máximo de 128dp
                ImageUtils.setImageWithRoundedBorder(context, icon.toString(), ivIcon, 46)
            }

            viewModel.user.observe(context) { newUser ->
                val icono = newUser.icon
                if (!icono.isNullOrBlank()) {
                    val bitmap = ImageUtils.decodeFromBase64(icono)
                    ivIcon.setImageBitmap(bitmap)
                } else {
                    ivIcon.setImageResource(R.drawable.default_icon_128)
                }
            }
        }


        // Initialize buttons and spinner
        initGoBack()
        initEditIcon()


        // Botón de idioma
        initAvailableLanguages()

        // Botón de tema
        initThemeToggleButton()

        // Panic button
        panicButton.setOnClickListener {
            // Crear el diálogo
            AlertDialog.Builder(this)
                .setTitle(R.string.panic_mode)
                .setMessage(R.string.panic_message)
                .setPositiveButton(R.string.yes) { dialog, _ ->
                    viewModel.sendPanic()
                    dialog.dismiss() // Cierra el diálogo
                    // Se envía al usuario a la pantalla de inicio
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                }
                .setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.dismiss() // Solo cierra el diálogo
                }
                .show()
        }

        // Log out
        btLogOut.setOnClickListener {
            // Crear el diálogo
            AlertDialog.Builder(this)
                .setTitle(R.string.log_out)
                .setMessage(R.string.log_out_confirmation)
                .setPositiveButton(R.string.yes) { dialog, _ ->
                    val logoutStatus = viewModel.logOut()
                    dialog.dismiss() // Cierra el diálogo

                    if (logoutStatus) {
                        // Se envía al usuario a la pantalla de inicio
                        val intent = Intent(this, MainActivity::class.java)
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                        startActivity(intent)
                        finish()
                    } else {
                        // Mostrar un mensaje de error
                        AlertDialog.Builder(this)
                            .setTitle(R.string.textError)
                            .setMessage(R.string.textLogoutError)
                            .setPositiveButton(R.string.textAccept, null)
                            .show()
                    }
                }
                .setNegativeButton(R.string.no) { dialog, _ ->
                    dialog.dismiss() // Solo cierra el diálogo
                }
                .show()
        }
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

    // Función para actualizar el icono del botón de tema
    private fun updateThemeButtonIcon(isNightMode: Boolean) {
        btnToggleTheme.setImageResource(
            if (isNightMode) R.drawable.light_mode else R.drawable.dark_mode
        )
    }

    private fun initThemeToggleButton() {
        // Observar preferencias y configurar tema inicial
        lifecycleScope.launch {
            appPreferences.preferencesDataClass.collect { preferences ->
                // Configurar el modo nocturno inicial
                AppCompatDelegate.setDefaultNightMode(
                    if (preferences.nightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )
                // Actualizar el icono del botón
                updateThemeButtonIcon(preferences.nightMode)
            }
        }

        // Configurar el clic del botón para alternar el tema
        btnToggleTheme.setOnClickListener {
            // Evitar múltiples cambios rápidos
            btnToggleTheme.isEnabled = false

            lifecycleScope.launch {
                // Leer las preferencias actuales
                val preferences = appPreferences.preferencesDataClass.first()
                val newNightMode = !preferences.nightMode

                // Actualizar el tema
                AppCompatDelegate.setDefaultNightMode(
                    if (newNightMode) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO
                )

                // Guardar el nuevo estado del tema en las preferencias
                appPreferences.savePreferences(newNightMode, getSelectedLanguage())

                // Actualizar el icono del botón
                updateThemeButtonIcon(newNightMode)

                // Rehabilitar el botón después de un breve retraso
                btnToggleTheme.postDelayed({ btnToggleTheme.isEnabled = true }, 500)
            }
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