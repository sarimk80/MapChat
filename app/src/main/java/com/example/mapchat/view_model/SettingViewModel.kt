package com.example.mapchat.view_model

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapchat.model.Users
import com.example.mapchat.repository.FirebaseRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.launch

class SettingViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

    private val isLoading = MutableLiveData<Boolean>()
    private val isError = MutableLiveData<String>()
    private val singleUser = MutableLiveData<Users>()
    private val link = MutableLiveData<String>()

    fun getCoroutineSingleUser(uniqueId: String): MutableLiveData<Users> {

        isLoading.value = true
        viewModelScope.launch {
            try {
                singleUser.value = firebaseRepository.suspendGetSingleUser(uniqueId)
                isLoading.value = false
            } catch (e: FirebaseFirestoreException) {
                isError.value = e.message
                isLoading.value = false
            } finally {
                isLoading.value = false
            }
        }

        return singleUser
    }


    fun getLink(): MutableLiveData<String> {

        isLoading.value = true
        viewModelScope.launch {
            link.value = firebaseRepository.playLink()
            isLoading.value = false
        }
        return link
    }

    fun isLoading(): MutableLiveData<Boolean> {
        return isLoading
    }
}