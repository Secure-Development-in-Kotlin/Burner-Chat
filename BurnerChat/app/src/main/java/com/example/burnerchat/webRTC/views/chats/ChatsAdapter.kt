package com.example.burnerchat.webRTC.views.chats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.R
import com.example.burnerchat.webRTC.business.ImageUtils
import com.example.burnerchat.webRTC.model.chats.Chat
import com.example.burnerchat.webRTC.model.users.User

class ChatsAdapter(

    private val chatsList: List<Chat>,
    private val id: String,
    private val onClickListener: (Chat?) -> Unit

) : RecyclerView.Adapter<ChatsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatsAdapter.ViewHolder {
        val layoutElement = R.layout.chat_element_view
        val view = LayoutInflater.from(parent.context).inflate(layoutElement, parent, false)
        return ViewHolder(view, id, onClickListener)
    }

    override fun onBindViewHolder(holder: ChatsAdapter.ViewHolder, position: Int) {
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

        fun setImage(user: User){
            val icon = user.getIcon()

            if(icon.isBlank()){
                ivIcon.setImageResource(R.drawable.baseline_person_24)
            }else
                ivIcon.setImageBitmap(ImageUtils.decodeFromBase64(icon))
        }

        fun bind(chat: Chat) {
            chatActual = chat
            tvName.text = chatActual.getTarget().username

            setImage(chat.getTarget())


            if (!chatActual.isEmpty())
                tvLastMessage.text = chatActual.getLastMessage().getLastContent()
            else
                tvLastMessage.text = ""
        }
    }
}