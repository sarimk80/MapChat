package com.example.mapchat.view_model

import android.app.DownloadManager
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapchat.model.UserMessages
import com.example.mapchat.repository.FirebaseRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch

class UserViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

    private val db = Firebase.firestore

    private val allFriends = MutableLiveData<List<UserMessages>>()
    private val friendsList = ArrayList<UserMessages>()
    private val isLoading = MutableLiveData<Boolean>()
    private val isError = MutableLiveData<String>()


    fun getAllFriends(): MutableLiveData<List<UserMessages>> {

        isLoading.value = true

        viewModelScope.launch {
            try {
                allFriends.value = firebaseRepository.getAllFriends()
                isLoading.value = false
            } catch (e: FirebaseFirestoreException) {
                isError.value = e.message
                isLoading.value = false
            } finally {
                isLoading.value = false
            }
        }


        return allFriends
    }

    //TODO: DELETE WHEN REFACTORING
    private fun getAllFriendsFromFirebase() {

        isLoading.value = true

        db.collection("Messages").orderBy("date", Query.Direction.ASCENDING)
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