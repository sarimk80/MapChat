package com.example.mapchat.ui


import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mapchat.R
import com.example.mapchat.adapters.MessageAdapter
import com.example.mapchat.adapters.MessageDecoration
import com.example.mapchat.databinding.FragmentChatBinding
import com.example.mapchat.helper.getCity
import com.example.mapchat.model.Messages
import com.example.mapchat.view_model.ChatViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import convertToAscii

import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {

    private lateinit var fragmentChatBinding: FragmentChatBinding
    private val charViewModel: ChatViewModel by viewModel()
    private val mAuth: FirebaseAuth by inject()
    private var asciiCode: String = ""
    private var friendId: String = ""
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var messageAdapter: MessageAdapter
//    private lateinit var friendUser: Users
//    private lateinit var currentUser: Users

    private var recyclerView: RecyclerView? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        fragmentChatBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_chat, null, false
        )
        (activity as AppCompatActivity).setSupportActionBar(fragmentChatBinding.toolbarChat)

        recyclerView = fragmentChatBinding.root.findViewById(R.id.recycler_view) as RecyclerView

        fragmentChatBinding.toolbarChat.setNavigationOnClickListener {
            it.findNavController().navigate(R.id.action_chatFragment_to_mapFragment)
        }


        return fragmentChatBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        asciiCode = convertToAscii(mAuth.uid, arguments?.getString("FriendId"))
        friendId = arguments?.getString("FriendId")!!

        linearLayoutManager = LinearLayoutManager(context)
        //linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.addItemDecoration(MessageDecoration(10, 10, 10))

        charViewModel.getNewSingleUser(friendId)
            .observe(viewLifecycleOwner, Observer { user ->

                if (user != null) {
                    // friendUser = user
                    fragmentChatBinding.user = user
                    Glide.with(this).load(user.imageUrl)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.ic_person_black_24dp)
                        .error(R.drawable.ic_person_black_24dp).into(fragmentChatBinding.imgFriend)


                    fragmentChatBinding.city = getCity(user.latitude, user.longitude, context!!)

                }
            })



        charViewModel.getAllMessages(asciiCode)
            .observe(viewLifecycleOwner,
                Observer { data ->

                    if (data.isEmpty()) {
                        Snackbar.make(
                            fragmentChatBinding.root,
                            "Start Conversation",
                            Snackbar.LENGTH_LONG
                        ).show()
                    } else {
                        messageAdapter = MessageAdapter(context!!, data, mAuth.uid!!)
                        recyclerView!!.adapter = messageAdapter

                        recyclerView!!.scrollToPosition(0)
                        messageAdapter.notifyDataSetChanged()

                    }
                })



        fragmentChatBinding.sendMessage.setOnClickListener {

            sendMessageToDataBase()
        }

        fragmentChatBinding.edtMessage.setOnEditorActionListener { _, actionId, _ ->

            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    sendMessageToDataBase()
                    true
                }
                else -> false
            }
        }





        charViewModel.loading().observe(viewLifecycleOwner, Observer { isLoading ->
            if (isLoading) {
                fragmentChatBinding.progressCircular.visibility = View.VISIBLE
            } else {
                fragmentChatBinding.progressCircular.visibility = View.INVISIBLE
            }
        })


        charViewModel.error().observe(viewLifecycleOwner, Observer { error ->
            Snackbar.make(fragmentChatBinding.root, error, Snackbar.LENGTH_SHORT).show()
        })


    }


    @SuppressLint("SimpleDateFormat")
    private fun sendMessageToDataBase() {

        if (fragmentChatBinding.edtMessage.text.isNotEmpty()) {


            val messages =
                Messages(
                    mAuth.uid,
                    friendId,
                    fragmentChatBinding.edtMessage.text.toString(),
                    System.currentTimeMillis().toString(),
                    mAuth.currentUser?.photoUrl!!.toString()
                )

            charViewModel.updateMessages(asciiCode, messages)
                .observe(viewLifecycleOwner, Observer {})

        }


        fragmentChatBinding.edtMessage.text.clear()

    }

    override fun onDetach() {
        super.onDetach()
        fragmentChatBinding.unbind()
    }
}


