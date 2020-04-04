package com.example.mapchat.view_model


import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapchat.model.Messages
import com.example.mapchat.model.UserMessages
import com.example.mapchat.model.Users
import com.example.mapchat.repository.FirebaseRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class ChatViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

    private val db = Firebase.firestore

    private val singleUser = MutableLiveData<Users>()
    private val messageList = MutableLiveData<List<Messages>>()
    private var message = ArrayList<Messages>()
    private val isLoading = MutableLiveData<Boolean>()
    private val isError = MutableLiveData<String>()
    private val sendMessage = MutableLiveData<Boolean>()
    private val isSendUserUpdated = MutableLiveData<Boolean>()


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

    //TODO DELETE WEH REFACTORING
    // get single user
    fun getSingleUser(userId: String): MutableLiveData<Users> {

        isLoading.value = false

        return firebaseRepository.getSingleUser(userId)

    }

    fun getAllMessages(uniqueId: String): MutableLiveData<List<Messages>> {
        isLoading.value = true
        viewModelScope.launch {
            try {
                firebaseRepository.getAllMessages(uniqueId).collect { message ->
                    messageList.value = message
                }
                // messageList.value = firebaseRepository.getAllMessagesFromFriend(uniqueId)
                isLoading.value = false
            } catch (e: FirebaseFirestoreException) {
                isError.value = e.message
                isLoading.value = false
            }
        }
        return messageList
    }


    fun updateMessages(uniqueId: String, messages: Messages): MutableLiveData<Boolean> {
        // sendMessageToFireBase(uniqueId, messages)
        isLoading.value = true
        viewModelScope.launch {
            try {
                sendMessage.value = firebaseRepository.sendMessageToDataBase(uniqueId, messages)
                isLoading.value = false
            } catch (e: FirebaseFirestoreException) {
                isLoading.value = false
                isError.value = e.message
            } finally {
                isLoading.value = false
            }
        }
        return sendMessage
    }

    fun updateSendUserData(uniqueId: String, userMessages: UserMessages): MutableLiveData<Boolean> {
        // sendUserData(uniqueId, userMessages)
        isLoading.value = true
        viewModelScope.launch {
            try {
                isSendUserUpdated.value =
                    firebaseRepository.sendUserDataToDataBase(uniqueId, userMessages)
                isLoading.value = false
            } catch (e: FirebaseFirestoreException) {
                isLoading.value = false
                isError.value = e.message
            } finally {
                isLoading.value = false
            }
        }

        return isSendUserUpdated
    }


    //TODO:DELETE WHEN REFACTORING
    private fun getFirebaseSingleUser(usesId: String) {
        db.collection("Users").document(usesId)
            .addSnapshotListener { snapshot, error ->


                if (snapshot != null) {
                    singleUser.value = snapshot.toObject(Users::class.java)
                } else {
                    Log.d("ChatViewModel", error.toString())
                }


            }
    }

    //TODO:DELETE WHEN REFACTORING
    private fun getFirebaseAllMessages(uniqueId: String) {

        isLoading.value = true

        db.collection("Messages").document(uniqueId).collection("PrivateMessage")
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->

                if (snapshot != null) {
                    message.clear()
                    for (documents in snapshot.documents) {
                        message.add(documents.toObject(Messages::class.java)!!)
                    }
                    isLoading.value = false
                    messageList.value = message
                } else {
                    isLoading.value = false
                    isError.value = "No Messages"
                }

                if (error != null) {
                    isLoading.value = false
                    isError.value = error.message
                }
            }
    }

    //TODO:DELETE WHEN REFACTORING
    private fun sendMessageToFireBase(uniqueId: String, messages: Messages) {
        db.collection("Messages").document(uniqueId).collection("PrivateMessage").document()
            .set(messages).addOnCompleteListener {
                sendMessage.value = true
            }.addOnFailureListener {
                sendMessage.value = false
                isError.value = it.message
            }
    }

    //TODO:DELETE WHEN REFACTORING
    private fun sendUserData(uniqueId: String, userMessages: UserMessages) {
        db.collection("Messages").document(uniqueId)
            .set(userMessages).addOnCompleteListener {
                isSendUserUpdated.value = true
            }
            .addOnFailureListener {
                isSendUserUpdated.value = false
            }
    }


    fun error(): MutableLiveData<String> {
        return isError
    }

    fun loading(): MutableLiveData<Boolean> {
        return isLoading
    }
}