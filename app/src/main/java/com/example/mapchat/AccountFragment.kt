package com.example.mapchat

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.mapchat.databinding.FragmentAccountBinding
import com.example.mapchat.model.Users
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject


class AccountFragment : Fragment() {

    private lateinit var accountBinding: FragmentAccountBinding
    private val mAuth: FirebaseAuth by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        accountBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_account, container, false)

        accountBinding.toolbarAccount.setNavigationOnClickListener {
            it.findNavController().navigate(R.id.action_accountFragment_to_mapFragment)
        }

        return accountBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        accountBinding.user = Users(
            mAuth.uid!!,
            mAuth.currentUser!!.displayName,
            mAuth.currentUser!!.email,
            mAuth.currentUser!!.phoneNumber,
            listOf(),
            mAuth.currentUser!!.photoUrl.toString(),
            0.0,
            0.0
        )
    }

}