package com.example.burnerchat.firebase.views.chats

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.R
import com.example.burnerchat.firebase.repositories.ImageUtils
import com.example.burnerchat.firebase.model.chats.Chat
import kotlinx.coroutines.launch

class ChatInfoActivity : AppCompatActivity() {

    private val viewModel : ChatInfoActivityViewModel by viewModels()

    private lateinit var btGoBack: ImageButton
    private lateinit var ivIcon: ImageView
    private lateinit var etName: EditText
    private lateinit var rvUsers: RecyclerView
    private lateinit var btUpdate: Button

    private fun initComponents(){
        btGoBack = findViewById(R.id.ibGoBackInfoChat)
        ivIcon = findViewById(R.id.ivInfoChatIcon)
        etName = findViewById(R.id.etInfoChatName)
        rvUsers = findViewById(R.id.rvInfoUsers)
        btUpdate = findViewById(R.id.btUpdateChat)

        viewModel.chat.observe(this){
            newChat->
            updateInfo(newChat)
        }

        lifecycleScope.launch {
            viewModel.getChatFromDB(intent.getStringExtra("chatId")!!)
            initRecycler()
        }

        btGoBack.setOnClickListener{
            finish()
        }


    }

    private fun initRecycler(){
        val context = this
        lifecycleScope.launch {
            viewModel.getUsers()
            val users = viewModel.usersDBList.value!!

            val customAdapter = ChatInfoUserAdapter(
                users
            )

            rvUsers.layoutManager = LinearLayoutManager(context)
            rvUsers.adapter=customAdapter

            viewModel.usersDBList.observe(context){
                users->
                val adapter = rvUsers.adapter as ChatInfoUserAdapter
                adapter.updateUsersList(users)
            }
        }
    }

    private fun updateInfo(chat: Chat){

       if(chat!=null){

           updateIcon(chat.imageUrl)
           etName.setText(chat.name)
           lifecycleScope.launch {
               viewModel.getUsers()
           }
       }
    }

    private fun updateIcon(icon:String?){
        val icono = icon
        if (icono != null && icono.isNotBlank()) {
            val bitmap = ImageUtils.decodeFromBase64(icono.toString())
            ivIcon.setImageBitmap(bitmap)
        }else{
            ivIcon.setImageResource(R.drawable.default_icon_128)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat_info)

        initComponents()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}