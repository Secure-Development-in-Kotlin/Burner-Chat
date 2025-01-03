package com.example.burnerchat.webRTC.business

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore


object UserPersistenceManager {
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
    fun sendPanic() {
        TODO("Se borra todo lo relacionado con el usuario: mensajes, chats y demás")
    }
}