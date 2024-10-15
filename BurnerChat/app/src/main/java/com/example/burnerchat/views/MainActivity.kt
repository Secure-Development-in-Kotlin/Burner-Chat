package com.example.burnerchat.views

import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.burnerchat.R
import com.example.burnerchat.business.MainRepository
import com.example.burnerchat.databinding.ActivityMainBinding
import com.example.burnerchat.webrtc.utils.DataConverter
import com.example.burnerchat.webrtc.utils.getFilePath
import dagger.hilt.android.AndroidEntryPoint
import org.webrtc.DataChannel
import java.nio.charset.Charset
import javax.inject.Inject

// ESTA CLASE HA SIDO MODIFICADA PARA SER TOMADA COMO EJEMPLO PARA UTILIZAR WEBRTC CON LA CAPA DE NEGOCIO
// TODO: Cambiar esta clase por la que correspondería
@AndroidEntryPoint
class MainActivity : AppCompatActivity(), MainRepository.Listener {

    private var username: String? = null
    private var imagePathToSend:String? = null

    @Inject
    lateinit var mainRepository: MainRepository // Here we initialize the business layout

    private lateinit var views: ActivityMainBinding

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if(isGranted){
                //open gallery
                pickImageLauncher.launch("image/*")
            }else{
                Toast.makeText(this, "we need storage permission", Toast.LENGTH_SHORT).show()
            }

        }

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.GetContent()){uri ->
            //convert the uri to real path
            uri?.let {
                val imagePath : String? = it.getFilePath(this)
                if (imagePath!=null){
                    this.imagePathToSend = imagePath
                    Glide.with(this).load(imagePath).into(views.sendingImageView)
                } else {
                    Toast.makeText(this, "image was not found", Toast.LENGTH_SHORT).show()
                }
            }

        }

    private fun openGallery(){
        val permission = android.Manifest.permission.READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                this,permission
            ) != PackageManager.PERMISSION_GRANTED ) {
            requestPermissionLauncher.launch(permission)
        } else {
            pickImageLauncher.launch("image/*")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        views = ActivityMainBinding.inflate(layoutInflater)
        setContentView(views.root)
        init()
    }

    private fun init() {
        username = intent.getStringExtra("username")
        if (username.isNullOrEmpty()) {
            finish()
        }

        mainRepository.listener = this
        mainRepository.init(username!!)

        // Handle UI stuff
        views.apply {
            // Request connection button
            requestBtn.setOnClickListener{
                if(targetEt.text.toString().isEmpty()){
                    Toast.makeText(this@MainActivity, "Please, fill up the target", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Here we send the petition to the target
                mainRepository.sendStartConnection(targetEt.text.toString())
            }
            // Sending Image button
            sendImageButton.setOnClickListener {
                if (imagePathToSend.isNullOrEmpty()){
                    Toast.makeText(this@MainActivity, "select image first", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                mainRepository.sendImageToChannel(imagePathToSend!!)
                imagePathToSend = null
                sendingImageView.setImageResource(R.drawable.ic_add_photo)

            }
            // Sending Text button
            sendTextButton.setOnClickListener {
                if(sendingTextEditText.text.isEmpty()){
                    Toast.makeText(this@MainActivity, "fill send text editText", Toast.LENGTH_SHORT)
                        .show()
                    return@setOnClickListener
                }
                // If not empty, we send the text inputted by the current user to the target
                mainRepository.sendTextToDataChannel(sendingTextEditText.text.toString())
                sendingTextEditText.setText("")
            }
            // Sending Image (open the gallery)
            sendingImageView.setOnClickListener {
                openGallery()
            }
        }

    }

    override fun onConnectionRequestReceived(target: String) {
        runOnUiThread{
            views.apply {
                requestLayout.isVisible = false
                notificationLayout.isVisible = true
                Log.d("MainActivity", "Notification layout visible for target: $target")
                // If the connection is accepted then the comunication between two peers start
                notificationAcceptBtn.setOnClickListener {
                    Log.d("MainActivity", "Connection accepted with target: $target")
                    mainRepository.startCall(target)
                    notificationLayout.isVisible = false
                }
                // If the connection is declined the pending requests view is visible again
                notificationDeclineBtn.setOnClickListener {
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
                Log.d("MainActivity", "Llega a canal recibido")
                requestLayout.isVisible = false
                receivedDataLayout.isVisible = true
                sendDataLayout.isVisible = true
            }
        }
    }

    // Convert the data back from the MainRepository to simple text
    override fun onDataReceivedFromChannel(it: DataChannel.Buffer) {
        runOnUiThread {
            val model = DataConverter.convertToModel(it)
            model?.let {
                when(it.first){
                    "TEXT"->{
                        views.receivedText.text = it.second
                            .toString()
                    }
                    "IMAGE"->{
                        Glide.with(this).load(it.second as Bitmap).into(
                            views.receivedImageView
                        )
                    }
                    // add more formats here
                }
            }
        }
    }
}