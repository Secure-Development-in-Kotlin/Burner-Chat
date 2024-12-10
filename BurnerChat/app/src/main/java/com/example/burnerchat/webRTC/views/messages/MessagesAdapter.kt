package com.example.burnerchat.webRTC.views.messages

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.R
import com.example.burnerchat.webRTC.model.messages.Message

class MessagesAdapter(
    private val messagesList: List<Message>,
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var tvMessage: TextView = view.findViewById(R.id.tvMessage)

        private fun formatDate(message: Message):String{
            var horas = message.getSentDate().hour
            var minutos = message.getSentDate().minute
            var string1 = message.getSentDate().toLocalDate().atTime(horas,minutos).toString()
            var string2 = string1.split("T")
            return string2[1]+" "+string2[0]

        }
        fun bind(message: Message) {
             tvMessage.text = message.getContent()+"\n"+formatDate(message)
            if(message.getUser()!==BurnerChatApp.appModule.usersRepository.getUser()){
                var params = tvMessage.layoutParams as RelativeLayout.LayoutParams
                tvMessage.setBackgroundColor(Color.parseColor("#03A9F4"))
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, R.id.tvMessage)
            }else{
                var params = tvMessage.layoutParams as RelativeLayout.LayoutParams
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, R.id.tvMessage)
            }
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