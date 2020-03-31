package com.example.mapchat.repository

import androidx.lifecycle.MutableLiveData
import com.example.mapchat.model.Users
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseRepository {

    val db = Firebase.firestore
    private lateinit var singleUser: MutableLiveData<Users>

    fun getSingleUser(userId: String): MutableLiveData<Users> {

        db.collection("Users").document(userId).addSnapshotListener { snapshot, error ->

            if (snapshot != null) {

                singleUser.value = snapshot.toObject(Users::class.java)
            } else {
                singleUser.value = null
            }
            if(error!=null){

            }

        }

        return singleUser
    }
}