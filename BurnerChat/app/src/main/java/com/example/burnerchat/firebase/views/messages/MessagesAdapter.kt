package com.example.burnerchat.firebase.views.messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.R
import com.example.burnerchat.firebase.model.messages.Message
import com.example.burnerchat.firebase.model.messages.messageImpls.ImageMessage
import com.example.burnerchat.firebase.repositories.ImageUtils
import java.time.ZoneId

class MessagesAdapter(
    private var messagesList: List<Message>,
) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    fun updateMessages(messages: List<Message>) {
        messagesList = messages

    }

    abstract class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        private var tvDate: TextView = view.findViewById(R.id.tvDate)
        protected lateinit var message: Message

        private fun formatDate(message: Message): String {
            val timestamp = message.getSentDate()
            val localDateTime = timestamp.toDate().toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime()

            val horas = localDateTime.hour
            val minutos = localDateTime.minute
            val string1 = localDateTime.toLocalDate().atTime(horas, minutos).toString()
            val string2 = string1.split("T")
            return string2[1] + " " + string2[0]
        }

        fun bind(message: Message) {
            this.message = message
            tvDate.text = formatDate(message)
            extraContent(message)

        }

        abstract fun extraContent(message: Message)
    }

    open class TextViewHolder(view: View) : ViewHolder(view) {
        protected var tvMessage: TextView = view.findViewById(R.id.tvMessage)
        override fun extraContent(message: Message) {
            tvMessage.text = message.getContent()
        }

    }

    class NameTextViewHolder(view: View) : TextViewHolder(view) {
        private val tvNombre: TextView = view.findViewById(R.id.tvUser)
        override fun extraContent(message: Message) {
            tvNombre.text = message.getUserEmail()
            tvMessage.text = message.getContent()
        }
    }

    open class SelfImageViewHolder(view: View) : ViewHolder(view) {
        protected val ivImage: ImageView = view.findViewById(R.id.ivImage)
        protected val tvText: TextView = view.findViewById(R.id.tvMessage)
        override fun extraContent(message: Message) {
            val messageCast = message as ImageMessage
            val image = messageCast.getContent()
            val text = messageCast.textContent
            ivImage.setImageBitmap(ImageUtils.decodeFromBase64(image))
            tvText.text = text
        }
    }

    class OtherImageViewHolder(view: View) : SelfImageViewHolder(view) {
        private val tvNombre: TextView = view.findViewById(R.id.tvUser)
        override fun extraContent(message: Message) {
            val messageCast = message as ImageMessage
            val image = messageCast.getContent()
            val text = messageCast.textContent
            ivImage.setImageBitmap(ImageUtils.decodeFromBase64(image))
            tvText.text = text
            tvNombre.text = message.getUserEmail()
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = messagesList[position]
        return message.getMessageTypeCode(BurnerChatApp.appModule.usersRepository.getLoggedUser()?.email!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return when (viewType) {
            Message.LayoutType.TextoAjeno.ordinal -> {
                val layoutElement = R.layout.other_text_message_element_view
                val view = LayoutInflater.from(parent.context).inflate(layoutElement, parent, false)
                return NameTextViewHolder(view)
            }

            Message.LayoutType.TextoPropio.ordinal -> {
                val layoutElement = R.layout.self_text_message_element_view
                val view = LayoutInflater.from(parent.context).inflate(layoutElement, parent, false)
                return TextViewHolder(view)
            }

            Message.LayoutType.ImagenAjena.ordinal -> {
                val layoutElement = R.layout.other_image_message_element_view
                val view = LayoutInflater.from(parent.context).inflate(layoutElement, parent, false)
                return OtherImageViewHolder(view)
            }

            Message.LayoutType.ImagenPropia.ordinal -> {
                val layoutElement = R.layout.self_image_message_element_view
                val view = LayoutInflater.from(parent.context).inflate(layoutElement, parent, false)
                return SelfImageViewHolder(view)
            }

            else -> {
                val layoutElement = R.layout.self_text_message_element_view
                val view = LayoutInflater.from(parent.context).inflate(layoutElement, parent, false)
                return TextViewHolder(view)
            }
        }

    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(messagesList[position])
    }

    override fun getItemCount(): Int {
        return messagesList.size
    }
}