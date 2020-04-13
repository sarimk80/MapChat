package com.example.mapchat.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapchat.model.UserMessages
import com.example.mapchat.model.Users
import com.example.mapchat.repository.FirebaseRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class MapViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

    val db = Firebase.firestore

    private val isUploaded = MutableLiveData<Boolean>()
    private val isLoading = MutableLiveData<Boolean>()
    private val isError = MutableLiveData<String>()
    private val usersList = MutableLiveData<List<Users>>()
    private val friendsList = MutableLiveData<List<UserMessages>>()
    private val singleUser = MutableLiveData<Users>()
    private val isFriendRead = MutableLiveData<Boolean>()
    private var users = ArrayList<Users>()


    fun messageRead(myId: String): MutableLiveData<Boolean> {

        viewModelScope.launch {
            try {
                firebaseRepository.getReadMessages(myId).collect { value ->
                    isFriendRead.value = value
                }
            } catch (e: FirebaseFirestoreException) {
                isError.value = e.message
            }
        }
        return isFriendRead
    }

    //Upload User information
    fun getUploadedResult(
        mAuth: FirebaseAuth,
        latitude: Double?,
        longitude: Double?
    ): MutableLiveData<Boolean> {

        isLoading.value = true
        viewModelScope.launch {
            try {
                isUploaded.value =
                    firebaseRepository.uploadDataToFirebase(mAuth, latitude, longitude)
                isLoading.value = false
            } catch (e: FirebaseFirestoreException) {
                isError.value = e.message
                isLoading.value = false
            }
        }
        return isUploaded
    }


    // get list of users
    fun getUserList(): MutableLiveData<List<Users>> {

        isLoading.value = true
        viewModelScope.launch {
            try {
                usersList.value = firebaseRepository.getUserList()
                isLoading.value = false
            } catch (e: FirebaseException) {
                isLoading.value = false
                isError.value = e.message
            } finally {
                isLoading.value = false
            }
        }
        return usersList
    }

    // loading indicator
    fun loading(): LiveData<Boolean> {
        return isLoading
    }

    // error indicator
    fun error(): LiveData<String> {
        return isError
    }


}