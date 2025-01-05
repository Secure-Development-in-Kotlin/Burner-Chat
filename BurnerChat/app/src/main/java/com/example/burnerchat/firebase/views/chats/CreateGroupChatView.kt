package com.example.burnerchat.firebase.views.chats

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
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

class CreateGroupChatView : AppCompatActivity() {

    private val viewModel: CreateGroupChatViewViewModel by viewModels()

    private lateinit var btGoBack: ImageButton
    private lateinit var rvGroupUsers: RecyclerView
    private lateinit var btGroupConfirm: Button
    private lateinit var ivGroupIcon: ImageView
    private lateinit var etChatName: EditText
    private lateinit var ibSearch: ImageButton
    private lateinit var etSearch: EditText


    private var galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) {
        val galleryURI = it
        try {
            val bitmap = ImageUtils.loadBitmapFromURI(galleryURI!!, contentResolver)
            viewModel.setIcon(bitmap!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launch {
            viewModel.getUsers()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_group_chat_view)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
    }

    private fun initComponents() {
        btGoBack = findViewById(R.id.ibGoBack)
        rvGroupUsers = findViewById(R.id.rvGroupUsers)
        btGroupConfirm = findViewById(R.id.btGroupConfirm)
        ivGroupIcon = findViewById(R.id.ivGroupChatIcon)
        etChatName = findViewById(R.id.etChatName)
        etSearch = findViewById(R.id.etSearch)
        ibSearch = findViewById(R.id.ibSearch)

        btGoBack.setOnClickListener {
            finish()
        }

        initImageButton()
        initUsersRecyler()
        initConfirmButton()
        initSearch()

        viewModel.createdChat.observe(this) {
            if (it) {
                finish()
            }
        }


    }

    private fun initSearch() {
        ibSearch.setOnClickListener {
            val string = etSearch.text.toString()
            if (!string.isNullOrBlank()) {
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

    private fun initConfirmButton() {
        btGroupConfirm.setOnClickListener {
            val name = etChatName.text.toString()
            if (name.isNotEmpty()) {
                viewModel.addChat(name)
            }
        }

    }

    private fun initUsersRecyler() {

        //Crear funciones
        var onClickAdd = fun(user: UserUIInfo) {
            viewModel.addUser(user)
        }

        var onClickRemove = fun(user: UserUIInfo) {
            viewModel.removeUser(user)
        }

        var checkContains = fun(user: String): Boolean {
            return viewModel.isSelected(user)
        }

        val context = this
        //Coger datos de la base de datos
        lifecycleScope.launch {
            viewModel.getUsers()

            val users = viewModel.dbUsersList.value

            val customAdapter = UsersGroupAddAdapter(
                users!!,
                onClickAdd,
                onClickRemove,
                checkContains
            )

            rvGroupUsers.layoutManager = LinearLayoutManager(context)
            rvGroupUsers.adapter = customAdapter

            viewModel.usersList.observe(context) {
                val adapter = rvGroupUsers.adapter as UsersGroupAddAdapter
                adapter.reset()
            }

            viewModel.dbUsersList.observe(context) { users ->
                val adapter = rvGroupUsers.adapter as UsersGroupAddAdapter
                adapter.updateUsersList(users)
            }
        }

    }

    private fun initImageButton() {
        ivGroupIcon.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        viewModel.icon.observe(this) { icon ->
            val icono = icon
            if (icono != null && icono.isNotBlank()) {
                val bitmap = ImageUtils.decodeFromBase64(icono.toString())
                ivGroupIcon.setImageBitmap(bitmap)
            } else {
                ivGroupIcon.setImageResource(R.drawable.default_icon_128)
            }
        }
    }

}