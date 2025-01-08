package com.example.burnerchat.firebase

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.burnerchat.BurnerChatApp
import com.example.burnerchat.firebase.views.chats.UserDTO
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class FirebaseAuthViewModel : ViewModel() {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    fun signUp(email: String, password: String, onSuccess: (FirebaseUser) -> Unit, onError: (Exception?) -> Unit) {
        if (email.isNotBlank() && password.isNotBlank()) {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.user?.let { user ->
                            onSuccess(user)
                        }
                    } else {
                        onError(task.exception)
                    }
                }
        } else {
            onError(IllegalArgumentException("Email or password cannot be blank"))
        }
    }

    fun logIn(email: String, password: String, onSuccess: (FirebaseUser) -> Unit, onError: (Exception?) -> Unit) {
        if (email.isNotBlank() && password.isNotBlank()) {
            firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        task.result?.user?.let { user ->
                            onSuccess(user)
                        }
                    } else {
                        onError(task.exception)
                    }
                }
        } else {
            onError(IllegalArgumentException("Email or password cannot be blank"))
        }
    }

    fun syncUserInDB(user: FirebaseUser, callback: () -> Unit) {
        val usersRepository = BurnerChatApp.appModule.usersRepository

        val currentUser = Firebase.auth.currentUser
        val currentUserId = currentUser?.uid

        if (currentUserId != null) {
            usersRepository.getUser(currentUserId) { userDB ->
                if (userDB == null) {
                    // User does not exist in the database, add the user
                    usersRepository.addUser(user)
                } else {
                    // User exists in the database, update the user
                    val userDTO = UserDTO(currentUser.email!!, currentUser.photoUrl.toString())
                    usersRepository.updateUser(currentUser, userDTO)
                }
                callback() // Ensure the callback is executed here
            }
        } else {
            // Handle the case where there is no authenticated user
            Log.d("FirebaseAuth", "No authenticated user found.")
            callback() // Ensure the callback is executed even if no user is authenticated
        }
    }


}