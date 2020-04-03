package com.example.mapchat.repository

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mapchat.model.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class FirebaseRepository(private val db: FirebaseFirestore) {


    fun uploadDataToFirebase(
        mAuth: FirebaseAuth,
        latitude: Double?,
        longitude: Double?
    ): MutableLiveData<Boolean> {

        val isUploaded = MutableLiveData<Boolean>()


        db.collection("Users").document(mAuth.currentUser!!.uid)
            .set(
                Users(
                    mAuth.currentUser!!.uid,
                    mAuth.currentUser!!.displayName,
                    mAuth.currentUser!!.email,
                    mAuth.currentUser!!.phoneNumber,
                    listOf(),
                    mAuth.currentUser!!.photoUrl.toString(),
                    latitude,
                    longitude
                )
            ).addOnCompleteListener { reference ->
                isUploaded.value = true
                // isLoading.value = false
            }.addOnFailureListener { e ->
                // isError.value = e.message
                isUploaded.value = false
            }

        return isUploaded
    }


    fun getSingleUser(userId: String): MutableLiveData<Users> {
        lateinit var singleUser: MutableLiveData<Users>


        db.collection("Users").document(userId).addSnapshotListener { snapshot, error ->

            if (snapshot != null) {

                singleUser.value = snapshot.toObject(Users::class.java)
            } else {
                singleUser.value = null
            }
            if (error != null) {

            }

        }

        return singleUser
    }


    fun getUserList(): MutableLiveData<List<Users>> {

        val userMutableList = MutableLiveData<List<Users>>()

        val userList = ArrayList<Users>()

        db.collection("Users").addSnapshotListener { snapshot, e ->
            if (snapshot!!.isEmpty) {
                Log.d("FirebaseRepository", "getUserList: ")
            } else {

                for (documents in snapshot.documents) {
                    userList.add(documents.toObject(Users::class.java)!!)


                }
                userMutableList.value = userList


            }

            if (e != null) {
                Log.d("FirebaseRepository", "getUserList: ")
            }


        }



        return userMutableList

    }
}