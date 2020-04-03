package com.example.mapchat.view_model


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapchat.model.Users
import com.example.mapchat.repository.FirebaseRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class MapViewModel(private val firebaseRepository: FirebaseRepository) : ViewModel() {

    val db = Firebase.firestore

    private val isUploaded = MutableLiveData<Boolean>()
    private val isLoading = MutableLiveData<Boolean>()
    private val isError = MutableLiveData<String>()
    private val usersList = MutableLiveData<List<Users>>()
    private val singleUser = MutableLiveData<Users>()
    private val isFriend = MutableLiveData<Boolean>()
    private var users = ArrayList<Users>()

    //Upload User information
    fun getUploadedResult(
        mAuth: FirebaseAuth,
        latitude: Double?,
        longitude: Double?
    ): MutableLiveData<Boolean> {


        return firebaseRepository.uploadDataToFirebase(mAuth, latitude, longitude)
    }

    //TODO: DELETE in refactoring
    fun updateUserFriendList(userId: String, friendId: List<String>): MutableLiveData<Boolean> {
        updateFriendList(userId, friendId)
        return isFriend
    }

    // get single user
    //TODO: DELETE in refactoring
    fun getSingleUser(userId: String): MutableLiveData<Users> {

        //getFirebaseSingleUser(userId)
        return firebaseRepository.getSingleUser(userId)
    }

    // get list of users
    fun getUserList(): MutableLiveData<List<Users>> {

        //getSuspendUsersList()
        return firebaseRepository.getUserList()
    }

    // loading indicator
    fun loading(): LiveData<Boolean> {
        return isLoading
    }

    // error indicator
    fun error(): LiveData<String> {
        return isError
    }


    // TODO: NOT using delete in refactoring
    private fun updateFriendList(userId: String, friendId: List<String>) {

        val updateRef = db.collection("Users").document(userId)
        db.runTransaction { transaction ->

            transaction.update(updateRef, "friends", friendId)

        }.addOnCompleteListener {
            isFriend.value = true
        }.addOnFailureListener {
            isFriend.value = false
        }
    }


    //TODO: DELETE in refactoring
    private fun getFirebaseSingleUser(usesId: String) {
        db.collection("Users").document(usesId)
            .addSnapshotListener { snapshot, error ->


                if (snapshot != null) {
                    singleUser.value = snapshot.toObject(Users::class.java)
                } else {
                    isError.value = "Error"
                }


            }
    }

    //TODO: DELETE in refactoring
    private fun getSuspendUsersList() {

        isLoading.value = true

        db.collection("Users").addSnapshotListener { snapshot, e ->
            if (snapshot!!.isEmpty) {
                isError.value = "Empty"
                isLoading.value = false
            } else {

                for (documents in snapshot.documents) {
                    users.add(documents.toObject(Users::class.java)!!)


                }
                usersList.value = users



                isLoading.value = false

            }

            if (e != null) {
                isLoading.value = false
                isError.value = "Error ${e.code}"
            }


        }


    }

    //TODO: DELETE in refactoring
    private fun uploadData(mAuth: FirebaseAuth, latitude: Double?, longitude: Double?) {

        isLoading.value = true

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

                isLoading.value = false
            }.addOnFailureListener { e ->
                isError.value = e.message
            }


    }


}