package com.example.burnerchat.views

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.burnerchat.business.MainRepository
import com.example.burnerchat.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.DataChannel
import javax.inject.Inject

// ESTA CLASE HA SIDO MODIFICADA PARA SER TOMADA COMO EJEMPLO PARA UTILIZAR WEBRTC CON LA CAPA DE NEGOCIO
// TODO: Cambiar esta clase por la que correspondería
@AndroidEntryPoint // Important for using Hilt
class MainActivity : AppCompatActivity(), MainRepository.Listener {

    private var username : String?=null

    @Inject
    lateinit var mainRepository: MainRepository // Here we initialize the business layout

    private lateinit var views: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityMainBinding.inflate(layoutInflater)
        setContentView(views.root)
        init()
    }

    private fun init(){
        username = intent.getStringExtra("username")
        if(username.isNullOrEmpty()) {
            finish()
        }

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
            // Sending Text button
            sendTextButton.setOnClickListener{
                TODO("Not yet implemented")
            }

            // Sending Image (open the gallery)
            sendingImageView.setOnClickListener{
                TODO("Not yet implemented")
            }
        }
    }

    override fun onConnectionRequestReceived(target: String) {
        runOnUiThread{
            views.apply {
                requestLayout.isVisible = false
                notificationLayout.isVisible = true
                // If the connection is accepted then the comunication between two peers start
                notificationAcceptBtn.setOnClickListener{
                    mainRepository.startCall(target)
                    notificationLayout.isVisible = false
                }
                // If the connection is declined the pending requests view is visible again
                notificationDeclineBtn.setOnClickListener{
                    notificationLayout.isVisible = false
                    requestLayout.isVisible = true
                }
            }
        }
    }

    // Once the connection is stablished, the layouts to receive and send data become visible
    override fun onDataChannelReceived() {
        runOnUiThread{
            views.apply {
                requestLayout.isVisible = false
                receivedDataLayout.isVisible = true
                sendDataLayout.isVisible = true
            }
        }
    }

    override fun onDatareceivedFromChannel(it: DataChannel.Buffer) {
        TODO("Not yet implemented")
    }
}