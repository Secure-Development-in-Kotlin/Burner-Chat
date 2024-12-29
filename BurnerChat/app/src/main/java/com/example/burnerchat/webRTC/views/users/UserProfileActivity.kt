package com.example.burnerchat.webRTC.views.users

import android.content.ContentResolver
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.R
import com.example.burnerchat.webRTC.business.ImageUtils
import java.io.IOException
import java.net.URI

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

        tvName.setText(intent.getStringExtra(CLAVE_NOMBRE_USUARIO))

        val user0 = usersRepository.getUser()
        viewModel.setUser(user0)
        val user = viewModel.user.value!!
        val icon = user.getIcon()

        if(icon.isBlank()){
            ivIcon.setImageResource(R.drawable.default_icon_128)
        }else
            ivIcon.setImageBitmap(ImageUtils.decodeFromBase64(icon))

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