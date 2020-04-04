package com.example.mapchat.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.example.mapchat.model.Messages
import com.example.mapchat.model.UserMessages
import com.example.mapchat.model.Users
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class FirebaseRepository(private val db: FirebaseFirestore) {


    suspend fun uploadDataToFirebase(
        mAuth: FirebaseAuth,
        latitude: Double?,
        longitude: Double?
    ): Boolean {

        return try {
            val snapshot = db.collection("Users").document(mAuth.currentUser!!.uid).set(
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
            ).await()
            true
        } catch (e: FirebaseException) {
            false
        }

//        db.collection("Users").document(mAuth.currentUser!!.uid)
//            .set(
//                Users(
//                    mAuth.currentUser!!.uid,
//                    mAuth.currentUser!!.displayName,
//                    mAuth.currentUser!!.email,
//                    mAuth.currentUser!!.phoneNumber,
//                    listOf(),
//                    mAuth.currentUser!!.photoUrl.toString(),
//                    latitude,
//                    longitude
//                )
//            ).addOnCompleteListener { reference ->
//                isUploaded.value = true
//                // isLoading.value = false
//            }.addOnFailureListener { e ->
//                // isError.value = e.message
//                isUploaded.value = false
//            }


    }


    suspend fun suspendGetSingleUser(uniqueId: String): Users? {


        return try {
            val data = db.collection("Users").document(uniqueId).get().await()
            data.toObject(Users::class.java)

        } catch (e: FirebaseException) {
            Log.d("Repository", e.message!!)
            null
        }

    }

    fun getSingleUser(userId: String): MutableLiveData<Users> {
        val singleUser = MutableLiveData<Users>()


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


    suspend fun getUserList(): List<Users>? {

        val userList = ArrayList<Users>()

        return try {
            val snapshot = db.collection("Users").get().await()
            userList.clear()
            for (documents in snapshot.documents) {
                userList.add(documents.toObject(Users::class.java)!!)
            }
            userList
        } catch (e: FirebaseException) {
            Log.d("Repository", e.message!!)
            null
        }

//        db.collection("Users").addSnapshotListener { snapshot, e ->
//            if (snapshot!!.isEmpty) {
//                Log.d("FirebaseRepository", "getUserList: ")
//            } else {
//
//                for (documents in snapshot.documents) {
//                    userList.add(documents.toObject(Users::class.java)!!)
//
//
//                }
//                userMutableList.value = userList
//
//
//            }
//
//            if (e != null) {
//                Log.d("FirebaseRepository", "getUserList: ")
//            }
//
//
//        }

    }


    suspend fun getAllMessagesFromFriend(uniqueId: String): List<Messages>? {

        val messagesMutableList = MutableLiveData<List<Messages>>()
        val messagesList = ArrayList<Messages>()

        return try {
            val snapshot = db.collection("Messages").document(uniqueId).collection("PrivateMessage")
                .orderBy("date", Query.Direction.DESCENDING).get().await()
            messagesList.clear()
            for (documents in snapshot.documents) {
                messagesList.add(documents.toObject(Messages::class.java)!!)
            }
            messagesList
        } catch (e: FirebaseException) {
            Log.d("Repository", e.message!!)
            null
        }

//        db.collection("Messages").document(uniqueId).collection("PrivateMessage")
//            .orderBy("date", Query.Direction.DESCENDING)
//            .addSnapshotListener { snapshot, error ->
//
//                if (snapshot != null) {
//                    messagesList.clear()
//                    for (documents in snapshot.documents) {
//                        messagesList.add(documents.toObject(Messages::class.java)!!)
//                    }
//                    //isLoading.value = false
//                    messagesMutableList.value = messagesList
//                } else {
////                    isLoading.value = false
////                    isError.value = "No Messages"
//                }
//
//                if (error != null) {
////                    isLoading.value = false
////                    isError.value = error.message
//                }
//            }


    }

//
//    private fun returnListOfMessages(uniqueId: String): ArrayList<Messages> {
//        val messageList = ArrayList<Messages>()
//
//        db.collection("Messages").document(uniqueId).collection("PrivateMessage")
//            .orderBy("date", Query.Direction.DESCENDING)
//            .addSnapshotListener { querySnapshot, _ ->
//
//                if (querySnapshot != null) {
//
//                    messageList.clear()
//                    for (documents in querySnapshot.documents) {
//                        messageList.add(documents.toObject(Messages::class.java)!!)
//                    }
//
//                }
//
//
//            }
//
//        return messageList
//    }

    fun getAllMessages(uniqueId: String): Flow<List<Messages>> = callbackFlow {

        val messageList = ArrayList<Messages>()

        val snapshot = db.collection("Messages").document(uniqueId).collection("PrivateMessage")
            .orderBy("date", Query.Direction.DESCENDING)


        val subscription = snapshot.addSnapshotListener { querySnapshot, _ ->
            if (querySnapshot != null) {
                messageList.clear()
                for (documents in querySnapshot.documents) {
                    messageList.add(documents.toObject(Messages::class.java)!!)
                }
                offer(messageList)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun sendMessageToDataBase(uniqueId: String, messages: Messages): Boolean {

        return try {
            db.collection("Messages").document(uniqueId).collection("PrivateMessage").document()
                .set(messages).await()
            true
        } catch (e: FirebaseException) {
            Log.d("Repository", e.message!!)
            false
        }

//        db.collection("Messages").document(uniqueId).collection("PrivateMessage").document()
//            .set(messages).addOnCompleteListener {
//                isSend.value = true
//            }.addOnFailureListener {
//                isSend.value = false
//
//            }

        return true

    }

    suspend fun sendUserDataToDataBase(
        uniqueId: String,
        userMessages: UserMessages
    ): Boolean {

        //val isSend = MutableLiveData<Boolean>()

//        db.collection("Messages").document(uniqueId)
//            .set(userMessages).addOnCompleteListener {
//                isSend.value = true
//            }
//            .addOnFailureListener {
//                isSend.value = false
//            }

        return try {
            db.collection("Messages").document(uniqueId).set(userMessages).await()
            true
        } catch (e: FirebaseException) {
            Log.d("Repository", e.message!!)
            false
        }
    }


    suspend fun getAllFriends(): List<UserMessages>? {

        //val friendMutableList = MutableLiveData<List<UserMessages>>()
        val friendList = ArrayList<UserMessages>()

        return try {
            val snapshot =
                db.collection("Messages").orderBy("date", Query.Direction.ASCENDING).get().await()
            friendList.clear()
            for (documents in snapshot.documents) {
                friendList.add(documents.toObject(UserMessages::class.java)!!)
            }

            friendList
        } catch (e: FirebaseFirestoreException) {
            Log.d("Repository", e.message!!)
            null
        }


//
//        db.collection("Messages").orderBy("date", Query.Direction.ASCENDING)
//            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
//
//                if (querySnapshot != null) {
//                    friendList.clear()
//
//                    for (documents in querySnapshot.documents) {
//                        friendList.add(documents.toObject(UserMessages::class.java)!!)
//                    }
//
//                    friendMutableList.value = friendList
//                }
//
//                if (firebaseFirestoreException != null) {
//
//                }
//
//            }


    }


}