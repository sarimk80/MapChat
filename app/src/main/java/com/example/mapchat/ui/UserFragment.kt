package com.example.mapchat.ui


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.mapchat.R
import com.example.mapchat.databinding.FragmentUserBinding
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject

/**
 * A simple [Fragment] subclass.
 */
class UserFragment : Fragment() {

    private lateinit var fragmentUserBinding: FragmentUserBinding
    private val mAuth: FirebaseAuth by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentUserBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_user, null, false)

        (activity as AppCompatActivity).setSupportActionBar(fragmentUserBinding.toolbarUser)
        fragmentUserBinding.toolbarUser.title = mAuth.currentUser?.displayName

        fragmentUserBinding.toolbarUser.setNavigationOnClickListener {
            it.findNavController().navigate(R.id.action_userFragment_to_mapFragment)
        }


        // Inflate the layout for this fragment
        return fragmentUserBinding.root
    }


}
