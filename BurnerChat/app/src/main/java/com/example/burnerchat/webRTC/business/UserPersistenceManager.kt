package com.example.burnerchat.webRTC.business

import android.util.Log
import com.example.burnerchat.webRTC.views.chats.UserUIInfo
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


object UserPersistenceManager {

    private const val USER_COLLECTION_NAME = "users"

    val db = Firebase.firestore

    fun getLoggedUser(): FirebaseUser? {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            return currentUser
        } else {
            Log.e("UserPersistenceManager", "No user logged in")
            return null
        }
    }

    fun getUser(userId: String): FirebaseUser? {
//        var user: FirebaseUser? = null
//        db.collection("users").document(userId).get().addOnSuccessListener {
//            user = it.toObject(FirebaseUser::class.java)
//        }
        // TODO: add onFailureListener

//        return user
        return Firebase.auth.currentUser
    }

    fun addUser(currentUser: FirebaseUser) {
        val userId: String = currentUser.uid // Unique Firebase Auth UID
        val email: String = currentUser.email.toString()
        val photoUrl: String? =
            if ((currentUser.photoUrl != null)) currentUser.photoUrl.toString() else null


        // Prepare the user document
        val userData: MutableMap<String, Any?> = HashMap()
        userData["email"] = email
        userData["profilePicture"] = photoUrl
        userData["lastSeen"] = FieldValue.serverTimestamp()


        // Add or update user in Firestore
        db.collection("users").document(userId).set(userData, SetOptions.merge())
            .addOnSuccessListener { aVoid: Void? ->
                Log.d("Firestore", "User data added/updated successfully")
            }
            .addOnFailureListener { e: Exception? ->
                Log.e("Firestore", "Error adding/updating user data", e)
            }
    }

    // Delete everything related to the user
    suspend fun sendPanic(currentUser: FirebaseUser) {
        // Se borra lo relacionado con el usuario: mensajes, chats y demás
        val chats = ChatsPersistenceManager.getChatsByUser(currentUser)
        for (chat in chats) {
            ChatsPersistenceManager.deleteMessagesByUser(chat, currentUser)
            deleteUser(currentUser)
        }
    }

    // Delete the user from the database
    private fun deleteUser(currentUser: FirebaseUser) {
        db.collection(USER_COLLECTION_NAME).document(currentUser.uid).delete()
            .addOnSuccessListener {
                Log.d("Firestore", "User data deleted successfully")
                // Deletes cached user
                currentUser.delete()
            }
            .addOnFailureListener { e: Exception? ->
                Log.e("Firestore", "Error deleting user data", e)
            }
    }

    suspend fun getUsers(): List<UserUIInfo> {
        val result = ChatsPersistenceManager.db.collection(USER_COLLECTION_NAME).get().await()
        val usersDataBase = mutableListOf<UserUIInfo>()
        for (document in result) {
            val data = document.data
            val firebaseUserData = data as Map<String, Any>

            if (firebaseUserData["email"] != getLoggedUser()?.email) {
                var profilePicture = firebaseUserData["profilePicture"]
                if (profilePicture == null)
                    profilePicture = " "
                else
                    profilePicture = profilePicture.toString()

                val userData = UserUIInfo(firebaseUserData["email"].toString(), profilePicture)
                usersDataBase.add(userData)
            }

        }
        return usersDataBase
    }

    suspend fun getUsersByEmail(emails: Array<String>): List<UserUIInfo> {
        val result = ChatsPersistenceManager.db.collection(USER_COLLECTION_NAME).get().await()
        val usersDataBase = mutableListOf<UserUIInfo>()
        for (document in result) {
            val data = document.data
            val firebaseUserData = data as Map<String, Any>


            if (emails.contains(firebaseUserData["email"])) {
                var profilePicture = firebaseUserData["profilePicture"]
                if (profilePicture == null)
                    profilePicture = " "
                else
                    profilePicture = profilePicture.toString()

                val userData = UserUIInfo(firebaseUserData["email"].toString(), profilePicture)
                usersDataBase.add(userData)
            }


        }
        return usersDataBase
    }

    suspend fun getUsersByString(string: String): List<UserUIInfo> {
        val result = ChatsPersistenceManager.db.collection(USER_COLLECTION_NAME).get().await()
        val usersDataBase = mutableListOf<UserUIInfo>()
        for (document in result) {
            val data = document.data
            val firebaseUserData = data as Map<String, Any>

            if (firebaseUserData["email"] != getLoggedUser()?.email) {
                if ((firebaseUserData["email"].toString().contains(string))) {
                    var profilePicture = firebaseUserData["profilePicture"]
                    if (profilePicture == null)
                        profilePicture = " "
                    else
                        profilePicture = profilePicture.toString()

                    val userData = UserUIInfo(firebaseUserData["email"].toString(), profilePicture)
                    usersDataBase.add(userData)
                }

            }

        }
        return usersDataBase
    }

}