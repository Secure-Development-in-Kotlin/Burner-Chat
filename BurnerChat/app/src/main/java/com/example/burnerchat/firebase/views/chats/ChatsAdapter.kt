package com.example.burnerchat.firebase.views.chats

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.R
import com.example.burnerchat.firebase.model.chats.Chat
import com.example.burnerchat.firebase.repositories.ImageUtils

class ChatsAdapter(

    private var chatsList: List<Chat>,
    private val id: String,
    private val onClickListener: (Chat?) -> Unit

) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    fun updateChatsList(list: List<Chat>) {
        chatsList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutElement = R.layout.chat_element_view
        val view = LayoutInflater.from(parent.context).inflate(layoutElement, parent, false)
        return ViewHolder(view, id, onClickListener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(chatsList[position])
    }

    override fun getItemCount(): Int = chatsList.size

    class ViewHolder(view: View, private val id: String, onClick: (Chat?) -> Unit) :
        RecyclerView.ViewHolder(view) {
        private lateinit var ivIcon: ImageView
        private lateinit var tvName: TextView
        private lateinit var tvLastMessage: TextView
        private lateinit var chatActual: Chat


        init {
            initComponents(view)
            view.setOnClickListener {
                onClick(chatActual)
            }
        }

        fun initComponents(view: View) {
            ivIcon = view.findViewById(R.id.ivChatIcon)
            tvName = view.findViewById(R.id.tvChatName)
            tvLastMessage = view.findViewById(R.id.tvLastMessage)
        }

        private fun setImage(imageUrl: String?) {
            if (imageUrl == null || TextUtils.isEmpty(imageUrl.toString())) {
                ivIcon.setImageResource(R.drawable.baseline_person_24)
            } else
                ivIcon.setImageBitmap(ImageUtils.decodeFromBase64(imageUrl.toString()))
        }

        fun bind(chat: Chat) {
            chatActual = chat
            tvName.text = chatActual.name

            setImage(chat.imageUrl)

            if (!chatActual.isEmpty()) {
//                tvLastMessage.text = chatActual.getLastMessage().getLastContent()
                val maxLength = 20 // Número máximo de caracteres

                tvLastMessage.apply {
                    // Limita el texto a un número máximo de caracteres y agrega "..." si es necesario
                    val message = chatActual.getLastMessage().getLastContent()
                    text = if (message.length > maxLength) {
                        message.take(maxLength) + "..." // Agrega "..." si el texto excede el límite
                    } else {
                        message
                    }
                }
            } else
                tvLastMessage.text = ""
        }
    }
}