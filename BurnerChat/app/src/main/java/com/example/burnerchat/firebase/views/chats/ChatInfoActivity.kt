package com.example.burnerchat.firebase.views.chats

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import org.w3c.dom.Text

class ChatInfoActivity : AppCompatActivity() {

    private val viewModel: ChatInfoActivityViewModel by viewModels()

    private lateinit var btGoBack: ImageButton
    private lateinit var ivIcon: ImageView
    private lateinit var etName: EditText
    private lateinit var rvUsers: RecyclerView
    private lateinit var btUpdate: Button
    private lateinit var ibSearch: ImageButton
    private lateinit var etSearch: EditText
    private lateinit var rvUsersToAdd: RecyclerView

    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        val galleryURI = it
        try {
            val bitmap = ImageUtils.loadBitmapFromURI(galleryURI!!, contentResolver)
            viewModel.setIcon(bitmap!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initComponents() {
        btGoBack = findViewById(R.id.ibGoBackInfoChat)
        ivIcon = findViewById(R.id.ivInfoChatIcon)
        etName = findViewById(R.id.etInfoChatName)
        rvUsers = findViewById(R.id.rvInfoUsers)
        btUpdate = findViewById(R.id.btUpdateChat)
        etSearch = findViewById(R.id.etSearchChatInfo)
        ibSearch = findViewById(R.id.ibSearchChatInfo)
        rvUsersToAdd = findViewById(R.id.rvAddChatInfo)

        viewModel.chat.observe(this) { newChat ->
            updateInfo(newChat)
        }

        lifecycleScope.launch {
            viewModel.getChatFromDB(intent.getStringExtra("chatId")!!)
            viewModel.getAddableUsers()
            viewModel.getUsersInChat()
            initUsersFromChatRecyler()
            initAddableUsersRecyler()

        }

        btGoBack.setOnClickListener {
            finish()
        }

        ivIcon.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        btUpdate.setOnClickListener {
            // Comprobar si es o no un chat grupal
            if (viewModel.isGroup()) {
                if (viewModel.canDelete()) {
                    viewModel.setNombre(etName.text.toString())
                    viewModel.updateChat()
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Los Chats grupales tienen que tener un mínimo de 3 usuarios",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } else {
                viewModel.setNombre(etName.text.toString())
                viewModel.updateChat()
                finish()
            }
        }
        initSearch()
    }

    private fun initUsersFromChatRecyler() {

        if (viewModel.isGroup()) {//Crear funciones
            val onClickAdd = fun(user: UserDTO) {
                viewModel.selectUserToRemove(user)
            }

            val onClickRemove = fun(user: UserDTO) {
                viewModel.deselectUserToRemove(user)
            }

            val checkContains = fun(user: String): Boolean {
                return viewModel.isInToRemove(user)
            }

            val context = this
            //Coger datos de la base de datos
            val users = viewModel.usersDBList.value

            val customAdapter = UsersGroupAddAdapter(
                users!!,
                onClickAdd,
                onClickRemove,
                checkContains
            )

            rvUsers.layoutManager = LinearLayoutManager(context)
            rvUsers.adapter = customAdapter

            viewModel.selectedToRemoveUsersList.observe(context) {
                val adapter = rvUsers.adapter as UsersGroupAddAdapter
                adapter.reset()
            }

            viewModel.usersDBList.observe(context) { userList ->
                val adapter = rvUsers.adapter as UsersGroupAddAdapter
                adapter.updateUsersList(userList)

            }
        } else {
            val users = viewModel.usersDBList.value

            val customAdapter = ChatInfoUserAdapter(
                users!!
            )

            rvUsers.layoutManager = LinearLayoutManager(this)
            rvUsers.adapter = customAdapter
        }

    }

    private fun initSearch() {
        ibSearch.setOnClickListener {
            val string = etSearch.text.toString()
            if (!string.isNullOrBlank()) {
                lifecycleScope.launch {
                    viewModel.findCurrentAddableUsers(string)
                }
            } else {
                lifecycleScope.launch {
                    viewModel.getAddableUsers()
                }
            }
        }
    }

    private fun initAddableUsersRecyler() {

        if (viewModel.isGroup()) {//Crear funciones
            val onClickAdd = fun(user: UserDTO) {
                viewModel.selectUserToAdd(user)
            }

            val onClickRemove = fun(user: UserDTO) {
                viewModel.deselectUserToAdd(user)
            }

            val checkContains = fun(user: String): Boolean {
                return viewModel.isInToAdd(user)
            }

            val context = this
            //Coger datos de la base de datos

            val users = viewModel.usersDBListAll.value

            val customAdapter = UsersGroupAddAdapter(
                users!!,
                onClickAdd,
                onClickRemove,
                checkContains
            )

            rvUsersToAdd.layoutManager = LinearLayoutManager(context)
            rvUsersToAdd.adapter = customAdapter

            viewModel.selectedToAddUsersList.observe(context) {
                val adapter = rvUsersToAdd.adapter as UsersGroupAddAdapter
                adapter.reset()
            }

            viewModel.usersDBListAll.observe(context) { userList ->
                val adapter = rvUsersToAdd.adapter as UsersGroupAddAdapter
                adapter.updateUsersList(userList)
            }
        } else {
            ibSearch.visibility = View.GONE
            etSearch.visibility = View.GONE
            rvUsersToAdd.visibility = View.GONE
            findViewById<TextView>(R.id.tvAddUsersInfo).visibility = View.GONE
        }
    }


    private fun updateInfo(chat: Chat) {
        updateIcon(chat.imageUrl)
        if (etName.text.toString().isBlank())
            etName.setText(chat.name)
        lifecycleScope.launch {
            viewModel.getUsersInChat()
        }
    }

    private fun updateIcon(icon: String?) {
        val icono = icon
        if (icono != null && icono.isNotBlank()) {
            val bitmap = ImageUtils.decodeFromBase64(icono.toString())
            ivIcon.setImageBitmap(bitmap)
        } else {
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