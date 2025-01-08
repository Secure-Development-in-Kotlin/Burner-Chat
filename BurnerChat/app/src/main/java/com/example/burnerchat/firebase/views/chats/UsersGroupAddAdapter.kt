package com.example.burnerchat.firebase.views.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.R
import com.example.burnerchat.firebase.repositories.ImageUtils

class UsersGroupAddAdapter(
    private var usersList: List<UserDTO>,
    private val onClickListenerAdd: (UserDTO) -> Unit,
    private val onClickListenerRemove: (UserDTO) -> Unit,
    private val checkContains: (String) -> Boolean

) : RecyclerView.Adapter<UsersGroupAddAdapter.ViewHolder>() {

    fun updateUsersList(list: List<UserDTO>) {
        usersList = list
        notifyDataSetChanged()
    }

    fun reset() {
        updateUsersList(usersList)
    }

    class ViewHolder(
        view: View,
        onClick: (UserDTO) -> Unit,
        onClickRemove: (UserDTO) -> Unit,
        checkContains: (String) -> Boolean
    ) :
        RecyclerView.ViewHolder(view) {

        private lateinit var ivUserIcon: ImageView
        private lateinit var tvName: TextView
        private lateinit var ibAdd: ImageButton
        private lateinit var userActual: UserDTO
        private val onClickFunc = onClick
        private val checkContains = checkContains
        private val deleteFunc = onClickRemove
        private val vista = view

        init {
            initComponents(view)
            ibAdd.setOnClickListener {
                onClick(userActual)
            }
        }

        fun initComponents(view: View) {
            ivUserIcon = view.findViewById(R.id.ivGroupAddIcon)
            tvName = view.findViewById(R.id.tvAddUserEmail)

            initButton(view)
        }

        private fun initButton(view: View) {
            ibAdd = view.findViewById(R.id.ibAddUser)

        }

        fun bind(userInfo: UserDTO) {
            userActual = userInfo
            tvName.text = userActual.email
            setImage(userActual.icon)
            val isOnList = checkContains(userActual.email)

            if (userActual.email == BurnerChatApp.appModule.usersRepository.getLoggedUser()?.email) {
                tvName.text = userActual.email + " (You)"
                ibAdd.visibility = View.GONE
            } else if (isOnList) {
                ibAdd.setBackgroundColor(vista.resources.getColor(R.color.button2, null))
                ibAdd.setImageResource(R.drawable.baseline_cancel_24)
                ibAdd.setOnClickListener {
                    deleteFunc(userActual)
                }
            } else {
                ibAdd.setBackgroundColor(vista.resources.getColor(R.color.button1, null))
                ibAdd.setImageResource(R.drawable.baseline_add_24)
                ibAdd.setOnClickListener {
                    onClickFunc(userActual)
                }
            }


        }

        private fun setImage(imageUrl: String?) {
            if (imageUrl.isNullOrBlank() || imageUrl == "null") {
                ivUserIcon.setImageResource(R.drawable.baseline_person_24)
            } else
                ivUserIcon.setImageBitmap(ImageUtils.decodeFromBase64(imageUrl.toString()))
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutElement = R.layout.user_info_group_chat_element_view
        val view = LayoutInflater.from(parent.context).inflate(layoutElement, parent, false)
        return ViewHolder(view, onClickListenerAdd, onClickListenerRemove, checkContains)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(usersList[position])
    }
}