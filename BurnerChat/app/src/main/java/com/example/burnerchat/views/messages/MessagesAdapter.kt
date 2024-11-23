package com.example.burnerchat.views.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.R
import com.example.burnerchat.model.messages.Message

class MessagesAdapter(
    private val messagesList: List<Message>,
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var tvMessage: TextView = view.findViewById(R.id.tvMessage)

        fun bind(message: Message) {
            tvMessage.text = message.getContent()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutElement = R.layout.sender_message_element_view
        val view = LayoutInflater.from(parent.context).inflate(layoutElement, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messagesList[position])
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }
}