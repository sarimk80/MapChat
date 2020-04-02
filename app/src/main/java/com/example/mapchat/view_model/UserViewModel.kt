package com.example.mapchat.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapchat.model.UserMessages
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class UserViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val allFriends = MutableLiveData<List<UserMessages>>()
    private val friendsList = ArrayList<UserMessages>()
    private val isLoading = MutableLiveData<Boolean>()
    private val isError = MutableLiveData<String>()


    fun getAllFriends(): MutableLiveData<List<UserMessages>> {

        getAllFriendsFromFirebase()
        return allFriends
    }

    private fun getAllFriendsFromFirebase() {

        isLoading.value = true

        db.collection("Messages")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->

                if (querySnapshot != null) {
                    friendsList.clear()

                    for (documents in querySnapshot.documents) {
                        friendsList.add(documents.toObject(UserMessages::class.java)!!)
                    }
                    isLoading.value = false
                    allFriends.value = friendsList
                }

                if (firebaseFirestoreException != null) {
                    isLoading.value = false
                    isError.value = firebaseFirestoreException.message
                }

            }

    }

    fun loading(): MutableLiveData<Boolean> {

        return isLoading
    }

    fun error(): MutableLiveData<String> {
        return isError
    }


}