package com.example.burnerchat.views

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.burnerchat.business.MainRepository
import com.example.burnerchat.databinding.ActivityLoginBinding
import com.example.burnerchat.databinding.ActivityMainBinding
import org.webrtc.DataChannel
import javax.inject.Inject

// ESTA CLASE HA SIDO MODIFICADA PARA SER TOMADA COMO EJEMPLO PARA UTILIZAR WEBRTC CON LA CAPA DE NEGOCIO
// TODO: Cambiar esta clase por la que correspondería
class MainActivity : AppCompatActivity(), MainRepository.Listener {

    private var username : String?=null

    @Inject lateinit var mainRepository: MainRepository // Here we initialize the business layout

    private lateinit var views: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
         */
        views = LoginActivity.inflate(layoutInflater)
        setContentView(views.root)
        init()
    }

    private fun init(){
        username = intent.getStringExtra("username")
        if(username.isNullOrEmpty())
            finish()

        mainRepository.listener = this
        mainRepository.init(username!!)

        // Handle UI stuff
        views.apply {
            // Request connection button
            requestBtn.setOnClickListener{
                if(targetEt.text.toString().isEmpty()){
                    Toast.makeText(this@MainActivity, "Please, Fill up the target", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Here we send the petition to the target
                mainRepository.sendStartConnection(targetEt.text.toString())
            }
            // Sending Image button
            sendImageButton.setOnClickListener{
                //TODO: ME QUEDÉ POR AQUÍ <---------------------------------------------------3
            }
        }
    }

    override fun onConnectionRequestReceived(target: String) {
        TODO("Not yet implemented")
    }

    override fun onDataChannelReceived() {
        TODO("Not yet implemented")
    }

    override fun onDatareceivedFromChannel(it: DataChannel.Buffer) {
        TODO("Not yet implemented")
    }
}