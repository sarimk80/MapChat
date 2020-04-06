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

    }


    suspend fun suspendGetSingleUser(uniqueId: String): Users? {


        return try {
            val data = db.collection("Users").document(uniqueId).get().await()
            data.toObject(Users::class.java)

        } catch (e: FirebaseException) {
            null
        }

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

    }


    suspend fun getAllMessagesFromFriend(uniqueId: String): List<Messages>? {

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

    }

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
            false
        }

    }

    suspend fun sendUserDataToDataBase(
        uniqueId: String,
        userMessages: UserMessages
    ): Boolean {

        return try {
            db.collection("Messages").document(uniqueId).set(userMessages).await()
            true
        } catch (e: FirebaseException) {
            false
        }
    }


    //TODO: CHANGE USRDID TO ACCICODE
    suspend fun getAllFriends(userId: String): List<UserMessages>? {

        val friendList = ArrayList<UserMessages>()

        return try {
            val snapshot =
                db.collection("Messages")
                    .whereEqualTo("userId", userId).orderBy("date", Query.Direction.ASCENDING).get()
                    .await()
            friendList.clear()
            for (documents in snapshot.documents) {
                friendList.add(documents.toObject(UserMessages::class.java)!!)
            }

            friendList
        } catch (e: FirebaseFirestoreException) {

            null
        }

    }

}