package com.example.burnerchat.webRTC.views.users

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.R
import com.example.burnerchat.webRTC.business.ImageUtils
import java.io.IOException
import androidx.lifecycle.lifecycleScope
import com.example.burnerchat.R
import com.example.burnerchat.preferences.AppPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Locale

class UserProfileActivity : AppCompatActivity() {
    companion object {
        const val CLAVE_NOMBRE_USUARIO: String = "nombreUsuario"
        const val CLAVE_CLAVE_PUBLICA: String = "clavePublica"
    }

    private val viewModel:UserProfileViewModel by viewModels()

    private val usersRepository = BurnerChatApp.appModule.usersRepository
    private lateinit var tvName: TextView

    private lateinit var ivIcon: ImageView
    private lateinit var btGoBack: Button
    private lateinit var btConfirm: Button

    private lateinit var btnToggleTheme: ImageButton

    // Tema guardado
    private lateinit var appPreferences: AppPreferences

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

    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()){
        val galleryURI = it
        try{
            val bitmap = ImageUtils.loadBitmapFromURI(galleryURI!!, contentResolver)
            viewModel.setIcon(bitmap!!)
        }catch (e:Exception){
            e.printStackTrace()
        }
    }


    fun initComponents() {
        tvName = findViewById(R.id.tvProfileName)
        ivIcon = findViewById(R.id.ivProfileIcon)
        btGoBack = findViewById(R.id.btProfileGoBack)
        btConfirm = findViewById(R.id.btEditConfirm)
        btnToggleTheme = findViewById(R.id.btToggleButton)
        spinnerLanguage = findViewById(R.id.spinnerLanguage)

        tvName.setText(intent.getStringExtra(CLAVE_NOMBRE_USUARIO))

        val user0 = usersRepository.getUser()
        viewModel.setUser(user0)
        val user = viewModel.user.value!!
        val icon = user.getIcon()

        if(icon.isBlank()){
            ivIcon.setImageResource(R.drawable.default_icon_128)
        }else
            ivIcon.setImageBitmap(ImageUtils.decodeFromBase64(icon))


        // Initialize buttons and spinner
        initGoBack()
        initEditIcon()

        viewModel.user.observe(this){
            newUser->
            val icono = newUser.getIcon()
                if(icono.isNotBlank() && icono.isNotEmpty()){
                    val bitmap = ImageUtils.decodeFromBase64(icono)
                    ivIcon.setImageBitmap(bitmap)
                }
        }
        
    
        initAvailableLanguages()
        initThemeToggleButton()

        // Cuando se haga click en confirmar se guardarán las preferencias del usuario
        btConfirm.setOnClickListener {
            // Obtener el idioma seleccionado del Spinner
            val selectedLanguage = getSelectedLanguage()

            // Obtener el estado del tema (claro u oscuro)
            val isNightMode =
                resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES

            Log.d("Tema", "¿El modo oscuro está puesto?: $isNightMode")

            // Guardar el idioma y el tema seleccionados
            lifecycleScope.launch {
                appPreferences.savePreferences(isNightMode, selectedLanguage)
            }

            // Aplicar el idioma y el tema
            setLocale(selectedLanguage)
            applyTheme(isNightMode)

            // Se vuelve atrás
            finish()
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
                    "en" -> 1
                    "fr" -> 2
                    "ru" -> 3
                    else -> 0  // Por defecto, español
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
                // Se guarda cuando se pulse confirmar
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // No hacer nada
            }
        }
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

        // Actualiza los textos de la vista
        updateTextsInView()
    }

    // Método para actualizar los textos de la vista según el idioma seleccionado
    private fun updateTextsInView() {
        etEditName.hint = getString(R.string.textNewName)
        btGoBack.text = getString(R.string.textGoBack)
        btConfirm.text = getString(R.string.textConfirm)
    }

    private fun applyTheme(isNightMode: Boolean) {
        // Aplica el tema
        if (isNightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        // Actualiza el icono después del cambio de tema
        btnToggleTheme.setImageResource(
            if (isNightMode) R.drawable.light_mode else R.drawable.dark_mode
        )
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
            val newThemeMode =
                if (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES) {
                    AppCompatDelegate.MODE_NIGHT_NO
                } else {
                    AppCompatDelegate.MODE_NIGHT_YES
                }

            // Aplica el nuevo tema
            AppCompatDelegate.setDefaultNightMode(newThemeMode)

            // Guarda la preferencia del tema en DataStore
            lifecycleScope.launch {
                appPreferences.savePreferences(
                    newThemeMode == AppCompatDelegate.MODE_NIGHT_YES,
                    getSelectedLanguage()
                )
            }

            // Actualiza el icono después del cambio
            btnToggleTheme.setImageResource(
                if (newThemeMode == AppCompatDelegate.MODE_NIGHT_YES) R.drawable.light_mode else R.drawable.dark_mode
            )

            // Reinicia la actividad para aplicar el cambio de tema correctamente
            recreate()

        }
    }

    private fun initEditIcon(){
        ivIcon.setOnClickListener{
            galleryLauncher.launch("image/*")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode== RESULT_OK && data!= null){
            var selectedImage = data.data

            ivIcon
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
            else -> "es" // Español por defecto
        }
    }
}