package com.example.mapchat.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.example.mapchat.R

class Home : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        intent.extras?.let {
            for (key in it.keySet()) {
                val value = intent.extras?.get(key)
                Log.d("HomeActivity", "Key: $key Value: $value")
                findNavController(R.layout.activity_home).navigate(
                    R.id.action_mapFragment_to_chatFragment,
                    bundleOf("FriendId" to value.toString())
                )
            }
        }
    }
}
