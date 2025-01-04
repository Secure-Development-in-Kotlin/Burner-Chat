package com.example.burnerchat.webRTC.views.chats

import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import com.example.burnerchat.R
import com.example.burnerchat.webRTC.business.ImageUtils
import com.example.burnerchat.webRTC.model.chats.Chat
import com.google.firebase.firestore.auth.User

class UsersGroupAddAdapter(
    private var usersList :List<UserUIInfo>,
    private val onClickListenerAdd: (UserUIInfo) -> Unit,
    private val onClickListenerRemove:(UserUIInfo)->Unit,
    private val checkContains:(String)->Boolean

): RecyclerView.Adapter<UsersGroupAddAdapter.ViewHolder>() {

    fun updateUsersList(list:List<UserUIInfo>){
        usersList = list
        notifyDataSetChanged()
    }

    fun reset(){
        updateUsersList(usersList)
    }
    class ViewHolder(
        view: View,
        onClick: (UserUIInfo) -> Unit,
        onClickRemove:(UserUIInfo)->Unit,
        checkContains:(String)->Boolean) :
        RecyclerView.ViewHolder(view){

            private lateinit var ivUserIcon: ImageView
            private lateinit var tvName: TextView
            private lateinit var ibAdd: ImageButton
            private lateinit var userActual : UserUIInfo
            private val onClickFunc = onClick
            private val checkContains = checkContains
            private val deleteFunc = onClickRemove
            private val vista = view

            init {
                initComponents(view)
                ibAdd.setOnClickListener{
                    onClick(userActual)
                }
            }

        fun initComponents(view: View){
            ivUserIcon = view.findViewById(R.id.ivGroupAddIcon)
            tvName = view.findViewById(R.id.tvAddUserEmail)

            initButton(view)
        }

        private fun initButton(view: View){
            ibAdd = view.findViewById(R.id.ibAddUser)

        }

        fun bind(userInfo:UserUIInfo){
            userActual = userInfo
            tvName.text = userActual.email
            setImage(userActual.icon)
            val isOnList = checkContains(userActual.email)

            if(isOnList){
                ibAdd.setBackgroundColor(vista.resources.getColor(R.color.button2,null))
                ibAdd.setImageResource(R.drawable.baseline_cancel_24)
                ibAdd.setOnClickListener{
                    deleteFunc(userActual)
                }
            }else{
                ibAdd.setBackgroundColor(vista.resources.getColor(R.color.button1,null))
                ibAdd.setImageResource(R.drawable.baseline_add_24)
                ibAdd.setOnClickListener{
                    onClickFunc(userActual)
                }
            }


        }

        fun setImage(imageUrl: String?) {
            if (imageUrl.isNullOrBlank()) {
                ivUserIcon.setImageResource(R.drawable.baseline_person_24)
            } else
                ivUserIcon.setImageBitmap(ImageUtils.decodeFromBase64(imageUrl.toString()))
        }



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutElement = R.layout.user_info_group_chat_element_view
        val view = LayoutInflater.from(parent.context).inflate(layoutElement, parent, false)
        return ViewHolder(view,onClickListenerAdd,onClickListenerRemove,checkContains)
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(usersList[position])
    }
}