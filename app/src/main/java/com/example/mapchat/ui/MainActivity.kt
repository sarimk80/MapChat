package com.example.mapchat.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.example.mapchat.R
import com.example.mapchat.databinding.ActivityMainBinding
import com.example.mapchat.event.MainActivityEvent
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {


    private val mAuth: FirebaseAuth by inject()
    private lateinit var googleSignClient: GoogleSignInClient
    private val googleSignInOptions: GoogleSignInOptions by inject()
    private val GOOGLE_CODE = 1000
    private lateinit var binding: ActivityMainBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)


        googleSignClient = GoogleSignIn.getClient(this, googleSignInOptions)



        binding.event = object : MainActivityEvent {
            override fun signIn() {
                SignIn()
            }

        }

        binding.executePendingBindings()


    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser

        updateUi(currentUser)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                updateUi(null)
            }


        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)

        mAuth.signInWithCredential(credential).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                updateUi(mAuth.currentUser)
            } else {
                Snackbar.make(binding.root, "Sign in failed", Snackbar.LENGTH_LONG).show()

            }
        }
    }

    private fun SignIn() {
        val signInIntent = googleSignClient.signInIntent

        startActivityForResult(signInIntent, GOOGLE_CODE)
    }

    private fun updateUi(user: FirebaseUser?) {
        if (user != null) {
            val intent = Intent(this, Home::class.java)
            startActivity(intent)
            finish()
        } else {

        }
    }
}
