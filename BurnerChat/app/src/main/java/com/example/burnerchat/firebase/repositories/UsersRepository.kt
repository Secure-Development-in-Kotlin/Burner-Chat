package com.example.burnerchat.firebase.repositories

import android.util.Log
import com.example.burnerchat.firebase.views.chats.UserDTO
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await


object UsersRepository {

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

    fun getUser(userId: String, callback: (UserDTO?) -> Unit) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val email = document.getString("email")
                    val profilePicture = document.getString("profilePicture")
                    callback(UserDTO(email!!, profilePicture ?: ""))
                } else {
                    callback(null) // User does not exist
                }
            }
            .addOnFailureListener {
                callback(null) // Handle error case
            }
    }


    suspend fun getUserData(): UserDTO? {
        // TODO: refactor to search by id
        val result = ChatsRepository.db.collection(USER_COLLECTION_NAME).get().await()
        for (document in result) {
            val data = document.data
            val firebaseUserData = data as Map<String, Any>

            if (firebaseUserData["email"] == getLoggedUser()?.email) {
                var profilePicture = firebaseUserData["profilePicture"].toString() ?: ""

                val userData =
                    UserDTO(firebaseUserData["email"].toString(), profilePicture)
                return userData
            }

        }
        return null
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
            .addOnSuccessListener {
                Log.d("Firestore", "User data added/updated successfully")
            }
            .addOnFailureListener { e: Exception? ->
                Log.e("Firestore", "Error adding/updating user data", e)
            }
    }

    fun updateUser(currentUser: FirebaseUser, userDTO: UserDTO) {
        val userId: String = currentUser.uid // Unique Firebase Auth UID
        val email: String = currentUser.email.toString()

        // Prepare the user document
        val userData: MutableMap<String, Any?> = HashMap()
        userData["email"] = email
        userData["profilePicture"] = userDTO.icon
        userData["lastSeen"] = FieldValue.serverTimestamp()


        // Add or update user in Firestore
        db.collection("users").document(userId).set(userData, SetOptions.merge())
            .addOnSuccessListener {
                Log.d("Firestore", "User data added/updated successfully")
            }
            .addOnFailureListener { e: Exception? ->
                Log.e("Firestore", "Error adding/updating user data", e)
            }
    }


    // Delete everything related to the user
    fun sendPanic() {
        // Se borra lo relacionado con el usuario: mensajes, chats y demás
        val currentUser = getLoggedUser()!!

        deleteUser(currentUser)

    }

    // Delete the user from the database
    private fun deleteUser(currentUser: FirebaseUser) {
        db.collection(USER_COLLECTION_NAME).document(currentUser.uid).delete()
            .addOnSuccessListener {
                Log.d("Firestore", "User data deleted successfully")
                // Deletes logged user from FirebaseAuth
                currentUser.delete()
            }
            .addOnFailureListener { e: Exception? ->
                Log.e("Firestore", "Error deleting user data", e)
            }
    }

    suspend fun getUsers(): List<UserDTO> {
        val result = ChatsRepository.db.collection(USER_COLLECTION_NAME).get().await()
        val usersDataBase = mutableListOf<UserDTO>()
        for (document in result) {
            val data = document.data
            val firebaseUserData = data as Map<String, Any>

            if (firebaseUserData["email"] != getLoggedUser()?.email) {
                var profilePicture = firebaseUserData["profilePicture"]
                profilePicture = profilePicture?.toString() ?: " "

                val userData = UserDTO(firebaseUserData["email"].toString(), profilePicture.toString())
                usersDataBase.add(userData)
            }

        }
        return usersDataBase
    }

    suspend fun getUsersByEmail(emails: Array<String>): List<UserDTO> {
        val result = ChatsRepository.db.collection(USER_COLLECTION_NAME).get().await()
        val usersDataBase = mutableListOf<UserDTO>()
        for (document in result) {
            val data = document.data
            val firebaseUserData = data as Map<String, Any>
            if (emails.contains(firebaseUserData["email"])) {
                var profilePicture = firebaseUserData["profilePicture"]
                profilePicture = profilePicture?.toString() ?: ""

                val userData = UserDTO(
                    firebaseUserData["email"].toString(),
                    profilePicture.toString()
                )
                usersDataBase.add(userData)
            }

        }
        return usersDataBase
    }

    suspend fun getUsersByString(string: String): List<UserDTO> {
        val result = ChatsRepository.db.collection(USER_COLLECTION_NAME).get().await()
        val usersDataBase = mutableListOf<UserDTO>()
        for (document in result) {
            val data = document.data
            val firebaseUserData = data as Map<String, Any>

            if (firebaseUserData["email"] != getLoggedUser()?.email) {
                if ((firebaseUserData["email"].toString().contains(string))) {
                    var profilePicture = firebaseUserData["profilePicture"]
                    profilePicture = profilePicture?.toString() ?: ""

                    val userData =
                        UserDTO(firebaseUserData["email"].toString(), profilePicture.toString())
                    usersDataBase.add(userData)
                }

            }

        }
        return usersDataBase
    }

    fun logout(): Boolean {
        FirebaseAuth.getInstance()
            .signOut()
        return FirebaseAuth.getInstance().currentUser == null
    }

    suspend fun getUsersExcept(emails: List<String>): List<UserDTO> {
        val result = ChatsRepository.db.collection(USER_COLLECTION_NAME).get().await()
        val usersDataBase = mutableListOf<UserDTO>()
        for (document in result) {
            val data = document.data
            val firebaseUserData = data as Map<String, Any>
            if (!emails.contains(firebaseUserData["email"])) {
                var profilePicture = firebaseUserData["profilePicture"]
                profilePicture = profilePicture?.toString() ?: ""

                val userData = UserDTO(
                    firebaseUserData["email"].toString(),
                    profilePicture.toString()
                )
                usersDataBase.add(userData)
            }

        }
        return usersDataBase
    }

    suspend fun getUsersByStringExcept(string: String, emails: Array<String>): List<UserDTO>? {
        val result = ChatsRepository.db.collection(USER_COLLECTION_NAME).get().await()
        val usersDataBase = mutableListOf<UserDTO>()
        for (document in result) {
            val data = document.data
            val firebaseUserData = data as Map<String, Any>
            if (!emails.contains(firebaseUserData["email"]) && firebaseUserData["email"] != getLoggedUser()?.email) {
                if ((firebaseUserData["email"].toString().contains(string))) {
                    var profilePicture = firebaseUserData["profilePicture"]
                    if (profilePicture == null)
                        profilePicture = ""
                    else
                        profilePicture = profilePicture.toString()

                    val userData = UserDTO(firebaseUserData["email"].toString(), profilePicture)
                    usersDataBase.add(userData)
                }


            }

        }
        return usersDataBase
    }

}