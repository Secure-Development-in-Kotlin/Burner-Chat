package com.example.burnerchat.firebase.views.chats

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
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
import kotlinx.coroutines.launch

class CreateSingleChatView : AppCompatActivity() {
    private val viewModel: CreateSingleChatViewModel by viewModels()

    private lateinit var btGoBack: ImageButton
    private lateinit var btConfirm: Button
    private lateinit var etName: EditText
    private lateinit var ivIcon: ImageView
    private lateinit var ibSearch: ImageButton
    private lateinit var etSearch: EditText
    private lateinit var rvUsers: RecyclerView

    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        val galleryURI = it
        try {
            val bitmap = ImageUtils.loadBitmapFromURI(galleryURI!!, contentResolver)
            viewModel.setIcon(bitmap!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun initSearch() {
        ibSearch.setOnClickListener {
            val string = etSearch.text.toString()
            if (string.isNotBlank()) {
                lifecycleScope.launch {
                    viewModel.findUsers(string)
                }
            } else {
                lifecycleScope.launch {
                    viewModel.getUsers()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_single_chat_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
    }

    private fun initComponents() {
        btGoBack = findViewById(R.id.ibGoBack)
        btConfirm = findViewById(R.id.btConfirm)
        etName = findViewById(R.id.etName)
        ibSearch = findViewById(R.id.ibSingleUserSearch)
        etSearch = findViewById(R.id.etSingleUserChat)
        rvUsers = findViewById(R.id.rvSingleUserUsers)

        ivIcon = findViewById(R.id.ivSingleChatCreation)
        btGoBack.setOnClickListener {
            finish()
        }

        btConfirm.setOnClickListener {
            val name = etName.text.toString()
            if (name.isEmpty()) {
                Toast.makeText(
                    this,
                    "El chat debe tener un nombre",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (viewModel.canAdd()) {
                viewModel.addChat(name)
            } else {
                Toast.makeText(
                    this,
                    "Necesitas tener un usuario seleccionado",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        viewModel.createdChat.observe(this) {
            if (it) {
                finish()
            }
        }

        initImageButton()
        initUsersRecyler()
        initSearch()

    }

    private fun initImageButton() {
        ivIcon.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        viewModel.icon.observe(this) { icon ->
            if (icon != null && icon.isNotBlank()) {
                val bitmap = ImageUtils.decodeFromBase64(icon.toString())
                ivIcon.setImageBitmap(bitmap)
            } else {
                ivIcon.setImageResource(R.drawable.default_icon_128)
            }
        }
    }

    private fun initUsersRecyler() {

        //Crear funciones
        val onClickAdd = fun(user: UserDTO) {
            viewModel.selectUser(user.email)
        }

        val onClickRemove = fun(_: UserDTO) {
            viewModel.deselectUser()
        }

        val checkContains = fun(user: String): Boolean {
            return viewModel.isSelected(user)
        }

        val context = this
        //Coger datos de la base de datos
        lifecycleScope.launch {
            viewModel.getUsers()

            val users = viewModel.usersDBList.value

            val customAdapter = UsersGroupAddAdapter(
                users!!,
                onClickAdd,
                onClickRemove,
                checkContains
            )

            rvUsers.layoutManager = LinearLayoutManager(context)
            rvUsers.adapter = customAdapter

            viewModel.selectedUser.observe(context) {
                val adapter = rvUsers.adapter as UsersGroupAddAdapter
                adapter.reset()
            }

            viewModel.usersDBList.observe(context) { userList ->
                val adapter = rvUsers.adapter as UsersGroupAddAdapter
                adapter.updateUsersList(userList)
            }
        }

    }

}