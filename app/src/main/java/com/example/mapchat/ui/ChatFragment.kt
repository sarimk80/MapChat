package com.example.mapchat.ui


import android.annotation.SuppressLint
import android.location.Geocoder
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
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mapchat.R
import com.example.mapchat.adapters.MessageAdapter
import com.example.mapchat.adapters.MessageDecoration
import com.example.mapchat.databinding.FragmentChatBinding
import com.example.mapchat.model.Messages
import com.example.mapchat.model.UserMessages
import com.example.mapchat.model.Users
import com.example.mapchat.view_model.ChatViewModel
import com.google.firebase.auth.FirebaseAuth
import convertToAscii

import org.koin.android.ext.android.inject
import java.text.SimpleDateFormat
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class ChatFragment : Fragment() {

    private lateinit var fragmentChatBinding: FragmentChatBinding
    private lateinit var charViewModel: ChatViewModel
    private val mAuth: FirebaseAuth by inject()
    private lateinit var geocoder: Geocoder
    private var asciiCode: String = ""
    private var friendId: String = ""
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var friendUser: Users

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

        charViewModel =
            ViewModelProvider(this, defaultViewModelProviderFactory).get(ChatViewModel::class.java)

        geocoder = Geocoder(context, Locale.getDefault())

        return fragmentChatBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        asciiCode = convertToAscii(mAuth.uid, arguments?.getString("FriendId"))
        friendId = arguments?.getString("FriendId")!!

        linearLayoutManager = LinearLayoutManager(context)
        linearLayoutManager.orientation = LinearLayoutManager.VERTICAL
        linearLayoutManager.stackFromEnd = true
        linearLayoutManager.reverseLayout = true
        linearLayoutManager.isSmoothScrollbarEnabled = true
        recyclerView!!.layoutManager = linearLayoutManager
        recyclerView!!.addItemDecoration(MessageDecoration())

        charViewModel.getSingleUser(friendId)
            .observe(viewLifecycleOwner, Observer { user ->

                fragmentChatBinding.user = user
                Glide.with(this).load(user.imageUrl).placeholder(R.drawable.ic_person_black_24dp)
                    .error(R.drawable.ic_person_black_24dp).into(fragmentChatBinding.imgFriend)

//                val address = geocoder.getFromLocation(user.latitude!!, user.longitude!!, 1)

                fragmentChatBinding.city = getCity(user.latitude, user.longitude)

                if (user != null) {
                    friendUser = user
                }
            })


        charViewModel.getAllMessages(asciiCode)
            .observe(viewLifecycleOwner,
                Observer { data ->

                    if (data.isEmpty()) {
                        Log.d("ChatFragment", data.size.toString())
                    } else {
                        messageAdapter = MessageAdapter(context!!, data, mAuth.uid!!)

                        recyclerView!!.adapter = messageAdapter
                        recyclerView!!.smoothScrollToPosition(messageAdapter.itemCount + 1)
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
            Log.d("ChatFragment", error)
        })


    }


    private fun getCity(latitude: Double?, longitude: Double?): String {

        val city: String
        city = try {
            val address = geocoder.getFromLocation(latitude!!, longitude!!, 1)
            address[0].locality + ", " + address[0].countryName
        } catch (e: Throwable) {
            "Earth"
        }


        return city
    }

    @SuppressLint("SimpleDateFormat")
    private fun sendMessageToDataBase() {

        if (fragmentChatBinding.edtMessage.text.isNotEmpty()) {

            val formatter = SimpleDateFormat("MM/dd/yy HH:mm aa")

            val messages =
                Messages(
                    mAuth.uid,
                    friendId,
                    fragmentChatBinding.edtMessage.text.toString(),
                    formatter.format(Calendar.getInstance().time)
                )

            val friendUser = UserMessages(friendUser, formatter.format(Calendar.getInstance().time))
            charViewModel.updateMessages(asciiCode, messages)
                .observe(viewLifecycleOwner, Observer { value ->

                    if (value == true) {
                        charViewModel.updateSendUserData(asciiCode, friendUser)
                            .observe(viewLifecycleOwner,
                                Observer {})
                    }


                })

        }


        fragmentChatBinding.edtMessage.text.clear()

    }

}


