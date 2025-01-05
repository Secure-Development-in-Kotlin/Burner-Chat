package com.example.burnerchat.firebase.views.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.R
import com.example.burnerchat.firebase.repositories.ImageUtils

class ChatInfoUserAdapter(
    private var usersList:List<UserUIInfo>
) :RecyclerView.Adapter<ChatInfoUserAdapter.ViewHolder>(){

    fun updateUsersList(list:List<UserUIInfo>){
        usersList = list
        notifyDataSetChanged()
    }

    class ViewHolder(
        view: View
    ):RecyclerView.ViewHolder(view){

        private val usersRepo = BurnerChatApp.appModule.usersRepository

        private lateinit var ivUserIcon: ImageView
        private lateinit var tvName: TextView
        private lateinit var userActual : UserUIInfo
        private val view = view
        init  {
            initComponents()
        }

        private fun initComponents(){
            ivUserIcon = view.findViewById(R.id.ivGroupAddIcon)
            tvName = view.findViewById(R.id.tvAddUserEmail)
        }

        fun bind(userInfo: UserUIInfo){
            userActual = userInfo
            val email = userActual.email
            val loggedUser = usersRepo.getLoggedUser()?.email!!
            if(email == loggedUser)
                tvName.text = userActual.email+" (You)"
            else
                tvName.text = userActual.email
            setImage(userActual.icon)
        }

        fun setImage(imageUrl: String?) {
            if (imageUrl.isNullOrBlank()) {
                ivUserIcon.setImageResource(R.drawable.baseline_person_24)
            } else
                ivUserIcon.setImageBitmap(ImageUtils.decodeFromBase64(imageUrl.toString()))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutElement = R.layout.user_info_chat_info_element_view
        val view = LayoutInflater.from(parent.context).inflate(layoutElement, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(usersList[position])
    }
}